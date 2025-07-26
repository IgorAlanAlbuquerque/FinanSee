package com.igor.finansee.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.igor.finansee.data.models.CategoryExpenseResult
import com.igor.finansee.data.models.Transaction
import com.igor.finansee.data.models.TransactionType
import com.igor.finansee.data.models.TransactionWithCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TransactionDao {
    @androidx.room.Transaction
    @Query("""
        SELECT * FROM transactions
        WHERE userId = :userId AND date >= :startDate AND date < :endDate
        ORDER BY date DESC
    """)
    fun getTransactionsForPeriod(userId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<TransactionWithCategory>>

    @Query("""
        SELECT SUM(value) FROM transactions
        WHERE userId = :userId AND type IN (:types) AND date >= :startDate AND date < :endDate
    """)
    fun getSumByTypesForPeriod(userId: String, types: List<TransactionType>, startDate: LocalDate, endDate: LocalDate): Flow<Double?>

    @Query("""
        SELECT categoryId, SUM(value) as totalAmount FROM transactions
        WHERE userId = :userId AND date >= :startDate AND date < :endDate AND type IN (:expenseTypes)
        GROUP BY categoryId
    """)
    fun getExpensesGroupedByCategory(userId: String, startDate: LocalDate, endDate: LocalDate, expenseTypes: List<TransactionType>): Flow<List<CategoryExpenseResult>>

    @Upsert
    suspend fun upsertTransaction(transaction: Transaction)
}