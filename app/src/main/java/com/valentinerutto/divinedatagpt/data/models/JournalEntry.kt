package com.valentinerutto.divinedatagpt.data.models

enum class JournalSourceType { BIBLE_READER, REFLECTION_CHAT, MANUAL }

/**
 * A single saved reflection. sessionId/messageId are soft references, not
 * Room foreign keys with CASCADE — deleting a chat session should never
 * silently delete a journal entry the user deliberately chose to keep.
 */
data class JournalEntry(
    val id: String,
    val verse: VerseCitation?,
    val note: String?,
    val sourceType: JournalSourceType,
    val sessionId: String? = null,
    val messageId: String? = null,
    val createdAt: Long
)

data class JournalUiState(
    val entries: List<JournalEntry> = emptyList(),
    val isComposerOpen: Boolean = false,
    val composerNote: String = "",
    val composerVerse: VerseCitation? = null,
    val isVersePickerOpen: Boolean = false
)