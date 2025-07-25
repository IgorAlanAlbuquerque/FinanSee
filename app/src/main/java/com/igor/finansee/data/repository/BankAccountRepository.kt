package com.igor.finansee.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.igor.finansee.data.daos.BankAccountDao
import com.igor.finansee.data.models.BankAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BankAccountRepository(
    private val bankAccountDao: BankAccountDao,
    firestore: FirebaseFirestore,
    private val userId: String
) {
    val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val collection = firestore.collection("users").document(userId).collection("bank_accounts")

    fun getAccountsFromRoom(): Flow<List<BankAccount>> {
        return bankAccountDao.getAccountsForUser(userId.toIntOrNull() ?: 0)
    }

    fun getOverallBalanceFromRoom(): Flow<Double?> {
        return bankAccountDao.getOverallBalanceForUser(userId.toIntOrNull() ?: 0)
    }

    fun startListeningForRemoteChanges() {
        collection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.w("Firestore", "Listen failed for bank accounts.", error)
                return@addSnapshotListener
            }
            if (snapshots != null) {
                val remoteAccounts = snapshots.toObjects<BankAccount>()
                repositoryScope.launch {
                    remoteAccounts.forEach { account ->
                        bankAccountDao.upsertBankAccount(account)
                    }
                }
            }
        }
    }

    suspend fun saveAccount(account: BankAccount) {
        try {
            collection.document(account.id.toString()).set(account).await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error saving bank account", e)
        }
    }

    fun cancelScope() {
        repositoryScope.cancel()
    }
}