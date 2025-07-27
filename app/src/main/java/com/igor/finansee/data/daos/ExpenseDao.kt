package com.igor.finansee.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.igor.finansee.data.models.Expense
import com.igor.finansee.data.models.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {
    @Transaction
    @Query("SELECT * FROM expense WHERE userId = :userId AND data >= :startDate AND data < :endDate ORDER BY data DESC")
    fun getExpensesForPeriod(userId: String, startDate: Date, endDate: Date): Flow<List<ExpenseWithCategory>>

    @Upsert
    suspend fun upsertExpense(expense: Expense)

    @Query("DELETE FROM expense WHERE id = :expenseId")
    suspend fun deleteExpenseById(expenseId: String)

    @Transaction
    @Query("SELECT * FROM expense WHERE data >= :start AND data < :end")
    fun getExpensesWithCategoryBetween(start: Date, end: Date): Flow<List<ExpenseWithCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(expenses: List<Expense>)
}
