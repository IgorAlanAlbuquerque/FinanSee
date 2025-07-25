package com.igor.finansee.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.igor.finansee.data.daos.TransactionDao
import com.igor.finansee.data.models.Transaction
import com.igor.finansee.data.models.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val firestore: FirebaseFirestore,
    private val userId: String
) {
    val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun getTransactionsFromRoom(startDate: LocalDate, endDate: LocalDate) =
        transactionDao.getTransactionsForPeriod(userId.toIntOrNull(), startDate, endDate)

    // --- SINCRONIZAÇÃO Remoto -> Local ---
    fun startListeningForRemoteChanges() {
        firestore.collection("users").document(userId).collection("transactions")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val remoteTransactions = snapshots.toObjects<Transaction>()

                    repositoryScope.launch(Dispatchers.IO) {
                        for (transaction in remoteTransactions) {
                            transactionDao.upsertTransaction(transaction)
                        }
                    }
                }
            }
    }

    // --- SINCRONIZAÇÃO Local -> Remoto ---
    suspend fun saveTransaction(transaction: Transaction) {
        try {
            firestore.collection("users").document(userId)
                .collection("transactions")
                .document(transaction.id.toString())
                .set(transaction)
                .await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error saving transaction", e)
        }
    }

    fun cancelScope() {
        repositoryScope.cancel()
    }

    fun getSumByTypesForPeriod(types: List<TransactionType>, startDate: LocalDate, endDate: LocalDate) =
        transactionDao.getSumByTypesForPeriod(userId.toIntOrNull() ?: 0, types, startDate, endDate)

    fun getExpensesGroupedByCategory(expenseTypes: List<TransactionType>, startDate: LocalDate, endDate: LocalDate) =
        transactionDao.getExpensesGroupedByCategory(userId.toIntOrNull() ?: 0, startDate, endDate, expenseTypes)
}