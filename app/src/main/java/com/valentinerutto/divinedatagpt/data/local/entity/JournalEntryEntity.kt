package com.valentinerutto.divinedatagpt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey val id: String,
    val verseId: String?,
    val note: String?,
    val sourceType: String,     // "bible_reader" | "reflection_chat" | "manual"
    val sessionId: String?,     // soft reference — no FK/CASCADE, see note below
    val messageId: String?,
    val createdAt: Long
)