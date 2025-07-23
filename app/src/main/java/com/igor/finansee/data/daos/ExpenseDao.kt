package com.igor.finansee.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.igor.finansee.data.models.Expense
import com.igor.finansee.data.models.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

@Dao
interface ExpenseDao {
    @Transaction
    @Query("SELECT * FROM expense WHERE data >= :startDate AND data < :endDate ORDER BY data DESC")
    fun getExpensesForPeriod(startDate: LocalDate, endDate: LocalDate): Flow<List<ExpenseWithCategory>>

    @Upsert
    suspend fun upsertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expense WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: UUID): Expense?
}