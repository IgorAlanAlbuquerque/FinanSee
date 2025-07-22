package com.igor.finansee.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.igor.finansee.data.models.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAllCategories(): Flow<List<Category>>
}