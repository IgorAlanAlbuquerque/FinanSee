package com.igor.finansee.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.igor.finansee.data.models.Transaction
import com.igor.finansee.data.models.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TransactionDao {
    @Query("""
        SELECT * FROM transactions
        WHERE userId = :userId AND date >= :startDate AND date < :endDate
        ORDER BY date DESC
    """)
    fun getTransactionsForPeriod(userId: Int, startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>>

    @Query("""
        SELECT SUM(value) FROM transactions
        WHERE userId = :userId AND type IN (:types) AND date >= :startDate AND date < :endDate
    """)
    fun getSumByTypesForPeriod(userId: Int, types: List<TransactionType>, startDate: LocalDate, endDate: LocalDate): Flow<Double?>

    @Query("""
        SELECT categoryId, SUM(value) as totalAmount FROM transactions
        WHERE userId = :userId AND date >= :startDate AND date < :endDate AND type IN (:expenseTypes)
        GROUP BY categoryId
    """)
    fun getExpensesGroupedByCategory(userId: Int, startDate: LocalDate, endDate: LocalDate, expenseTypes: List<TransactionType>): Flow<List<CategoryExpenseResult>>
}

data class CategoryExpenseResult(
    val categoryId: Int,
    val totalAmount: Double
)