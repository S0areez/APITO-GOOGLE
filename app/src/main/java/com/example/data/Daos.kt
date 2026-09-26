package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles WHERE role = 'arbitro'")
    fun getAllReferees(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    fun getProfileById(id: String): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileDirect(id: String): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE role = :role LIMIT 1")
    suspend fun getFirstProfileByRole(role: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<ProfileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    @Query("DELETE FROM profiles")
    suspend fun clearProfiles()
}

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches ORDER BY createdAt DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE (refereeId = '' OR status = 'aberta') ORDER BY createdAt DESC")
    fun getOpenMatches(): Flow<List<MatchEntity>>

    @Query("UPDATE matches SET refereeId = :refereeId, refereeName = :refereeName, status = :status WHERE id = :id")
    suspend fun assignReferee(id: String, refereeId: String, refereeName: String, status: String = "aceita")

    @Query("SELECT * FROM matches WHERE contractorId = :contractorId ORDER BY createdAt DESC")
    fun getMatchesByContractor(contractorId: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE refereeId = :refereeId ORDER BY createdAt DESC")
    fun getMatchesByReferee(refereeId: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE id = :id LIMIT 1")
    fun getMatchById(id: String): Flow<MatchEntity?>

    @Query("SELECT * FROM matches WHERE id = :id LIMIT 1")
    suspend fun getMatchDirect(id: String): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Query("UPDATE matches SET status = :status WHERE id = :id")
    suspend fun updateMatchStatus(id: String, status: String)

    @Query("UPDATE matches SET contractorCheckin = :checkin WHERE id = :id")
    suspend fun updateContractorCheckin(id: String, checkin: Boolean)

    @Query("UPDATE matches SET refereeCheckin = :checkin WHERE id = :id")
    suspend fun updateRefereeCheckin(id: String, checkin: Boolean)

    @Query("UPDATE matches SET abacateBillingId = :billingId, abacateBillingUrl = :billingUrl, abacateStatus = :status WHERE id = :id")
    suspend fun updateMatchBilling(id: String, billingId: String?, billingUrl: String?, status: String?)

    @Query("DELETE FROM matches")
    suspend fun clearMatches()
}

@Dao
interface MatchEventDao {
    @Query("SELECT * FROM match_events WHERE matchId = :matchId ORDER BY minute ASC, createdAt ASC")
    fun getEventsByMatch(matchId: String): Flow<List<MatchEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: MatchEventEntity)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE targetId = :targetId ORDER BY createdAt DESC")
    fun getReviewsForTarget(targetId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTransactionsByUser(userId: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()
}
