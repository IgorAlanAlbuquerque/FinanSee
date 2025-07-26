package com.igor.finansee.data.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.igor.finansee.data.models.PlannedCategorySpending
import com.igor.finansee.data.models.TransactionType
import java.time.LocalDate
import java.util.Date

object Converters {
    @TypeConverter
    fun fromTransactionType(type: TransactionType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? {
        return value?.let { enumValueOf<TransactionType>(it) }
    }

    @TypeConverter
    fun fromCategorySpendingPlan(plan: List<PlannedCategorySpending>?): String? {
        if (plan == null) {
            return null
        }
        return Gson().toJson(plan)
    }

    @TypeConverter
    fun toCategorySpendingPlan(planString: String?): List<PlannedCategorySpending>? {
        if (planString == null) {
            return null
        }
        val listType = object : TypeToken<List<PlannedCategorySpending>>() {}.type
        return Gson().fromJson(planString, listType)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? { // O nome poderia ser toDate, mas funciona
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? { // O nome poderia ser fromDate, mas funciona
        return date?.time
    }

    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}