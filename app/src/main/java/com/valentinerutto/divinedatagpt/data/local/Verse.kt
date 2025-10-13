package com.valentinerutto.divinedatagpt.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Verse(@PrimaryKey val id: Int, val text: String, val translation: String, val reference: String)
