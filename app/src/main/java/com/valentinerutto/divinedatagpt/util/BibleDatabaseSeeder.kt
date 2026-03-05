package com.valentinerutto.divinedatagpt.util


import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.valentinerutto.divinedatagpt.data.local.dao.BibleDao
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleBookEntity
import com.valentinerutto.divinedatagpt.util.BibleDatabaseSeeder.Companion.BATCH_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * One-time seeder: reads `assets/bible.json`, converts each DTO to an [BibleVerseEntity],
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
        private const val ASSET_FILE = "bible.json"
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
            val json = context.assets.open(ASSET_FILE).bufferedReader().use { it.readText() }

            val type = object : TypeToken<List<BibleBookEntity>>() {}.type
            val dtos: List<BibleVerseDto> = gson.fromJson(json, type)

            Log.d(TAG, "Parsed ${dtos.size} verses – inserting in batches of $BATCH_SIZE …")

            dtos
                .chunked(BATCH_SIZE)
                .forEachIndexed { index, batch ->
                    dao.insertAll(batch.map { it.toEntity() })
                    Log.v(
                        TAG,
                        "Inserted batch ${index + 1} (${(index + 1) * BATCH_SIZE} / ${dtos.size})"
                    )
                }

            Log.d(TAG, "Seeding complete. Total verses in DB: ${dao.count()}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to seed database", e)
            false
        }
    }

    // ── Mapping ───────────────────────────────────────────────────────────────

    private fun BibleVerseDto.toEntity() = BibleVerseEntity(
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