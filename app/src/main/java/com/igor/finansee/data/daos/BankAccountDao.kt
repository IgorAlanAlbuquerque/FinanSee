package com.igor.finansee.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.igor.finansee.data.models.BankAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {
    @Query("SELECT SUM(currentBalance) FROM bank_accounts WHERE userId = :userId AND isActive = 1")
    fun getOverallBalanceForUser(userId: Int): Flow<Double?>

    @Query("SELECT * FROM bank_accounts WHERE userId = :userId")
    fun getAccountsForUser(userId: Int): Flow<List<BankAccount>>
}