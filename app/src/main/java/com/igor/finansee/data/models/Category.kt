package com.igor.finansee.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(tableName = "category")
data class Category(
    @PrimaryKey
    val id: String = "",
    val name: String,
)