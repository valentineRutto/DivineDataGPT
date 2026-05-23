package com.valentinerutto.divinedatagpt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.valentinerutto.divinedatagpt.data.local.entity.bible.ReadingPlanCompletionEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.ReadingPlanDayEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.ReadingPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingPlanDao {
    @Query("SELECT * FROM reading_plans ORDER BY createdAt DESC")
    fun observePlans(): Flow<List<ReadingPlanEntity>>

    @Query("SELECT * FROM reading_plan_days ORDER BY planId ASC, dayNumber ASC")
    fun observePlanDays(): Flow<List<ReadingPlanDayEntity>>

    @Query("SELECT * FROM reading_plan_completions ORDER BY completedAt DESC")
    fun observeCompletions(): Flow<List<ReadingPlanCompletionEntity>>

    @Insert
    suspend fun insertPlan(plan: ReadingPlanEntity): Long

    @Insert
    suspend fun insertDays(days: List<ReadingPlanDayEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun completeDay(completion: ReadingPlanCompletionEntity)

    @Query("DELETE FROM reading_plan_completions WHERE planDayId = :planDayId")
    suspend fun uncompleteDay(planDayId: Long)

    @Query("DELETE FROM reading_plans WHERE id = :planId")
    suspend fun deletePlan(planId: Long)

    @Transaction
    suspend fun insertPlanWithDays(
        plan: ReadingPlanEntity,
        days: List<ReadingPlanDayEntity>
    ): Long {
        val planId = insertPlan(plan)
        insertDays(days.map { day -> day.copy(planId = planId) })
        return planId
    }
}
