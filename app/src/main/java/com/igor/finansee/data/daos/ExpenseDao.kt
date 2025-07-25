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
    /**
     * Busca todas as despesas de um USUÁRIO ESPECÍFICO dentro de um período.
     * A cláusula "WHERE userId = :userId" é a chave para filtrar os dados.
     */
    @Transaction
    @Query("SELECT * FROM expense WHERE userId = :userId AND data >= :startDate AND data < :endDate ORDER BY data DESC")
    fun getExpensesForPeriod(userId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<ExpenseWithCategory>>

    /**
     * Insere ou atualiza uma despesa.
     * O objeto 'expense' que chega aqui JÁ DEVE CONTER o userId correto.
     */
    @Upsert
    suspend fun upsertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Transaction
    @Query("SELECT * FROM expense WHERE data >= :start AND data < :end")
    fun getExpensesWithCategoryBetween(start: LocalDate, end: LocalDate): Flow<List<ExpenseWithCategory>>
    /**
     * Busca uma despesa específica pelo seu ID, garantindo que ela pertence ao usuário logado.
     */
    @Query("SELECT * FROM expense WHERE id = :expenseId AND userId = :userId")
    suspend fun getExpenseById(expenseId: UUID, userId: String): Expense?
}
