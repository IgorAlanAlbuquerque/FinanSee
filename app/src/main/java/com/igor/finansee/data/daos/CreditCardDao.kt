package com.igor.finansee.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.igor.finansee.data.models.CreditCard
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditCardDao {
    @Query("SELECT * FROM credit_card WHERE userId = :userId")
    fun getCardsForUser(userId: String): Flow<List<CreditCard>>

    @Upsert
    suspend fun upsertCreditCard(card: CreditCard)
}