package com.valentinerutto.divinedatagpt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "memory_summary")
data class MemorySummaryEntity(
    @PrimaryKey val id: Int = 0,
    val summary: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
