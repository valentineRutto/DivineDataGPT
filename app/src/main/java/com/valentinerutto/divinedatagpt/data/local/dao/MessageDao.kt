package com.valentinerutto.divinedatagpt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.valentinerutto.divinedatagpt.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    // Insert single message
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    // Insert multiple messages (optional utility)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<MessageEntity>)

    // Get all messages ordered by time
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    suspend fun getAllMessages(): List<MessageEntity>

    // Flow version (for Compose auto updates)
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun observeAllMessages(): Flow<List<MessageEntity>>

    // Get most recent N messages
    @Query(
        """
        SELECT * FROM messages 
        ORDER BY timestamp DESC 
        LIMIT :limit
    """
    )
    suspend fun getRecentMessages(limit: Int): List<MessageEntity>

    // Delete specific messages (used for compression)
    @Delete
    suspend fun deleteMessages(messages: List<MessageEntity>)

    // Delete by IDs (cleaner option)
    @Query("DELETE FROM messages WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    // Delete oldest messages except last N
    @Query(
        """
        DELETE FROM messages 
        WHERE id NOT IN (
            SELECT id FROM messages 
            ORDER BY timestamp DESC 
            LIMIT :keepCount
        )
    """
    )
    suspend fun deleteAllExceptLast(keepCount: Int)

    // Clear entire chat (reset)
    @Query("DELETE FROM messages")
    suspend fun clearAll()
}
