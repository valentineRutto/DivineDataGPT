package com.valentinerutto.divinedatagpt.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.valentinerutto.divinedatagpt.data.local.entity.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun observeEntries(): Flow<List<JournalEntryEntity>>

    @Upsert
    suspend fun insert(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun delete(id: String)
}