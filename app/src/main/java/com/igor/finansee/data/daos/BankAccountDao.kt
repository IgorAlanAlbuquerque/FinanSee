package com.igor.finansee.data.daos

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {
    @Query("SELECT SUM(currentBalance) FROM bank_accounts WHERE userId = :userId AND isActive = 1")
    fun getOverallBalanceForUser(userId: Int): Flow<Double?>
}