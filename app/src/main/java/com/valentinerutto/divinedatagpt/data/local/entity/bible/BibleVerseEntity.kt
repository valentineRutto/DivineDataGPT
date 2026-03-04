package com.valentinerutto.divinedatagpt.data.local.entity.bible

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bible_verses", indices = [
        Index(value = ["book", "chapter", "verse"]),
        Index(value = ["book"])]
)
data class BibleVerseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val version: String
)