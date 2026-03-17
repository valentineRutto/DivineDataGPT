package com.valentinerutto.divinedatagpt.data.local.entity.bible

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "bible_books")
data class BibleBookEntity(
    @PrimaryKey
    val bookName: String,
    // "Genesis"
    val abbreviation: String,      // "genesis"
    val bookOrder: Int,            // 1-66
    val testament: String,         // "Old Testament" or "New Testament"
    val totalChapters: Int,        // Total chapters in book
    val totalVerses: Int           // Total verses in book
)