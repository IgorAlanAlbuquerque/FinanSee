package com.igor.finansee.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.igor.finansee.data.daos.ExpenseDao
import com.igor.finansee.data.models.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.UUID

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    firestore: FirebaseFirestore,
    userId: String
) {
    val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val collection = firestore.collection("users").document(userId).collection("expenses")

    fun getExpensesFromRoom(userId: String, startDate: LocalDate, endDate: LocalDate) =
        expenseDao.getExpensesForPeriod(userId, startDate, endDate)

    fun startListeningForRemoteChanges() {
        collection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.w("Firestore", "Listen failed for expenses.", error)
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val remoteExpenses = snapshots.toObjects<Expense>()
                repositoryScope.launch {
                    remoteExpenses.forEach { expense ->
                        expenseDao.upsertExpense(expense)
                    }
                }
            }
        }
    }

    suspend fun saveExpense(expense: Expense) {
        try {
            collection.document(expense.id.toString()).set(expense).await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error saving expense", e)
        }
    }

    suspend fun deleteExpense(expense: Expense) {
        try {
            collection.document(expense.id.toString()).delete().await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error deleting expense", e)
        }
    }

    suspend fun getExpenseById(userId: String, expenseId: UUID): Expense? {
        return expenseDao.getExpenseById(
            expenseId,
            userId = userId
        )
    }

    fun cancelScope() {
        repositoryScope.cancel()
    }
}