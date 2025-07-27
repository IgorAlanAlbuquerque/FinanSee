package com.igor.finansee.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.igor.finansee.data.daos.TransactionDao
import com.igor.finansee.data.models.CategorySpending
import com.igor.finansee.data.models.Transaction
import com.igor.finansee.data.models.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.Date

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val firestore: FirebaseFirestore,
    private val userId: String
) {
    val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val collection = firestore.collection("users").document(userId).collection("transactions")

    fun getTransactionsFromRoom(startDate: LocalDate, endDate: LocalDate) =
        transactionDao.getTransactionsForPeriod(userId, startDate, endDate)

    // --- SINCRONIZAÇÃO Remoto -> Local ---
    fun startListeningForRemoteChanges() {
        collection.addSnapshotListener { snapshots, error ->
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
    suspend fun upsertTransaction(transaction: Transaction) {
        try {
            val transactionToSave = if (transaction.id.isBlank()) {
                val firestoreId = collection.document().id
                transaction.copy(id = firestoreId)
            } else {
                transaction
            }

            transactionDao.upsertTransaction(transactionToSave)
            collection.document(transactionToSave.id).set(transactionToSave).await()

        } catch (e: Exception) {
            Log.e("Firestore", "Error upserting bank account", e)
        }
    }

    fun cancelScope() {
        repositoryScope.cancel()
    }

    fun getSumByTypesForPeriod(types: List<TransactionType>, startDate: Date?, endDate: Date?) =
        transactionDao.getSumByTypesForPeriod(userId, types, startDate, endDate)

    fun getExpensesGroupedByCategory(types: List<TransactionType>, startDate: Date, endDate: Date): Flow<List<CategorySpending>> {
        return transactionDao.getExpensesGroupedByCategory(userId, startDate, endDate, types)
    }
}