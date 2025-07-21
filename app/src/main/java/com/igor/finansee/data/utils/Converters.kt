package com.igor.finansee.data.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.igor.finansee.data.models.PlannedCategorySpending

class Converters {
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