package com.valentinerutto.divinedatagpt.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity

@Fts4(contentEntity = VerseEntity::class)
@Entity(tableName = "verses_fts")
data class VersesFTS(
    val bookName: String,
    val text: String
)
