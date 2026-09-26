package com.example.data

import com.example.data.supabase.SupabaseClient
import com.example.data.supabase.SupabaseMatch
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ApitoRepository(private val db: AppDatabase) {

    private val supabaseClient = SupabaseClient.getInstance()

    fun hasSupabaseKey(): Boolean = supabaseClient.hasConfiguredKey()

    val allReferees: Flow<List<ProfileEntity>> = db.profileDao().getAllReferees()
    val allMatches: Flow<List<MatchEntity>> = db.matchDao().getAllMatches()
    val openMatches: Flow<List<MatchEntity>> = db.matchDao().getOpenMatches()

    fun getMatchesByContractor(id: String): Flow<List<MatchEntity>> =
        db.matchDao().getMatchesByContractor(id)

    fun getMatchesByReferee(id: String): Flow<List<MatchEntity>> =
        db.matchDao().getMatchesByReferee(id)

    fun getMatch(id: String): Flow<MatchEntity?> = db.matchDao().getMatchById(id)

    suspend fun getMatchDirect(id: String): MatchEntity? = db.matchDao().getMatchDirect(id)

    fun getProfile(id: String): Flow<ProfileEntity?> = db.profileDao().getProfileById(id)

    suspend fun getProfileDirect(id: String): ProfileEntity? = db.profileDao().getProfileDirect(id)

    suspend fun getFirstProfileByRole(role: String): ProfileEntity? =
        db.profileDao().getFirstProfileByRole(role)

    fun getMatchEvents(matchId: String): Flow<List<MatchEventEntity>> =
        db.matchEventDao().getEventsByMatch(matchId)

    fun getReviews(targetId: String): Flow<List<ReviewEntity>> =
        db.reviewDao().getReviewsForTarget(targetId)

    fun getTransactions(userId: String): Flow<List<TransactionEntity>> =
        db.transactionDao().getTransactionsByUser(userId)

    suspend fun insertProfile(profile: ProfileEntity) {
        db.profileDao().insertProfile(profile)
    }

    suspend fun updateProfile(profile: ProfileEntity) {
        db.profileDao().updateProfile(profile)
    }

    suspend fun createMatch(match: MatchEntity) {
        db.matchDao().insertMatch(match)
        // Also log transaction for contractor
        db.transactionDao().insertTransaction(
            TransactionEntity(
                userId = match.contractorId,
                type = "saida",
                amount = match.price,
                description = "Reserva de Árbitro: ${match.modality.uppercase()} (${match.refereeName})"
            )
        )
        if (supabaseClient.hasConfiguredKey()) {
            supabaseClient.createMatch(
                SupabaseMatch(
                    id = match.id,
                    organizer_id = match.contractorId,
                    referee_id = match.refereeId.ifBlank { null },
                    location = match.location,
                    address = match.location,
                    category = match.modality,
                    price = match.price,
                    status = match.status,
                    duration = match.duration.toDouble(),
                    match_date = "${match.date} ${match.time}:00"
                )
            )
        }
    }

    suspend fun updateMatchStatus(matchId: String, status: String) {
        db.matchDao().updateMatchStatus(matchId, status)
        if (status == "finalizada") {
            val match = db.matchDao().getMatchDirect(matchId)
            if (match != null) {
                // Credit referee's wallet
                val netEarning = match.price - match.platformFee
                db.transactionDao().insertTransaction(
                    TransactionEntity(
                        userId = match.refereeId,
                        type = "entrada",
                        amount = netEarning,
                        description = "Pagamento por partida finalizada (${match.modality.uppercase()})"
                    )
                )
            }
        }
        if (supabaseClient.hasConfiguredKey()) {
            supabaseClient.updateMatchStatus(matchId, status)
        }
    }

    suspend fun syncWithSupabase(forceClearLocal: Boolean = false): Result<Int> {
        if (!supabaseClient.hasConfiguredKey()) {
            return Result.failure(IllegalStateException("Sem chave Supabase configurada"))
        }

        var synced = 0
        val profilesRes = supabaseClient.fetchProfiles()
        profilesRes.onSuccess { spProfiles ->
            if (spProfiles.isNotEmpty()) {
                if (forceClearLocal) {
                    db.profileDao().clearProfiles()
                }
                val entities = spProfiles.map { sp ->
                    val isOrganizer = (sp.role ?: "").lowercase() in listOf("organizer", "contratante", "admin")
                    val mappedRole = if (isOrganizer) "contratante" else "arbitro"
                    val ratingVal = if ((sp.rating ?: 0.0) > 0.0) sp.rating!! else 5.0

                    ProfileEntity(
                        id = sp.id,
                        fullName = sp.full_name ?: (if (isOrganizer) "Organizador" else "Árbitro"),
                        email = if (!sp.phone.isNullOrBlank()) "${sp.phone}@apito.com" else "usuario@apito.com",
                        avatarUrl = sp.avatar_url ?: "",
                        role = mappedRole,
                        city = sp.city ?: "São Paulo - SP",
                        phone = sp.phone ?: "",
                        bio = sp.bio ?: (if (isOrganizer) "Organizador esportivo cadastrado no Supabase" else "Árbitro credenciado no Supabase"),
                        modalities = "futebol,society,futsal",
                        equipment = "Apito,Cartões,Cronômetro",
                        hourlyRate = 120.0,
                        level = if ((sp.matches_completed ?: 0) > 10) "ouro" else "prata",
                        gamesCount = sp.matches_completed ?: 0,
                        ratingAvg = ratingVal,
                        isVerified = true,
                        availability = "Sexta,Sábado,Domingo",
                        certifications = "FPF / CBF",
                        contractorType = if (isOrganizer) "Organizador" else "Amador"
                    )
                }
                db.profileDao().insertProfiles(entities)
                synced += entities.size
            }
        }

        val matchesRes = supabaseClient.fetchMatches()
        matchesRes.onSuccess { spMatches ->
            if (spMatches.isNotEmpty()) {
                if (forceClearLocal) {
                    db.matchDao().clearMatches()
                }
                val entities = spMatches.map { sm ->
                    val dateStr = sm.match_date?.split("T")?.firstOrNull() ?: sm.match_date?.split(" ")?.firstOrNull() ?: "Hoje"
                    val timeStr = if (sm.match_date?.contains("T") == true) {
                        sm.match_date.substringAfter("T").take(5)
                    } else if (sm.match_date?.contains(" ") == true) {
                        sm.match_date.substringAfter(" ").take(5)
                    } else "20:00"
                    val loc = if (!sm.location.isNullOrBlank()) sm.location else (sm.address ?: "Arena Esportiva")

                    MatchEntity(
                        id = sm.id ?: UUID.randomUUID().toString(),
                        contractorId = sm.organizer_id ?: "",
                        contractorName = "Organizador",
                        refereeId = sm.referee_id ?: "",
                        refereeName = if (sm.referee_id.isNullOrBlank()) "Aguardando Árbitro" else "Árbitro Confirmado",
                        date = dateStr,
                        time = timeStr,
                        location = loc,
                        modality = sm.category ?: "Society 7",
                        price = sm.price ?: 120.0,
                        status = sm.status ?: "aberta",
                        paymentMethod = "pix",
                        contractorCheckin = false,
                        refereeCheckin = false,
                        duration = sm.duration?.toInt() ?: 1,
                        platformFee = 15.0,
                        isSurge = (sm.price ?: 120.0) > 150.0
                    )
                }
                db.matchDao().insertMatches(entities)
                synced += entities.size
            }
        }

        val walletsRes = supabaseClient.fetchWallets()
        walletsRes.onSuccess { spWallets ->
            if (spWallets.isNotEmpty()) {
                if (forceClearLocal) {
                    db.transactionDao().clearTransactions()
                }
                val entities = spWallets.mapNotNull { sw ->
                    sw.user_id?.let { uid ->
                        TransactionEntity(
                            id = sw.id ?: UUID.randomUUID().toString(),
                            userId = uid,
                            type = "entrada",
                            amount = sw.balance ?: 0.0,
                            description = "Saldo da Carteira Supabase"
                        )
                    }
                }
                if (entities.isNotEmpty()) {
                    db.transactionDao().insertTransactions(entities)
                    synced += entities.size
                }
            }
        }

        return Result.success(synced)
    }

    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        role: String,
        phone: String = "",
        city: String = ""
    ): Result<ProfileEntity> {
        val authResult = supabaseClient.signUp(
            email = email,
            password = password,
            fullName = fullName,
            role = if (role == "contratante") "organizer" else "arbitro",
            phone = phone,
            city = city
        )
        return authResult.fold(
            onSuccess = { resp ->
                val userId = resp.user?.id ?: UUID.randomUUID().toString()
                val profileEntity = ProfileEntity(
                    id = userId,
                    fullName = fullName,
                    email = email,
                    avatarUrl = "https://ui-avatars.com/api/?name=${fullName.replace(" ", "+")}&background=0052FF&color=fff",
                    role = role,
                    city = if (city.isNotBlank()) city else "São Paulo - SP",
                    phone = phone,
                    bio = if (role == "contratante") "Organizador de partidas" else "Árbitro credenciado",
                    modalities = "futebol,society,futsal",
                    equipment = "Apito,Cartões",
                    hourlyRate = 120.0,
                    level = "prata",
                    gamesCount = 0,
                    ratingAvg = 5.0,
                    isVerified = true,
                    availability = "Sábado,Domingo",
                    certifications = "FPF / CBF",
                    contractorType = if (role == "contratante") "Organizador" else "Amador"
                )
                db.profileDao().insertProfile(profileEntity)
                syncWithSupabase(forceClearLocal = false)
                Result.success(profileEntity)
            },
            onFailure = { err ->
                Result.failure(err)
            }
        )
    }

    suspend fun signIn(email: String, password: String): Result<ProfileEntity> {
        val authResult = supabaseClient.signIn(email = email, password = password)
        return authResult.fold(
            onSuccess = { resp ->
                val userId = resp.user?.id ?: ""
                val remoteProfileRes = supabaseClient.fetchProfileById(userId)
                val sp = remoteProfileRes.getOrNull()
                val role = if ((sp?.role ?: "").lowercase() in listOf("organizer", "contratante", "admin")) {
                    "contratante"
                } else {
                    "arbitro"
                }
                val profileEntity = ProfileEntity(
                    id = userId,
                    fullName = sp?.full_name ?: resp.user?.user_metadata?.get("full_name")?.toString() ?: "Usuário",
                    email = email,
                    avatarUrl = sp?.avatar_url ?: "",
                    role = role,
                    city = sp?.city ?: "São Paulo - SP",
                    phone = sp?.phone ?: "",
                    bio = sp?.bio ?: "",
                    modalities = "futebol,society,futsal",
                    equipment = "Apito,Cartões",
                    hourlyRate = 120.0,
                    level = "prata",
                    gamesCount = sp?.matches_completed ?: 0,
                    ratingAvg = if ((sp?.rating ?: 0.0) > 0.0) sp!!.rating!! else 5.0,
                    isVerified = true,
                    availability = "Sábado,Domingo",
                    certifications = "FPF / CBF",
                    contractorType = if (role == "contratante") "Organizador" else "Amador"
                )
                db.profileDao().insertProfile(profileEntity)
                syncWithSupabase(forceClearLocal = false)
                Result.success(profileEntity)
            },
            onFailure = { err ->
                Result.failure(err)
            }
        )
    }

    suspend fun createMatch(
        organizerId: String,
        organizerName: String,
        location: String,
        address: String,
        date: String,
        time: String,
        modality: String,
        price: Double,
        durationHours: Double = 1.0,
        level: String = "Amador"
    ): Result<MatchEntity> {
        val matchId = UUID.randomUUID().toString()
        val matchDateCombined = "$date ${if (time.contains(":")) time else "$time:00"}:00"

        val localEntity = MatchEntity(
            id = matchId,
            contractorId = organizerId,
            contractorName = organizerName,
            refereeId = "",
            refereeName = "Aguardando Árbitro",
            date = date,
            time = time,
            location = location,
            modality = modality,
            price = price,
            status = "aberta",
            paymentMethod = "pix",
            contractorCheckin = false,
            refereeCheckin = false,
            duration = durationHours.toInt().coerceAtLeast(1),
            platformFee = 15.0,
            isSurge = price > 150.0
        )
        db.matchDao().insertMatch(localEntity)

        if (supabaseClient.hasConfiguredKey()) {
            val sm = SupabaseMatch(
                id = matchId,
                organizer_id = organizerId,
                referee_id = null,
                location = location,
                address = address,
                category = modality,
                price = price,
                status = "aberta",
                duration = durationHours,
                match_date = matchDateCombined,
                level = level
            )
            supabaseClient.createMatch(sm)
        }
        return Result.success(localEntity)
    }

    suspend fun acceptMatchAsReferee(matchId: String, refereeId: String, refereeName: String): Result<Unit> {
        db.matchDao().assignReferee(id = matchId, refereeId = refereeId, refereeName = refereeName, status = "aceita")
        if (supabaseClient.hasConfiguredKey()) {
            supabaseClient.updateMatch(
                id = matchId,
                updates = mapOf(
                    "referee_id" to refereeId,
                    "status" to "aceita"
                )
            )
        }
        return Result.success(Unit)
    }

    suspend fun updateMatchBilling(matchId: String, billingId: String?, billingUrl: String?, status: String?) {
        db.matchDao().updateMatchBilling(matchId, billingId, billingUrl, status)
    }

    suspend fun setContractorCheckin(matchId: String, checkin: Boolean) {
        db.matchDao().updateContractorCheckin(matchId, checkin)
    }

    suspend fun setRefereeCheckin(matchId: String, checkin: Boolean) {
        db.matchDao().updateRefereeCheckin(matchId, checkin)
    }

    suspend fun addMatchEvent(matchId: String, type: String, minute: Int, description: String) {
        db.matchEventDao().insertEvent(
            MatchEventEntity(
                matchId = matchId,
                type = type,
                minute = minute,
                description = description
            )
        )
    }

    suspend fun addReview(
        matchId: String,
        reviewerId: String,
        reviewerName: String,
        targetId: String,
        rating: Float,
        punctuality: Float,
        professionalism: Float,
        comment: String
    ) {
        db.reviewDao().insertReview(
            ReviewEntity(
                matchId = matchId,
                reviewerId = reviewerId,
                reviewerName = reviewerName,
                targetId = targetId,
                rating = rating,
                punctuality = punctuality,
                professionalism = professionalism,
                comment = comment
            )
        )
    }

    suspend fun withdrawFunds(userId: String, amount: Double, pixKey: String) {
        db.transactionDao().insertTransaction(
            TransactionEntity(
                userId = userId,
                type = "saque",
                amount = amount,
                description = "Saque PIX para chave: $pixKey"
            )
        )
    }

    suspend fun ensureSeedData() {
        if (supabaseClient.hasConfiguredKey()) {
            val syncRes = syncWithSupabase(forceClearLocal = true)
            if (syncRes.isSuccess && syncRes.getOrDefault(0) > 0) {
                return
            }
        }

        val contractor = db.profileDao().getFirstProfileByRole("contratante")
        if (contractor != null) return // Already seeded

        // Seed Profiles
        val defaultContractor = ProfileEntity(
            id = "user_contractor_1",
            fullName = "Time Tabajara FC",
            email = "tabajara@apito.com",
            role = "contratante",
            city = "São Paulo",
            phone = "(11) 99876-5432",
            bio = "Organizador de amistosos e copas de futebol society na zona oeste.",
            contractorType = "Time"
        )

        val referee1 = ProfileEntity(
            id = "ref_carlos_silva",
            fullName = "Carlos Silva",
            email = "carlos.silva@apito.com",
            role = "arbitro",
            city = "São Paulo",
            phone = "(11) 98111-2233",
            bio = "Árbitro credenciado FPF com mais de 8 anos de experiência em campeonatos de várzea e ligas corporativas.",
            modalities = "futebol,society",
            equipment = "Apito,Cartões,Cronômetro,Súmula",
            hourlyRate = 150.0,
            level = "ouro",
            gamesCount = 54,
            ratingAvg = 4.9,
            isVerified = true,
            availability = "Sexta,Sábado,Domingo",
            certifications = "FPF Nível 1, CBF Amador"
        )

        val referee2 = ProfileEntity(
            id = "ref_roberto_juiz",
            fullName = "Roberto Juiz",
            email = "roberto@apito.com",
            role = "arbitro",
            city = "Rio de Janeiro",
            phone = "(21) 97222-3344",
            bio = "Especialista em Futsal e Futebol 7. Apita com firmeza, diálogo e excelente posicionamento tático.",
            modalities = "futsal,society",
            equipment = "Apito,Cartões,Cronômetro",
            hourlyRate = 110.0,
            level = "prata",
            gamesCount = 32,
            ratingAvg = 4.75,
            isVerified = true,
            availability = "Terça,Quinta,Sábado",
            certifications = "Federação Carioca de Futsal"
        )

        val referee3 = ProfileEntity(
            id = "ref_ana_apito",
            fullName = "Ana Paula Oliveira",
            email = "ana.apito@apito.com",
            role = "arbitro",
            city = "Curitiba",
            phone = "(41) 98888-7766",
            bio = "Formada em Educação Física, árbitra oficial com pontualidade impecável e controle disciplinar exemplar.",
            modalities = "society,futebol_7,futebol",
            equipment = "Apito,Cartões,Cronômetro,Súmula,Placar",
            hourlyRate = 130.0,
            level = "ouro",
            gamesCount = 47,
            ratingAvg = 4.95,
            isVerified = true,
            availability = "Quarta,Sexta,Sábado,Domingo",
            certifications = "Federação Paranaense de Futebol"
        )

        val referee4 = ProfileEntity(
            id = "ref_marcos_whistle",
            fullName = "Marcos Whistle",
            email = "marcos.w@apito.com",
            role = "arbitro",
            city = "São Paulo",
            phone = "(11) 99123-4567",
            bio = "Árbitro Master Black com histórico em finais de grandes torneios. Condução tranquila e jogo limpo garantido.",
            modalities = "futebol,futsal",
            equipment = "Apito,Cartões,Cronômetro,Súmula,Spray",
            hourlyRate = 200.0,
            level = "black",
            gamesCount = 112,
            ratingAvg = 5.0,
            isVerified = true,
            availability = "Sábado,Domingo",
            certifications = "CBF Nacional, CONMEBOL Amador"
        )

        val referee5 = ProfileEntity(
            id = "ref_julia_campo",
            fullName = "Julia Campo",
            email = "julia.c@apito.com",
            role = "arbitro",
            city = "Belo Horizonte",
            phone = "(31) 99765-4321",
            bio = "Árbitra jovem e muito enérgica, pronta para conduzir partidas dinâmicas e de alto ritmo competitivo.",
            modalities = "futebol,futsal",
            equipment = "Apito,Cartões",
            hourlyRate = 125.0,
            level = "bronze",
            gamesCount = 19,
            ratingAvg = 4.65,
            isVerified = true,
            availability = "Segunda,Quarta,Sábado",
            certifications = "Federação Mineira de Futebol"
        )

        db.profileDao().insertProfiles(listOf(defaultContractor, referee1, referee2, referee3, referee4, referee5))

        // Seed Matches
        val match1 = MatchEntity(
            id = "match_active_1",
            contractorId = defaultContractor.id,
            contractorName = defaultContractor.fullName,
            refereeId = referee1.id,
            refereeName = referee1.fullName,
            date = "2026-10-18",
            time = "19:30",
            location = "Arena Gol de Placa - Quadra 2, SP",
            modality = "society",
            price = 150.0,
            status = "aceita",
            paymentMethod = "pix",
            contractorCheckin = true,
            refereeCheckin = false,
            duration = 1,
            platformFee = 15.0,
            isSurge = false
        )

        val match2 = MatchEntity(
            id = "match_pending_2",
            contractorId = defaultContractor.id,
            contractorName = defaultContractor.fullName,
            refereeId = referee4.id,
            refereeName = referee4.fullName,
            date = "2026-10-24",
            time = "16:00",
            location = "Centro Esportivo Pacaembu - Campo 1",
            modality = "futebol",
            price = 200.0,
            status = "pendente",
            paymentMethod = "cartao",
            contractorCheckin = false,
            refereeCheckin = false,
            duration = 2,
            platformFee = 25.0,
            isSurge = true
        )

        val match3 = MatchEntity(
            id = "match_finished_3",
            contractorId = defaultContractor.id,
            contractorName = defaultContractor.fullName,
            refereeId = referee2.id,
            refereeName = referee2.fullName,
            date = "2026-09-12",
            time = "20:00",
            location = "Ginásio Ibirapuera - Quadra A",
            modality = "futsal",
            price = 110.0,
            status = "finalizada",
            paymentMethod = "pix",
            contractorCheckin = true,
            refereeCheckin = true,
            duration = 1,
            platformFee = 12.0,
            isSurge = false
        )

        db.matchDao().insertMatches(listOf(match1, match2, match3))

        // Seed Reviews
        val review1 = ReviewEntity(
            id = "rev_1",
            matchId = match3.id,
            reviewerId = defaultContractor.id,
            reviewerName = defaultContractor.fullName,
            targetId = referee1.id,
            rating = 5.0f,
            punctuality = 5.0f,
            professionalism = 5.0f,
            comment = "Excelente juiz! Chegou 20 minutos antes, controlou o jogo sem picotar as jogadas. Chamaremos de novo!"
        )

        val review2 = ReviewEntity(
            id = "rev_2",
            matchId = match3.id,
            reviewerId = defaultContractor.id,
            reviewerName = "Grêmio Vila Mariana",
            targetId = referee1.id,
            rating = 4.8f,
            punctuality = 5.0f,
            professionalism = 4.7f,
            comment = "Muito experiente, manteve o respeito entre as duas equipes do início ao fim."
        )

        val review3 = ReviewEntity(
            id = "rev_3",
            matchId = match3.id,
            reviewerId = defaultContractor.id,
            reviewerName = "FC Boleiros",
            targetId = referee4.id,
            rating = 5.0f,
            punctuality = 5.0f,
            professionalism = 5.0f,
            comment = "Nível profissional sem comparação. Vale cada centavo investido na arbitragem."
        )

        db.reviewDao().insertReviews(listOf(review1, review2, review3))

        // Seed Transactions
        val trans1 = TransactionEntity(
            id = "tx_1",
            userId = referee1.id,
            type = "entrada",
            amount = 135.0,
            description = "Partida Amadora (Society) - Arena Morumbi"
        )
        val trans2 = TransactionEntity(
            id = "tx_2",
            userId = referee1.id,
            type = "saque",
            amount = 100.0,
            description = "Saque via PIX para Nubank"
        )
        val trans3 = TransactionEntity(
            id = "tx_3",
            userId = defaultContractor.id,
            type = "entrada",
            amount = 300.0,
            description = "Recarga de saldo da equipe"
        )

        db.transactionDao().insertTransactions(listOf(trans1, trans2, trans3))

        // Seed Match Events for match 3
        val event1 = MatchEventEntity(
            matchId = match3.id,
            type = "gol",
            minute = 14,
            description = "Gol de camisa 10 (Time Tabajara)"
        )
        val event2 = MatchEventEntity(
            matchId = match3.id,
            type = "cartao_amarelo",
            minute = 28,
            description = "Falta tática no meio campo (camisa 7)"
        )
        val event3 = MatchEventEntity(
            matchId = match3.id,
            type = "gol",
            minute = 42,
            description = "Gol de empate (Garra FC)"
        )
        db.matchEventDao().insertEvent(event1)
        db.matchEventDao().insertEvent(event2)
        db.matchEventDao().insertEvent(event3)
    }
}
