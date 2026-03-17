package com.valentinerutto.divinedatagpt.data.models

import com.google.gson.annotations.SerializedName

data class KjvBibleData(
    @SerializedName("books")
    val books: List<KjvBook>
)

data class KjvBook(
    @SerializedName("name")
    val name: String,

    @SerializedName("chapters")
    val chapters: List<List<String>>
)