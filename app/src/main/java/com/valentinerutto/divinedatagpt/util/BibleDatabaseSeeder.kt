package com.valentinerutto.divinedatagpt.util


import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.valentinerutto.divinedatagpt.data.local.dao.BibleDao
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleVerseDto
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleVerseEntity2
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


        suspend fun preloadIfNeeded() {
            val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)

            if (prefs.getBoolean("is_preloaded", false)) return

            val json = context.assets.open("bible.json")
                .bufferedReader()
                .use { it.readText() }

            val bible = Gson().fromJson(json, BibleJson::class.java)

            insertIntoDb(bible)

            prefs.edit().putBoolean("is_preloaded", true).apply()
        }

        private suspend fun insertIntoDb(bible: BibleJson) {
            var bookId = 1
            var chapterId = 1
            var verseId = 1

            val books = mutableListOf<Book>()
            val chapters = mutableListOf<Chapter>()
            val verses = mutableListOf<Verse>()

            bible.books.forEach { b ->
                books.add(Book(bookId, b.name))

                b.chapters.forEach { ch ->
                    chapters.add(Chapter(chapterId, bookId, ch.chapter))

                    ch.verses.forEach { v ->
                        verses.add(
                            Verse(
                                verseId,
                                chapterId,
                                v.verse,
                                v.text
                            )
                        )
                        verseId++
                    }

                    chapterId++
                }

                bookId++
            }

            // Insert in transaction
            dao.insertBooks(books)
            dao.insertChapters(chapters)
            dao.insertVerses(verses)
        }
    }

