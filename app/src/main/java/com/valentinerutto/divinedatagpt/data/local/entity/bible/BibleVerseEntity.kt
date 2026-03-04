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
    val id: Long = 0,
    val book: String,              // "Genesis"
    val bookAbbrev: String,        // "genesis"
    val chapter: Int,              // 1, 2, 3...
    val verse: Int,                // 1, 2, 3...
    val text: String,              // The actual verse text
    val sectionNumber: Int = 1,    // Section within chapter
    val bookOrder: Int,            // 1-66 for sorting
    val testament: String
)