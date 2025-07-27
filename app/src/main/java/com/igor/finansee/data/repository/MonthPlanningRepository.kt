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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class MonthPlanningRepository(
    private val planningDao: MonthPlanningDao,
    firestore: FirebaseFirestore,
    private val userId: String
) {
    val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val collection = firestore.collection("users").document(userId).collection("plannings")

    fun getPlanningFromRoom(startDate: LocalDate, endDate: LocalDate): Flow<MonthPlanning?> {
        val start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        return planningDao.getPlanningForUserInPeriod(userId, start, end)
    }
    // NOVA FUNÇÃO ADICIONADA
    suspend fun getPlanningForMonthOnce(month: LocalDate): MonthPlanning? {
        val startDate = Date.from(month.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endDate = Date.from(month.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant())

        return planningDao.getPlanningForMonth(userId, startDate, endDate)
    }
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
            planningDao.upsertPlanning(planning)
            collection.document(planning.id).set(planning).await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error upserting planning", e)
        }
    }

    fun cancelScope() {
        repositoryScope.cancel()
    }
}