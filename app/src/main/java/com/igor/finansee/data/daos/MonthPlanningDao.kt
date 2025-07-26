package com.igor.finansee.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.igor.finansee.data.models.MonthPlanning
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MonthPlanningDao {
    @Upsert
    suspend fun upsertPlanning(planning: MonthPlanning)

    @Query("SELECT * FROM month_planning WHERE userId = :userId AND monthYear >= :startDate AND monthYear < :endDate LIMIT 1")
    fun getPlanningForUserInPeriod(userId: String, startDate: LocalDate, endDate: LocalDate): Flow<MonthPlanning?>
}