package com.valentinerutto.divinedatagpt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.valentinerutto.divinedatagpt.data.local.entity.MemorySummaryEntity

@Dao
interface MemorySummaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(summary: MemorySummaryEntity)

    @Query("SELECT * FROM memory_summary WHERE id = 0 LIMIT 1")
    suspend fun getSummary(): MemorySummaryEntity?

    @Query("DELETE FROM memory_summary")
    suspend fun clearSummary()
}