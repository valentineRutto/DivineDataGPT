package com.valentinerutto.divinedatagpt.data.local.entity.bible

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "bible_notes",
    indices = [
        Index(value = ["verseId"], unique = true)
    ]
)
data class BibleNoteEntitynotused(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val verseId: Int,
    val translation: String,
    val bookName: String,
    val book: Int,
    val chapter: Int,
    val verse: Int,
    val verseText: String,
    val note: String,
    val highlightColor: String,
    val createdAt: Long = System.currentTimeMillis()
)
