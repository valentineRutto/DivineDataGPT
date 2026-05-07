package com.valentinerutto.divinedatagpt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user" or "assistant"
    val content: String,
    val verse: String? = null,
    val reference: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
