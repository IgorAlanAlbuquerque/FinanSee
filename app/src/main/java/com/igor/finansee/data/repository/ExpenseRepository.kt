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

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    firestore: FirebaseFirestore,
    userId: String
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
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
                    expenseDao.upsertAll(remoteExpenses)
                }
            }
        }
    }

    suspend fun upsertExpense(expense: Expense) {
        try {
            val expenseToSave = if (expense.id.isBlank()) {
                val newId = collection.document().id
                expense.copy(id = newId)
            } else {
                expense
            }

            expenseDao.upsertExpense(expenseToSave)
            collection.document(expenseToSave.id).set(expenseToSave).await()

        } catch (e: Exception) {
            Log.e("Firestore", "Error upserting expense", e)
        }
    }

    suspend fun deleteExpenseById(expenseId: String) {
        try {
            expenseDao.deleteExpenseById(expenseId)
            collection.document(expenseId).delete().await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error deleting expense", e)
        }
    }

    fun cancelScope() {
        repositoryScope.cancel()
    }
}