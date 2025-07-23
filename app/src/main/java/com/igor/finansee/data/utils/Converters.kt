package com.igor.finansee.data.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.igor.finansee.data.models.Category // Supondo que Category seja um enum
import com.igor.finansee.data.models.PlannedCategorySpending
import com.igor.finansee.data.models.TransactionType
import java.time.LocalDate
import java.util.UUID

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return uuid?.let { UUID.fromString(it) }
    }

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
}