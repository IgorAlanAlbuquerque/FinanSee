package com.igor.finansee.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.models.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CategoryRepository(
    private val categoryDao: CategoryDao,
    firestore: FirebaseFirestore
) {
    val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val collection = firestore.collection("categories")

    fun getCategoriesFromRoom(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }

    fun startListeningForRemoteChanges() {
        collection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.w("Firestore", "Listen failed for categories.", error)
                return@addSnapshotListener
            }
            if (snapshots != null) {
                val remoteCategories = snapshots.toObjects<Category>()
                repositoryScope.launch {
                    remoteCategories.forEach { category ->
                        categoryDao.upsertCategory(category)
                    }
                }
            }
        }
    }

    suspend fun saveCategory(category: Category) {
        try {
            collection.document(category.id.toString()).set(category).await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error saving category", e)
        }
    }

    fun cancelScope() {
        repositoryScope.cancel()
    }
}