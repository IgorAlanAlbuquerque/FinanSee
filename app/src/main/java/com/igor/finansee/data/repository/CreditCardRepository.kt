package com.igor.finansee.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.igor.finansee.data.daos.CreditCardDao
import com.igor.finansee.data.models.CreditCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CreditCardRepository(
    private val creditCardDao: CreditCardDao,
    firestore: FirebaseFirestore,
    private val userId: String
) {
    val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val collection = firestore.collection("users").document(userId).collection("credit_cards")

    fun getCardsFromRoom(): Flow<List<CreditCard>> {
        return creditCardDao.getCardsForUser(userId)
    }

    fun startListeningForRemoteChanges() {
        collection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.w("Firestore", "Listen failed for credit cards.", error)
                return@addSnapshotListener
            }
            if (snapshots != null) {
                val remoteCards = snapshots.toObjects<CreditCard>()
                repositoryScope.launch {
                    remoteCards.forEach { card -> creditCardDao.upsertCreditCard(card) }
                }
            }
        }
    }

    suspend fun upsertCreditCard(creditCard: CreditCard) {
        try {
            val creditCardToSave = if (creditCard.id.isBlank()) {
                val firestoreId = collection.document().id
                creditCard.copy(id = firestoreId)
            } else {
                creditCard
            }

            creditCardDao.upsertCreditCard(creditCardToSave)

            collection.document(creditCardToSave.id).set(creditCardToSave).await()

        } catch (e: Exception) {
            Log.e("Firestore", "Error upserting credit card", e)
        }
    }

    fun cancelScope() {
        repositoryScope.cancel()
    }
}