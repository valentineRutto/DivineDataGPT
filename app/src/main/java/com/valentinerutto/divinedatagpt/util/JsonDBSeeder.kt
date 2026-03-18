//package com.valentinerutto.divinedatagpt.util
//
//import android.content.Context
//import com.google.gson.Gson
//import com.valentinerutto.divinedatagpt.data.local.dao.BibleDao
//
//
//class JsonBibleDatabaseSeeder(
//    private val context: Context,
//    private val dao: BibleDao,
//    private val gson: Gson
//) {
//
//    companion object {
//        private const val TAG = "BibleDatabaseSeeder"
//        private const val BATCH_SIZE = 500
//
//        // Asset file paths
//        private const val ASSET_FILE_ALAMO = "AlamoPolyglot2.json"
//        private const val ASSET_FILE_WEB = "web_complete.json"
//        private const val ASSET_FILE_KJV = "kjv.json"
//    }
//
//    /**
//     * Seeds the database from the JSON asset if it has not been seeded yet.
//     *
//     * @param format The JSON format to use for seeding
//     * @return `true` if seeding actually ran, `false` if already seeded.
//     */
//    suspend fun seedIfEmpty(
//        format: BibleJsonFormat = BibleJsonFormat.KJV
//    ): Boolean = withContext(Dispatchers.IO) {
//
//        if (dao.getTotalVerseCount() > 0) {
//            Log.d(TAG, "Database already seeded – skipping.")
//            return@withContext false
//        }
//
//        val assetFile = when (format) {
//            BibleJsonFormat.ALAMO_POLYGLOT -> ASSET_FILE_ALAMO
//            BibleJsonFormat.WEB_COMPLETE -> ASSET_FILE_WEB
//            BibleJsonFormat.KJV -> ASSET_FILE_KJV
//        }
//
//        Log.d(TAG, "Seeding Bible database from $assetFile using format: $format")
//
//        try {
//            when (format) {
//                BibleJsonFormat.ALAMO_POLYGLOT -> seedAlamoPolyglot(assetFile)
//                BibleJsonFormat.WEB_COMPLETE -> seedWebComplete(assetFile)
//                BibleJsonFormat.KJV -> seedKjv(assetFile)
//            }
//            true
//        } catch (e: Exception) {
//            Log.e(TAG, "Failed to seed database", e)
//            false
//        }
//    }
//
//    /**
//     * Seed from AlamoPolyglot2.json format (original streaming approach)
//     */
//    private suspend fun seedAlamoPolyglot(assetFile: String) {
//        var totalInserted = 0
//        val batch = ArrayList<BibleVerseEntity>(BATCH_SIZE)
//
//        context.assets.open(assetFile).bufferedReader().use { reader ->
//            JsonReader(reader).use { jsonReader ->
//                jsonReader.beginArray()
//
//                while (jsonReader.hasNext()) {
//                    val dto: BibleVerseDto = gson.fromJson(jsonReader, BibleVerseDto::class.java)
//                    batch.add(dto.toEntity())
//
//                    if (batch.size >= BATCH_SIZE) {
//                        dao.insertVerses(batch)
//                        totalInserted += batch.size
//                        Log.v(TAG, "Inserted $totalInserted verses so far…")
//                        batch.clear()
//                    }
//                }
//
//                jsonReader.endArray()
//            }
//        }
//
//        // Flush remaining verses
//        if (batch.isNotEmpty()) {
//            dao.insertVerses(batch)
//            totalInserted += batch.size
//        }
//
//        Log.d(TAG, "Seeding complete – $totalInserted verses inserted.")
//    }
//
//    /**
//     * Seed from web_complete.json format
//     */
//    private suspend fun seedWebComplete(assetFile: String) {
//        Log.d(TAG, "Parsing web_complete.json...")
//
//        val inputStream = context.assets.open(assetFile)
//        val reader = inputStream.bufferedReader()
//
//        // Parse as Map<String, List<VerseItem>>
//        val mapType = object : TypeToken<Map<String, List<VerseItem>>>() {}.type
//        val bibleData: Map<String, List<VerseItem>> = gson.fromJson(reader, mapType)
//        reader.close()
//
//        Log.d(TAG, "Parsed ${bibleData.size} books")
//
//        val bookEntities = mutableListOf<BibleBookEntity>()
//        val verseEntities = mutableListOf<BibleVerseEntity>()
//        var bookOrder = 1
//
//        bibleData.forEach { (bookAbbrev, verseItems) ->
//            val bookInfo = BibleBookMapper.getBookInfo(bookAbbrev)
//
//            if (bookInfo == null) {
//                Log.w(TAG, "Unknown book: $bookAbbrev - skipping")
//                return@forEach
//            }
//
//            Log.d(TAG, "Processing ${bookInfo.displayName}...")
//
//            val chaptersMap = mutableMapOf<Int, MutableList<BibleVerseEntity>>()
//            var totalVerses = 0
//
//            // Parse verse items
//            verseItems.forEach { item ->
//                if (item.type == "paragraph text" &&
//                    item.chapterNumber != null &&
//                    item.verseNumber != null &&
//                    item.value != null) {
//
//                    val verse = BibleVerseEntity(
//                        book = bookInfo.displayName,
//                        bookAbbrev = bookAbbrev,
//                        chapter = item.chapterNumber,
//                        verse = item.verseNumber,
//                        text = item.value.trim(),
//                        sectionNumber = item.sectionNumber ?: 1,
//                        bookOrder = bookInfo.order,
//                        testament = bookInfo.testament
//                    )
//
//                    verseEntities.add(verse)
//                    chaptersMap.getOrPut(item.chapterNumber) { mutableListOf() }.add(verse)
//                    totalVerses++
//                }
//            }
//
//            // Create book entity
//            val bookEntity = BibleBookEntity(
//                bookName = bookInfo.displayName,
//                abbreviation = bookAbbrev,
//                bookOrder = bookInfo.order,
//                testament = bookInfo.testament,
//                totalChapters = chaptersMap.keys.maxOrNull() ?: 0,
//                totalVerses = totalVerses
//            )
//
//            bookEntities.add(bookEntity)
//            bookOrder++
//        }
//
//        // Insert books
//        dao.insertBooks(bookEntities)
//        Log.d(TAG, "Inserted ${bookEntities.size} books")
//
//        // Insert verses in batches
//        verseEntities.chunked(BATCH_SIZE).forEachIndexed { index, batch ->
//            dao.insertVerses(batch)
//            val progress = ((index + 1) * BATCH_SIZE).coerceAtMost(verseEntities.size)
//            Log.v(TAG, "Inserted $progress / ${verseEntities.size} verses...")
//        }
//
//        Log.d(TAG, "Seeding complete – ${verseEntities.size} verses inserted.")
//    }
//
//    /**
//     * Seed from kjv.json format
//     */
//    private suspend fun seedKjv(assetFile: String) {
//        Log.d(TAG, "Parsing kjv.json...")
//
//        val inputStream = context.assets.open(assetFile)
//        val reader = inputStream.bufferedReader()
//
//        val kjvData: KjvBibleData = gson.fromJson(reader, KjvBibleData::class.java)
//        reader.close()
//
//        Log.d(TAG, "Parsed ${kjvData.books.size} books")
//
//        val bookEntities = mutableListOf<BibleBookEntity>()
//        val verseEntities = mutableListOf<BibleVerseEntity>()
//
//        kjvData.books.forEach { kjvBook ->
//            val bookInfo = BibleBookMapper.getBookInfo(kjvBook.name)
//
//            if (bookInfo == null) {
//                Log.w(TAG, "Unknown book: ${kjvBook.name} - skipping")
//                return@forEach
//            }
//
//            Log.d(TAG, "Processing ${bookInfo.displayName}...")
//
//            var bookTotalVerses = 0
//
//            // Process each chapter
//            kjvBook.chapters.forEachIndexed { chapterIndex, verses ->
//                val chapterNumber = chapterIndex + 1
//
//                // Process each verse in the chapter
//                verses.forEachIndexed { verseIndex, verseText ->
//                    val verseNumber = verseIndex + 1
//
//                    val verse = BibleVerseEntity(
//                        book = bookInfo.displayName,
//                        bookAbbrev = bookInfo.abbreviation,
//                        chapter = chapterNumber,
//                        verse = verseNumber,
//                        text = verseText.trim(),
//                        sectionNumber = 1,
//                        bookOrder = bookInfo.order,
//                        testament = bookInfo.testament
//                    )
//
//                    verseEntities.add(verse)
//                    bookTotalVerses++
//                }
//            }
//
//            // Create book entity
//            val bookEntity = BibleBookEntity(
//                bookName = bookInfo.displayName,
//                abbreviation = bookInfo.abbreviation,
//                bookOrder = bookInfo.order,
//                testament = bookInfo.testament,
//                totalChapters = kjvBook.chapters.size,
//                totalVerses = bookTotalVerses
//            )
//
//            bookEntities.add(bookEntity)
//            Log.d(TAG, "   ✅ ${bookInfo.displayName}: ${kjvBook.chapters.size} chapters, $bookTotalVerses verses")
//        }
//
//        // Insert books
//        dao.insertBooks(bookEntities)
//        Log.d(TAG, "Inserted ${bookEntities.size} books")
//
//        // Insert verses in batches
//        verseEntities.chunked(BATCH_SIZE).forEachIndexed { index, batch ->
//            dao.insertVerses(batch)
//            val progress = ((index + 1) * BATCH_SIZE).coerceAtMost(verseEntities.size)
//            Log.v(TAG, "Inserted $progress / ${verseEntities.size} verses...")
//        }
//
//        Log.d(TAG, "Seeding complete – ${verseEntities.size} verses inserted.")
//    }
//}
//
///**
// * Extension function to convert DTO to Entity
// */
//private fun BibleVerseDto.toEntity(): BibleVerseEntity {
//    val bookInfo = BibleBookMapper.getBookInfo(this.book)
//
//    return BibleVerseEntity(
//        book = this.book,
//        bookAbbrev = bookInfo?.abbreviation ?: this.book.take(3).lowercase(),
//        chapter = this.chapter,
//        verse = this.verse,
//        text = this.text,
//        sectionNumber = 1,
//        bookOrder = this.bookOrder ?: bookInfo?.order ?: 0,
//        testament = this.testament ?: bookInfo?.testament ?: "Unknown"
//    )
//}