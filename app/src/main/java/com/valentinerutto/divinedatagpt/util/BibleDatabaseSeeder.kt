package com.valentinerutto.divinedatagpt.util


import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.valentinerutto.divinedatagpt.data.local.dao.BibleDao
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleVerseDto
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleVerseEntity2
import com.valentinerutto.divinedatagpt.util.BibleDatabaseSeeder.Companion.BATCH_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * One-time seeder: reads `assets/bible.json`, converts each DTO to an [BibleVerseEntity2],
 * and batch-inserts into Room in chunks of [BATCH_SIZE] to avoid OOM on large files.
 *
 * The seeder is idempotent – it checks [BibleVerseDao.count] first and skips seeding
 * if the table is already populated.
 */
class BibleDatabaseSeeder(
    private val context: Context,
    private val dao: BibleDao,
    private val gson: Gson
) {

    companion object {
        private const val TAG = "BibleDatabaseSeeder"
        private const val ASSET_FILE = "AlamoPolyglot2.json"
        private const val BATCH_SIZE = 500
    }

    /**
     * Seeds the database from the JSON asset if it has not been seeded yet.
     *
     * Should be called once at app startup (e.g. in [Application.onCreate] or via
     * an initializer).  Suspends on [Dispatchers.IO] so it is safe to call from a
     * coroutine scope without blocking the main thread.
     *
     * @return `true` if seeding actually ran, `false` if already seeded.
     */
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
                        // Gson reads + deserialises exactly one { … } object at a time
                        val dto: BibleVerseDto =
                            gson.fromJson(jsonReader, BibleVerseDto::class.java)
                        batch.add(dto.toEntity())

                        if (batch.size >= BATCH_SIZE) {
                            dao.insertAll(batch)
                            totalInserted += batch.size
                            Log.v(TAG, "Inserted $totalInserted verses so far…")
                            batch.clear()                        // release before next batch
                        }
                    }

                    jsonReader.endArray()                        // ]
                }
            }

            // Flush any remaining verses in the final partial batch
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

    // ── Mapping ───────────────────────────────────────────────────────────────

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
}