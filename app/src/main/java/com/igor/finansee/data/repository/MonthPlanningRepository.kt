package com.igor.finansee.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.igor.finansee.data.daos.MonthPlanningDao
import com.igor.finansee.data.models.MonthPlanning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class MonthPlanningRepository(
    private val planningDao: MonthPlanningDao,
    firestore: FirebaseFirestore,
    private val userId: String
) {
    val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val collection = firestore.collection("users").document(userId).collection("plannings")

    fun getPlanningFromRoom(startDate: LocalDate, endDate: LocalDate) =
        planningDao.getPlanningForUserInPeriod(userId, startDate, endDate)

    fun startListeningForRemoteChanges() {
        collection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.w("Firestore", "Listen failed for plannings.", error)
                return@addSnapshotListener
            }

            snapshots?.documents?.forEach { document ->
                val remotePlanning = document.toObject<MonthPlanning>()
                if (remotePlanning != null) {
                    repositoryScope.launch {
                        planningDao.upsertPlanning(remotePlanning)
                    }
                }
            }
        }
    }

    suspend fun upsertPlanning(planning: MonthPlanning) {
        try {
            val planningToSave = if (planning.id.isBlank()) {
                val firestoreId = collection.document().id
                planning.copy(id = firestoreId)
            } else {
                planning
            }

            planningDao.upsertPlanning(planningToSave)
            collection.document(planningToSave.id).set(planningToSave).await()

        } catch (e: Exception) {
            Log.e("Firestore", "Error upserting bank account", e)
        }
    }

    fun cancelScope() {
        repositoryScope.cancel()
    }
}