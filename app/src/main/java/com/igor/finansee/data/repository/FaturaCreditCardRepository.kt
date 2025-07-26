package com.igor.finansee.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.igor.finansee.data.daos.FaturaCreditCardDao
import com.igor.finansee.data.models.FaturaCreditCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class FaturaCreditCardRepository(
    private val faturaDao: FaturaCreditCardDao,
    firestore: FirebaseFirestore,
    private val userId: String
) {
    val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val collection = firestore.collection("users").document(userId).collection("faturas")

    fun getFaturasFromRoom(startDate: LocalDate, endDate: LocalDate) =
        faturaDao.getFaturasForUserInPeriod(userId, startDate, endDate)

    fun startListeningForRemoteChanges() {
        collection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.w("Firestore", "Listen failed for faturas.", error)
                return@addSnapshotListener
            }
            if (snapshots != null) {
                val remoteFaturas = snapshots.toObjects<FaturaCreditCard>()
                repositoryScope.launch {
                    remoteFaturas.forEach { fatura -> faturaDao.upsertFatura(fatura) }
                }
            }
        }
    }

    suspend fun upsertFatura(fatura: FaturaCreditCard) {
        try {
            val faturaToSave = if (fatura.id.isBlank()) {
                val firestoreId = collection.document().id
                fatura.copy(id = firestoreId)
            } else {
                fatura
            }

            faturaDao.upsertFatura(faturaToSave)

            collection.document(faturaToSave.id).set(faturaToSave).await()

        } catch (e: Exception) {
            Log.e("Firestore", "Error upserting bank account", e)
        }
    }

    fun cancelScope() {
        repositoryScope.cancel()
    }
}