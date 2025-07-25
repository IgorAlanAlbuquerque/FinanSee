package com.igor.finansee.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.igor.finansee.data.models.FaturaCreditCard
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FaturaCreditCardDao {
    @Query("""
        SELECT * FROM fatura_credit_card
        WHERE month >= :startDate AND month < :endDate
        AND creditCardId IN (SELECT id FROM credit_card WHERE userId = :userId)
    """)
    fun getFaturasForUserInPeriod(userId: Int, startDate: LocalDate, endDate: LocalDate): Flow<List<FaturaCreditCard>>

    @Upsert
    suspend fun upsertFatura(fatura: FaturaCreditCard)

    @Delete
    suspend fun deleteFatura(fatura: FaturaCreditCard)
}