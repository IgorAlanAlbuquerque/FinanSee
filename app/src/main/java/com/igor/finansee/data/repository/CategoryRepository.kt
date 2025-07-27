package com.igor.finansee.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.models.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val firestore: FirebaseFirestore
) {
    val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val collection = firestore.collection("categories")

    fun getCategoriesFromRoom(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }

    // NOVA FUNÇÃO ADICIONADA
    suspend fun getAllCategoriesOnce(): List<Category> {
        return categoryDao.getAllCategories().first()
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

    suspend fun isEmpty(): Boolean {
        return categoryDao.getCategoryCount() == 0
    }

    suspend fun insertAll(categories: List<Category>) {
        try {
            val categoriesWithIds = mutableListOf<Category>()
            val batch = firestore.batch()

            categories.forEach { category ->
                val firestoreId = collection.document().id
                val categoryWithId = category.copy(id = firestoreId)
                categoriesWithIds.add(categoryWithId)

                val docRef = collection.document(firestoreId)
                batch.set(docRef, categoryWithId)
            }

            categoryDao.upsertAll(categoriesWithIds)
            batch.commit().await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error inserting initial categories", e)
        }
    }

    fun cancelScope() {
        repositoryScope.cancel()
    }
}