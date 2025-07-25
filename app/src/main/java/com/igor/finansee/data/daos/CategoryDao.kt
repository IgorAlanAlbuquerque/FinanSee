package com.igor.finansee.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.igor.finansee.data.models.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Upsert
    suspend fun upsertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)
    @Query("SELECT * FROM category")
    fun getAllCategories(): Flow<List<Category>>
}