package com.valentinerutto.divinedatagpt.data

import com.valentinerutto.divinedatagpt.data.local.dao.ReadingPlanDao
import com.valentinerutto.divinedatagpt.data.local.dao.VerseDao
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleNoteEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BookmarkEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.ReadingPlanCompletionEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.ReadingPlanDayEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.ReadingPlanEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity
import com.valentinerutto.divinedatagpt.data.models.BibleBook
import com.valentinerutto.divinedatagpt.data.network.ai.AiApi
import com.valentinerutto.divinedatagpt.data.network.bible.ApiService
import kotlinx.coroutines.flow.Flow


class BibleRepository(
    private val esvApi: ApiService,
    private val huggingFaceApi: AiApi,
    private val dao: VerseDao,
    private val readingPlanDao: ReadingPlanDao

    ) {

    fun observeBooks(translation: String): Flow<List<BibleBook>> {
        return dao.observeBooks(translation)
    }

    fun observeChapter(
        translation: String = "shortname",
        book: Int,
        chapter: Int
    ): Flow<List<VerseEntity>> {
        return dao.getChapter(
            translation = translation,
            book = book,
            chapter = chapter
        )
    }

    fun observeChapters(
        translation: String,
        book: Int
    ): Flow<List<Int>> {
        return dao.observeChapters(
            translation = translation,
            book = book
        )
    }

    fun searchVerses(
        translation: String,
        query: String
    ): Flow<List<VerseEntity>> {
        return dao.searchVerses(
            translation = translation,
            query = query.trim()
        )
    }

    suspend fun getRandomDailyVerse(
        translation: String = "shortname"
    ): VerseEntity? {
        return dao.getRandomDailyVerse(translation)
    }

    fun observeBibleNotes(): Flow<List<BibleNoteEntity>> {
        return dao.observeBibleNotes()
    }

    suspend fun saveBibleNote(note: BibleNoteEntity) {
        dao.saveBibleNote(note)
    }

    fun observeReadingPlans(): Flow<List<ReadingPlanEntity>> {
        return readingPlanDao.observePlans()
    }

    fun observeReadingPlanDays(): Flow<List<ReadingPlanDayEntity>> {
        return readingPlanDao.observePlanDays()
    }

    fun observeReadingPlanCompletions(): Flow<List<ReadingPlanCompletionEntity>> {
        return readingPlanDao.observeCompletions()
    }

    suspend fun startReadingPlan(
        plan: ReadingPlanEntity,
        days: List<ReadingPlanDayEntity>
    ): Long {
        return readingPlanDao.insertPlanWithDays(plan, days)
    }

    suspend fun completeReadingPlanDay(planDayId: Long, completedEpochDay: Long) {
        readingPlanDao.completeDay(
            ReadingPlanCompletionEntity(
                planDayId = planDayId,
                completedEpochDay = completedEpochDay
            )
        )
    }

    suspend fun uncompleteReadingPlanDay(planDayId: Long) {
        readingPlanDao.uncompleteDay(planDayId)
    }

    suspend fun deleteReadingPlan(planId: Long) {
        readingPlanDao.deletePlan(planId)
    }


    suspend fun addBookmark(book: String, chapter: Int, verse: Int, note: String?) {
        // bibleDao.insertBookmark(
        BookmarkEntity(
            book = book,
            chapter = chapter,
            verse = verse,
            note = note,
            color = "purple"
        )
        // )
    }

    suspend fun recordReading(book: String, chapter: Int) {
//        bibleDao.insertReadingHistory(
//            ReadingHistoryEntity(
//                book = book,
//                chapter = chapter
//            )
        // )
    }


    // Popular Hugging Face instruction-tuned models
    companion object {
        const val HF_CHAT_MODEL = "meta-llama/Llama-3.1-8B-Instruct:cerebras"
        const val LLAMA_2_7B_CHAT = "meta-llama/Llama-2-7b-chat-hf"
        const val ZEPHYR_7B_BETA = "HuggingFaceH4/zephyr-7b-beta"
        const val PHI_2 = "microsoft/phi-2"
        const val MIXTRAL_8X7B = "mistralai/Mixtral-8x7B-Instruct-v0.1"
    }

    private fun buildMistralPrompt(instruction: String): String {
        return "<s>[INST] $instruction [/INST]"
    }

    // Helper function to extract JSON from Mistral's response
    private fun extractJson(text: String): String {
        // Try to find JSON object in the response
        val jsonStart = text.indexOf("{")
        val jsonEnd = text.lastIndexOf("}") + 1

        return if (jsonStart >= 0 && jsonEnd > jsonStart) {
            text.substring(jsonStart, jsonEnd)
        } else {
            text.trim()
        }
    }

}
