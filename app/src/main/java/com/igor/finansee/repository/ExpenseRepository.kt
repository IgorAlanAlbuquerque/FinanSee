package com.igor.finansee.data.repositories

import com.igor.finansee.data.models.Expense
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.expenseList
import java.util.UUID
import java.time.LocalDate

class ExpenseRepository {

    fun getAllExpenses(): List<Expense> = expenseList

    fun updateExpense(expenseId: UUID, descricao: String, valor: Double, categoria: Category, data: String) {
        val index = expenseList.indexOfFirst { it.id == expenseId }
        if (index != -1) {
            val updatedExpense = expenseList[index].copy(
                descricao = descricao,
                valor = valor,
                categoria = categoria,
                data = LocalDate.parse(data)
            )
            expenseList[index] = updatedExpense
        }
    }

    fun deleteExpense(expenseId: UUID) {
        expenseList.removeAll { it.id == expenseId }
    }

    fun addExpense(descricao: String, valor: Double, categoria: Category, data: String) {
        val newExpense = Expense(
            descricao = descricao,
            valor = valor,
            categoria = categoria,
            data = LocalDate.parse(data)
        )
        expenseList.add(newExpense)
    }
}
