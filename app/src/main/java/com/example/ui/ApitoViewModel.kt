package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ApitoRepository
import com.example.data.AppDatabase
import com.example.data.MatchEntity
import com.example.data.MatchEventEntity
import com.example.data.ProfileEntity
import com.example.data.ReviewEntity
import com.example.data.TransactionEntity
import com.example.data.abacatepay.AbacateBillingData
import com.example.data.abacatepay.AbacatePayClient
import com.example.util.ApitoSoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ApitoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ApitoRepository(AppDatabase.getDatabase(application))
    private val abacateClient = AbacatePayClient()
    private val prefs = application.getSharedPreferences("apito_auth_prefs", Context.MODE_PRIVATE)

    val isCreatingBilling = MutableStateFlow(false)
    val billingErrorMessage = MutableStateFlow<String?>(null)

    val isSyncingSupabase = MutableStateFlow(false)
    val supabaseSyncMessage = MutableStateFlow<String?>(null)

    val authLoading = MutableStateFlow(false)
    val authError = MutableStateFlow<String?>(null)
    val isAuthenticated = MutableStateFlow(prefs.getString("user_id", null) != null)

    fun hasAbacateApiKey(): Boolean = abacateClient.hasConfiguredApiKey()
    fun hasSupabaseKey(): Boolean = repository.hasSupabaseKey()

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            authError.value = "Por favor, preencha todos os campos."
            return
        }
        authLoading.value = true
        authError.value = null
        viewModelScope.launch {
            val result = repository.signIn(email.trim(), pass)
            authLoading.value = false
            result.fold(
                onSuccess = { profile ->
                    prefs.edit()
                        .putString("user_id", profile.id)
                        .putString("user_email", profile.email)
                        .putString("user_role", profile.role)
                        .apply()
                    _currentProfile.value = profile
                    _selectedRole.value = profile.role
                    isAuthenticated.value = true
                    loadTransactions(profile.id)
                    onSuccess()
                },
                onFailure = { err ->
                    authError.value = err.message ?: "Falha ao entrar. Verifique suas credenciais."
                }
            )
        }
    }

    fun register(
        email: String,
        pass: String,
        fullName: String,
        role: String,
        phone: String = "",
        city: String = "",
        onSuccess: () -> Unit
    ) {
        if (email.isBlank() || pass.isBlank() || fullName.isBlank()) {
            authError.value = "Nome, e-mail e senha são obrigatórios."
            return
        }
        if (pass.length < 6) {
            authError.value = "A senha deve conter no mínimo 6 caracteres."
            return
        }
        authLoading.value = true
        authError.value = null
        viewModelScope.launch {
            val result = repository.signUp(
                email = email.trim(),
                password = pass,
                fullName = fullName.trim(),
                role = role,
                phone = phone.trim(),
                city = city.trim()
            )
            authLoading.value = false
            result.fold(
                onSuccess = { profile ->
                    prefs.edit()
                        .putString("user_id", profile.id)
                        .putString("user_email", profile.email)
                        .putString("user_role", profile.role)
                        .apply()
                    _currentProfile.value = profile
                    _selectedRole.value = profile.role
                    isAuthenticated.value = true
                    loadTransactions(profile.id)
                    onSuccess()
                },
                onFailure = { err ->
                    authError.value = err.message ?: "Falha ao criar conta. Tente novamente."
                }
            )
        }
    }

    fun logout(onComplete: () -> Unit) {
        prefs.edit().clear().apply()
        isAuthenticated.value = false
        _currentProfile.value = null
        onComplete()
    }

    fun syncWithSupabase(onComplete: (Boolean, String) -> Unit = { _, _ -> }) {
        isSyncingSupabase.value = true
        supabaseSyncMessage.value = null
        viewModelScope.launch {
            val res = repository.syncWithSupabase(forceClearLocal = true)
            isSyncingSupabase.value = false
            res.fold(
                onSuccess = { count ->
                    val msg = "Sincronizado! $count registros carregados do Supabase."
                    supabaseSyncMessage.value = msg
                    val profile = repository.getFirstProfileByRole(_selectedRole.value)
                    _currentProfile.value = profile
                    onComplete(true, msg)
                },
                onFailure = { err ->
                    val msg = err.message ?: "Falha ao conectar ao Supabase"
                    supabaseSyncMessage.value = msg
                    onComplete(false, msg)
                }
            )
        }
    }

    // Active User Profile & Role
    private val _currentProfile = MutableStateFlow<ProfileEntity?>(null)
    val currentProfile: StateFlow<ProfileEntity?> = _currentProfile.asStateFlow()

    private val _selectedRole = MutableStateFlow<String>("contratante")
    val selectedRole: StateFlow<String> = _selectedRole.asStateFlow()

    // All Referees
    val referees: StateFlow<List<ProfileEntity>> = repository.allReferees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Matches
    val allMatches: StateFlow<List<MatchEntity>> = repository.allMatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Open Matches Available in Real-Time
    val openMatches: StateFlow<List<MatchEntity>> = repository.openMatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Referee Availability Toggle (Uber-like online/offline state)
    // "O árbitro tem que estar disponível para aparecer a partida para ele. Caso não esteja, não irá aparecer."
    val isRefereeAvailable = MutableStateFlow(prefs.getBoolean("referee_is_available", true))

    fun toggleRefereeAvailability(available: Boolean) {
        isRefereeAvailable.value = available
        prefs.edit().putBoolean("referee_is_available", available).apply()
        if (!available) {
            dismissCurrentIncomingOffer()
        } else {
            checkAndTriggerNextIncomingOffer()
        }
    }

    // Incoming Match Offer State for Referees (showing one-by-one with sound)
    val currentIncomingOffer = MutableStateFlow<MatchEntity?>(null)
    val incomingOfferCountdown = MutableStateFlow(30)
    private val dismissedMatchIds = mutableSetOf<String>()
    private var countdownJob: Job? = null

    fun checkAndTriggerNextIncomingOffer() {
        if (_selectedRole.value != "arbitro" || !isRefereeAvailable.value) {
            return
        }
        if (currentIncomingOffer.value != null) return

        val candidate = openMatches.value.firstOrNull {
            it.status == "aberta" && it.refereeId.isBlank() && it.id !in dismissedMatchIds
        }

        if (candidate != null) {
            currentIncomingOffer.value = candidate
            incomingOfferCountdown.value = 30
            ApitoSoundManager.startIncomingMatchSound(viewModelScope)

            countdownJob?.cancel()
            countdownJob = viewModelScope.launch {
                for (s in 30 downTo 1) {
                    incomingOfferCountdown.value = s
                    delay(1000)
                }
                // When 30s expired without accepting: dismiss and show next
                dismissCurrentIncomingOffer()
            }
        }
    }

    fun acceptIncomingOffer(onAccepted: (String) -> Unit) {
        val offer = currentIncomingOffer.value ?: return
        val currentRef = _currentProfile.value
        val refId = currentRef?.id ?: prefs.getString("user_id", null) ?: "ref_${UUID.randomUUID()}"
        val refName = currentRef?.fullName ?: "Árbitro Confirmado"

        countdownJob?.cancel()
        ApitoSoundManager.playAcceptedSound()
        currentIncomingOffer.value = null

        viewModelScope.launch {
            repository.acceptMatchAsReferee(offer.id, refId, refName)
            repository.syncWithSupabase(forceClearLocal = false)
            onAccepted(offer.id)
        }
    }

    fun dismissCurrentIncomingOffer() {
        countdownJob?.cancel()
        ApitoSoundManager.stopSound()
        val currentId = currentIncomingOffer.value?.id
        if (currentId != null) {
            dismissedMatchIds.add(currentId)
        }
        currentIncomingOffer.value = null

        // Check if there is another open match in queue (shows one by one)
        viewModelScope.launch {
            delay(1200)
            checkAndTriggerNextIncomingOffer()
        }
    }

    fun publishMatch(
        location: String,
        address: String,
        date: String,
        time: String,
        modality: String,
        price: Double,
        durationHours: Double,
        level: String,
        onSuccess: (MatchEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        val profile = _currentProfile.value
        val organizerId = profile?.id ?: prefs.getString("user_id", null) ?: UUID.randomUUID().toString()
        val organizerName = profile?.fullName ?: "Contratante"

        viewModelScope.launch {
            try {
                val res = repository.createMatch(
                    organizerId = organizerId,
                    organizerName = organizerName,
                    location = location.trim(),
                    address = address.trim(),
                    date = date.trim(),
                    time = time.trim(),
                    modality = modality,
                    price = price,
                    durationHours = durationHours,
                    level = level
                )
                res.fold(
                    onSuccess = { match ->
                        repository.syncWithSupabase(forceClearLocal = false)
                        onSuccess(match)
                    },
                    onFailure = { err ->
                        onError(err.message ?: "Falha ao criar partida.")
                    }
                )
            } catch (e: Exception) {
                onError(e.message ?: "Falha ao criar partida.")
            }
        }
    }

    // Search & Filter State
    val searchQuery = MutableStateFlow("")
    val selectedModality = MutableStateFlow<String?>(null)
    val selectedEquipment = MutableStateFlow<Set<String>>(emptySet())
    val maxPrice = MutableStateFlow(250f)
    val selectedDateFilter = MutableStateFlow<String?>(null)

    // Filtered Referees
    val filteredReferees: StateFlow<List<ProfileEntity>> = combine(
        referees,
        searchQuery,
        selectedModality,
        selectedEquipment,
        maxPrice
    ) { list, query, modality, equipment, price ->
        list.filter { ref ->
            val matchesQuery = query.isBlank() ||
                    ref.fullName.contains(query, ignoreCase = true) ||
                    ref.city.contains(query, ignoreCase = true)
            val matchesModality = modality == null ||
                    ref.getModalitiesList().any { it.equals(modality, ignoreCase = true) }
            val matchesEquipment = equipment.isEmpty() ||
                    equipment.all { req -> ref.getEquipmentList().any { it.equals(req, ignoreCase = true) } }
            val matchesPrice = ref.hourlyRate <= price
            matchesQuery && matchesModality && matchesEquipment && matchesPrice
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wallet Transactions
    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val transactions: StateFlow<List<TransactionEntity>> = _transactions.asStateFlow()

    // Live Match Tracker State
    private val _activeMatch = MutableStateFlow<MatchEntity?>(null)
    val activeMatch: StateFlow<MatchEntity?> = _activeMatch.asStateFlow()

    private val _activeMatchEvents = MutableStateFlow<List<MatchEventEntity>>(emptyList())
    val activeMatchEvents: StateFlow<List<MatchEventEntity>> = _activeMatchEvents.asStateFlow()

    private val _matchTimerSeconds = MutableStateFlow(0L)
    val matchTimerSeconds: StateFlow<Long> = _matchTimerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _shareGps = MutableStateFlow(false)
    val shareGps: StateFlow<Boolean> = _shareGps.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureSeedData()

            // Restore saved session if user already logged in
            val savedUserId = prefs.getString("user_id", null)
            val savedRole = prefs.getString("user_role", null)

            if (savedUserId != null) {
                val profile = repository.getProfileDirect(savedUserId)
                if (profile != null) {
                    _currentProfile.value = profile
                    _selectedRole.value = profile.role
                    isAuthenticated.value = true
                    loadTransactions(profile.id)
                } else {
                    val initial = repository.getFirstProfileByRole(savedRole ?: "contratante")
                    _currentProfile.value = initial
                    if (initial != null) loadTransactions(initial.id)
                }
            } else {
                val initial = repository.getFirstProfileByRole("contratante")
                _currentProfile.value = initial
                if (initial != null) {
                    loadTransactions(initial.id)
                }
            }

            // Listen to open matches to trigger incoming offers in real-time
            launch {
                repository.openMatches.collect {
                    if (_selectedRole.value == "arbitro" && isRefereeAvailable.value && currentIncomingOffer.value == null) {
                        checkAndTriggerNextIncomingOffer()
                    }
                }
            }

            // Real-time automatic background synchronization loop
            // Silently syncs database changes from Supabase every 5 seconds without requiring user clicks
            while (true) {
                delay(5_000)
                try {
                    repository.syncWithSupabase(forceClearLocal = false)
                } catch (_: Exception) {}
            }
        }
    }

    fun switchRole(role: String) {
        _selectedRole.value = role
        viewModelScope.launch {
            val target = repository.getFirstProfileByRole(role)
            _currentProfile.value = target
            if (target != null) {
                loadTransactions(target.id)
            }
        }
    }

    fun setCustomProfile(profile: ProfileEntity) {
        _currentProfile.value = profile
        _selectedRole.value = profile.role
        loadTransactions(profile.id)
    }

    private fun loadTransactions(userId: String) {
        viewModelScope.launch {
            repository.getTransactions(userId).collect {
                _transactions.value = it
            }
        }
    }

    fun updateProfile(updated: ProfileEntity) {
        viewModelScope.launch {
            repository.updateProfile(updated)
            _currentProfile.value = updated
        }
    }

    // Dynamic Price Calculation
    fun calculatePrice(
        hourlyRate: Double,
        durationHours: Int,
        isSurge: Boolean = false
    ): Triple<Double, Double, Double> {
        val base = hourlyRate * durationHours
        val surgeMultiplier = if (isSurge) 1.2 else 1.0
        val subtotal = base * surgeMultiplier
        val fee = subtotal * 0.10 // 10% platform fee
        val total = subtotal + fee
        return Triple(total, fee, subtotal)
    }

    // Match Creation / Booking
    fun createBooking(
        referee: ProfileEntity,
        date: String,
        time: String,
        location: String,
        modality: String,
        durationHours: Int,
        paymentMethod: String,
        isSurge: Boolean,
        onSuccess: (String) -> Unit
    ) {
        val contractor = _currentProfile.value ?: return
        val (total, fee, _) = calculatePrice(referee.hourlyRate, durationHours, isSurge)

        val match = MatchEntity(
            contractorId = contractor.id,
            contractorName = contractor.fullName,
            refereeId = referee.id,
            refereeName = referee.fullName,
            date = date,
            time = time,
            location = location,
            modality = modality,
            price = total,
            status = "pendente",
            paymentMethod = paymentMethod,
            contractorCheckin = false,
            refereeCheckin = false,
            duration = durationHours,
            platformFee = fee,
            isSurge = isSurge
        )

        viewModelScope.launch {
            repository.createMatch(match)
            onSuccess(match.id)
        }
    }

    // Match Operations
    fun acceptMatch(matchId: String) {
        viewModelScope.launch {
            repository.updateMatchStatus(matchId, "aceita")
        }
    }

    fun rejectMatch(matchId: String) {
        viewModelScope.launch {
            repository.updateMatchStatus(matchId, "cancelada")
        }
    }

    fun loadMatchDetail(matchId: String) {
        viewModelScope.launch {
            repository.getMatch(matchId).collect { match ->
                _activeMatch.value = match
            }
        }
        viewModelScope.launch {
            repository.getMatchEvents(matchId).collect { events ->
                _activeMatchEvents.value = events
            }
        }
    }

    fun toggleContractorCheckin(matchId: String) {
        val current = _activeMatch.value ?: return
        val newCheckin = !current.contractorCheckin
        viewModelScope.launch {
            repository.setContractorCheckin(matchId, newCheckin)
            _activeMatch.value = current.copy(contractorCheckin = newCheckin)
        }
    }

    fun toggleRefereeCheckin(matchId: String) {
        val current = _activeMatch.value ?: return
        val newCheckin = !current.refereeCheckin
        viewModelScope.launch {
            repository.setRefereeCheckin(matchId, newCheckin)
            _activeMatch.value = current.copy(refereeCheckin = newCheckin)
        }
    }

    fun updateMatchStatus(matchId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateMatchStatus(matchId, newStatus)
            _activeMatch.value = _activeMatch.value?.copy(status = newStatus)

            if (newStatus == "em_andamento") {
                startTimer()
            } else if (newStatus == "finalizada" || newStatus == "cancelada") {
                stopTimer()
            }
        }
    }

    fun toggleGpsSharing() {
        _shareGps.value = !_shareGps.value
    }

    // Stopwatch / Cronômetro
    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value) {
                delay(1000)
                _matchTimerSeconds.value += 1
            }
        }
    }

    fun stopTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        _matchTimerSeconds.value = 0
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    // Log occurrence event during match
    fun addMatchEvent(matchId: String, type: String, minute: Int, description: String) {
        viewModelScope.launch {
            repository.addMatchEvent(matchId, type, minute, description)
        }
    }

    // Post-Match Review
    fun submitReview(
        matchId: String,
        targetId: String,
        rating: Float,
        punctuality: Float,
        professionalism: Float,
        comment: String,
        onDone: () -> Unit
    ) {
        val user = _currentProfile.value ?: return
        viewModelScope.launch {
            repository.addReview(
                matchId = matchId,
                reviewerId = user.id,
                reviewerName = user.fullName,
                targetId = targetId,
                rating = rating,
                punctuality = punctuality,
                professionalism = professionalism,
                comment = comment
            )
            onDone()
        }
    }

    fun getReviews(targetId: String) = repository.getReviews(targetId)

    // Wallet actions
    fun withdrawFunds(amount: Double, pixKey: String, onDone: (Boolean) -> Unit) {
        val user = _currentProfile.value ?: return
        viewModelScope.launch {
            repository.withdrawFunds(user.id, amount, pixKey)
            loadTransactions(user.id)
            onDone(true)
        }
    }

    // AbacatePay Gateway Integration
    fun createAbacatePayBilling(
        matchId: String,
        onResult: (AbacateBillingData?) -> Unit = {}
    ) {
        val match = allMatches.value.firstOrNull { it.id == matchId } ?: return
        val contractor = _currentProfile.value
        isCreatingBilling.value = true
        billingErrorMessage.value = null

        viewModelScope.launch {
            val result = abacateClient.createBilling(
                matchId = matchId,
                matchTitle = "Árbitro ${match.modality.uppercase()}: ${match.date} às ${match.time}",
                amountReais = match.price,
                customerName = contractor?.fullName ?: match.contractorName,
                customerEmail = contractor?.email ?: "usuario@apito.com",
                customerPhone = contractor?.phone ?: "11988887777"
            )

            isCreatingBilling.value = false
            result.fold(
                onSuccess = { billing ->
                    repository.updateMatchBilling(
                        matchId = matchId,
                        billingId = billing.id,
                        billingUrl = billing.url,
                        status = billing.status ?: "PENDING"
                    )
                    onResult(billing)
                },
                onFailure = { err ->
                    billingErrorMessage.value = err.message
                    onResult(null)
                }
            )
        }
    }

    fun checkAbacateBillingStatus(
        matchId: String,
        billingId: String,
        onStatus: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = abacateClient.checkBillingStatus(billingId)
            result.onSuccess { status ->
                if (status.equals("PAID", ignoreCase = true)) {
                    updateMatchStatus(matchId, "aceita")
                    repository.updateMatchBilling(matchId, billingId, null, "PAID")
                }
                onStatus(status)
            }
        }
    }

    fun triggerSupabaseWebhook(
        matchId: String,
        billingId: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val res = abacateClient.triggerSupabaseWebhook(matchId, billingId, "PAID")
            onResult(res.getOrDefault(false))
        }
    }

    // Bulk League Matches Import
    fun importLeagueMatches(csvText: String): Pair<Int, Int> {
        val contractor = _currentProfile.value ?: return Pair(0, 0)
        val allRefs = referees.value
        if (allRefs.isEmpty()) return Pair(0, 0)

        var created = 0
        var failed = 0

        val lines = csvText.trim().split("\n")
        viewModelScope.launch {
            for (line in lines) {
                if (line.isBlank() || line.startsWith("#") || line.startsWith("Data")) continue
                val parts = line.split(",").map { it.trim() }
                if (parts.size >= 4) {
                    val date = parts[0]
                    val time = parts[1]
                    val location = parts[2]
                    val modalityRaw = parts[3].lowercase()

                    val modality = when {
                        modalityRaw.contains("futsal") -> "futsal"
                        modalityRaw.contains("society") -> "society"
                        modalityRaw.contains("7") -> "futebol_7"
                        else -> "futebol"
                    }

                    // Smart auto-match referee by modality
                    val matchedRef = allRefs.firstOrNull { ref ->
                        ref.getModalitiesList().any { it.equals(modality, ignoreCase = true) }
                    } ?: allRefs.first()

                    val match = MatchEntity(
                        contractorId = contractor.id,
                        contractorName = contractor.fullName,
                        refereeId = matchedRef.id,
                        refereeName = matchedRef.fullName,
                        date = date,
                        time = time,
                        location = location,
                        modality = modality,
                        price = matchedRef.hourlyRate,
                        status = "pendente",
                        paymentMethod = "saldo",
                        duration = 1,
                        platformFee = matchedRef.hourlyRate * 0.1
                    )
                    repository.createMatch(match)
                    created++
                } else {
                    failed++
                }
            }
        }
        return Pair(created, failed)
    }
}
