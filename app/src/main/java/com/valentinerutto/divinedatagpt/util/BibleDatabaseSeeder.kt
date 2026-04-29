package com.valentinerutto.divinedatagpt.util


import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.valentinerutto.divinedatagpt.data.local.dao.VerseDao
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleJson
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseJson
import com.valentinerutto.divinedatagpt.data.local.entity.bible.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BibleDatabaseSeeder(
    private val context: Context,
    private val dao: VerseDao,
) {

    companion object {
        private const val TAG = "BibleDatabaseSeeder"
        private const val ASSET_FILE = "AlamoPolyglot2.json"
        private const val ASSET_FILE_WEB = "web.json"
        private const val ASSET_FILE_KJV = "kjv.json"
        private const val BATCH_SIZE = 500
    }

    suspend fun seedIfEmpty() = withContext(Dispatchers.IO) {

        if (dao.count() > 0) return@withContext

        Log.d(TAG, "Seeding Bible database from $ASSET_FILE …")


        val gson = Gson()
        val batch = mutableListOf<VerseEntity>()
        var shortName = "WEB"

        context.assets.open(ASSET_FILE_WEB).bufferedReader().use { reader ->
            val jsonReader = JsonReader(reader)

            jsonReader.beginObject()

            while (jsonReader.hasNext()) {
                when (jsonReader.nextName()) {
                    "metadata" -> {
                        gson.fromJson<BibleJson>(
                            jsonReader,
                            BibleJson::class.java
                        )
                        shortName = "shortname"
                    }

                    "verses" -> {
                        jsonReader.beginArray()

                        while (jsonReader.hasNext()) {
                            val verse = gson.fromJson<VerseJson>(
                                jsonReader,
                                VerseJson::class.java
                            )

                            batch += verse.toEntity(translation = shortName)

                            if (batch.size >= 500) {
                                dao.insertAll(batch)
                                batch.clear()
                            }
                        }

                        jsonReader.endArray()
                    }

                    else -> jsonReader.skipValue()
                }
            }

            jsonReader.endObject()
        }

        if (batch.isNotEmpty()) {
            dao.insertAll(batch)
        }

    }

    }






