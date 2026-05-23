package com.valentinerutto.divinedatagpt.data.local.entity.bible

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "reading_plans")
data class ReadingPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val templateId: String,
    val title: String,
    val subtitle: String,
    val durationDays: Int,
    val category: String,
    val accentColor: String,
    val startEpochDay: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "reading_plan_days",
    foreignKeys = [
        ForeignKey(
            entity = ReadingPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["planId"]),
        Index(value = ["planId", "dayNumber"], unique = true)
    ]
)
data class ReadingPlanDayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val planId: Long,
    val dayNumber: Int,
    val title: String,
    val bookName: String,
    val book: Int,
    val chapter: Int,
    val focus: String
)

@Entity(
    tableName = "reading_plan_completions",
    foreignKeys = [
        ForeignKey(
            entity = ReadingPlanDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["planDayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["planDayId"], unique = true),
        Index(value = ["completedEpochDay"])
    ]
)
data class ReadingPlanCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val planDayId: Long,
    val completedEpochDay: Long,
    val completedAt: Long = System.currentTimeMillis()
)
