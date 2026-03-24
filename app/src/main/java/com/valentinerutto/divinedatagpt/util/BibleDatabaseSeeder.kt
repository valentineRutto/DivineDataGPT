package com.valentinerutto.divinedatagpt.util


import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.valentinerutto.divinedatagpt.data.local.dao.BibleDao
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleBookEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleVerseDto
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleVerseEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleVerseEntity2
import com.valentinerutto.divinedatagpt.data.models.KjvBibleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BibleDatabaseSeeder(
    private val context: Context,
    private val dao: BibleDao,
    private val gson: Gson
) {

    companion object {
        private const val TAG = "BibleDatabaseSeeder"
        private const val ASSET_FILE = "AlamoPolyglot2.json"
        private const val ASSET_FILE_WEB = "web_complete.json"
        private const val ASSET_FILE_KJV = "kjv.json"
        private const val BATCH_SIZE = 500
    }

    suspend fun seedIfEmpty(): Boolean = withContext(Dispatchers.IO) {

        if (dao.count() > 0) {
            Log.d(TAG, "Database already seeded – skipping.")
            return@withContext false
        }

        Log.d(TAG, "Seeding Bible database from $ASSET_FILE …")

        try {
            var totalInserted = 0
            val batch = ArrayList<BibleVerseEntity2>(BATCH_SIZE)


            context.assets.open(ASSET_FILE).bufferedReader().use { reader ->

                JsonReader(reader).use { jsonReader ->

                    jsonReader.beginArray()                      // [

                    while (jsonReader.hasNext()) {

                        val dto: BibleVerseDto =
                            gson.fromJson(jsonReader, BibleVerseDto::class.java)

                        batch.add(dto.toEntity())

                        if (batch.size >= BATCH_SIZE) {
                            dao.insertAll(batch)
                            totalInserted += batch.size
                            Log.v(TAG, "Inserted $totalInserted verses so far…")
                            batch.clear()
                        }
                    }

                    jsonReader.endArray()                        // ]
                }
            }

            if (batch.isNotEmpty()) {
                dao.insertAll(batch)
                totalInserted += batch.size
                batch.clear()
            }

            Log.d(TAG, "Seeding complete – $totalInserted verses inserted.")
            true

        } catch (e: Exception) {
            Log.e(TAG, "Failed to seed database", e)
            false
        }
    }

    private fun BibleVerseDto.toEntity() = BibleVerseEntity2(
        id = id,
        bookId = bookId,
        bookName = bookName,
        chapter = chapter,
        verse = verse,
        worldEnglishBibleWeb = worldEnglishBibleWeb,
        kingJamesBibleKjv = kingJamesBibleKjv,
        leningradCodex = leningradCodex,
        jewishPublicationSocietyJps = jewishPublicationSocietyJps,
        codexAlexandrinus = codexAlexandrinus,
        brenton = brenton,
        samaritanPentateuch = samaritanPentateuch,
        samaritanPentateuchEnglish = samaritanPentateuchEnglish,
        onkelosAramaic = onkelosAramaic,
        onkelosEnglish = onkelosEnglish
    )

    suspend fun seedKjvIfEmpty(): Boolean = withContext(Dispatchers.IO) {


        try {

            if (dao.getTotalVerseCount() > 0) {
                Log.d(TAG, "Database kjv already seeded – skipping.")
                return@withContext false
            }


            val inputStream = context.assets.open(ASSET_FILE)
            val reader = inputStream.bufferedReader()
            val jsonText = reader.readText()
            reader.close()
            inputStream.close()

            val kjvData: KjvBibleData? = try {
                gson.fromJson(jsonText, KjvBibleData::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse JSON", e)
                return@withContext false
            }

            // Validate parsed data
            if (kjvData == null) {
                Log.e(TAG, "Parsed data is null")
                return@withContext false
            }

            if (kjvData.books == null) {
                Log.e(TAG, "kjv Books list is null")
                return@withContext false
            }

            if (kjvData.books.isEmpty()) {
                Log.e(TAG, "kjv Books list is empty")
                return@withContext false
            }

            val bookEntities = mutableListOf<BibleBookEntity>()
            val verseEntities = mutableListOf<BibleVerseEntity>()

            kjvData.books.forEach { kjvBook ->

                val bookInfo = BibleBookMapper.getBookInfo(kjvBook.name) ?: return@forEach


                var bookTotalVerses = 0

                // Process each chapter
                kjvBook.chapters.forEachIndexed { chapterIndex, verses ->
                    val chapterNumber = chapterIndex + 1

                    // Process each verse in the chapter
                    verses.forEachIndexed { verseIndex, verseText ->
                        val verseNumber = verseIndex + 1

                        val verse = BibleVerseEntity(
                            book = bookInfo.displayName,
                            bookAbbrev = bookInfo.abbreviation,
                            chapter = chapterNumber,
                            verse = verseNumber,
                            text = verseText.trim(),
                            sectionNumber = 1,
                            bookOrder = bookInfo.order,
                            testament = bookInfo.testament
                        )

                        verseEntities.add(verse)

                        bookTotalVerses++
                    }
                }

                // Create book entity
                val bookEntity = BibleBookEntity(
                    bookName = bookInfo.displayName,
                    abbreviation = bookInfo.abbreviation,
                    bookOrder = bookInfo.order,
                    testament = bookInfo.testament,
                    totalChapters = kjvBook.chapters.size,
                    totalVerses = bookTotalVerses
                )

                bookEntities.add(bookEntity)
                Log.d(
                    TAG,
                    "   ✅ ${bookInfo.displayName}: ${kjvBook.chapters.size} chapters, $bookTotalVerses verses"
                )
            }

            // Insert books
            dao.insertBooks(bookEntities)
            Log.d(TAG, "Inserted ${bookEntities.size} books")

            // Insert verses in batches
            verseEntities.chunked(BATCH_SIZE).forEachIndexed { index, batch ->
                dao.insertVerses(batch)
                val progress = ((index + 1) * BATCH_SIZE).coerceAtMost(verseEntities.size)
                Log.v(TAG, "Inserted $progress / ${verseEntities.size} verses...")
            }

            Log.d(TAG, "kjv Seeding complete – ${verseEntities.size} verses inserted.")
            true
        } catch (e: Exception) {
            e.printStackTrace()

            false
        }


    }

}