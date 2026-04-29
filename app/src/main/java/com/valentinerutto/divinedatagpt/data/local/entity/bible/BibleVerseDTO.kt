package com.valentinerutto.divinedatagpt.data.local.entity.bible


import com.google.gson.annotations.SerializedName

data class BibleVerseDto(

    @SerializedName("id")
    val id: Int,

    @SerializedName("book_id")
    val bookId: Int,

    @SerializedName("book_name")
    val bookName: String,

    @SerializedName("chapter")
    val chapter: Int,

    @SerializedName("verse")
    val verse: Int,

    @SerializedName("world_english_bible_web")
    val worldEnglishBibleWeb: String? = null,

    @SerializedName("king_james_bible_kjv")
    val kingJamesBibleKjv: String? = null,

    @SerializedName("leningrad_codex")
    val leningradCodex: String? = null,

    @SerializedName("jewish_publication_society_jps")
    val jewishPublicationSocietyJps: String? = null,

    @SerializedName("codex_alexandrinus")
    val codexAlexandrinus: String? = null,

    @SerializedName("brenton")
    val brenton: String? = null,

    @SerializedName("samaritan_pentateuch")
    val samaritanPentateuch: String? = null,

    @SerializedName("samaritan_pentateuch_english")
    val samaritanPentateuchEnglish: String? = null,

    @SerializedName("onkelos_aramaic")
    val onkelosAramaic: String? = null,

    @SerializedName("onkelos_english")
    val onkelosEnglish: String? = null
)

data class BibleJson(
    val metadata: BibleMetadata,
    val verses: List<VerseJson>
)

data class BibleMetadata(
    val name: String,
    val shortname: String,
    val module: String,
    val year: String?
)

data class VerseJson(
    @SerializedName("book_name")
    val bookName: String,
    val book: Int,
    val chapter: Int,
    val verse: Int,
    val text: String
)

