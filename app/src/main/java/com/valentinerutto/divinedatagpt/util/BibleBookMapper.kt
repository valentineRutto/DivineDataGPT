package com.valentinerutto.divinedatagpt.util


data class BookInfo(
    val displayName: String,
    val order: Int,
    val testament: String,
    val abbreviation: String
)

object BibleBookMapper {

    private val bookMapping = mapOf(
        "Genesis" to BookInfo("Genesis", 1, "Old Testament", "gen"),
        "Exodus" to BookInfo("Exodus", 2, "Old Testament", "exo"),
        "Leviticus" to BookInfo("Leviticus", 3, "Old Testament", "lev"),
        "Numbers" to BookInfo("Numbers", 4, "Old Testament", "num"),
        "Deuteronomy" to BookInfo("Deuteronomy", 5, "Old Testament", "deu"),
        "Joshua" to BookInfo("Joshua", 6, "Old Testament", "jos"),
        "Judges" to BookInfo("Judges", 7, "Old Testament", "jdg"),
        "Ruth" to BookInfo("Ruth", 8, "Old Testament", "rut"),
        "1 Samuel" to BookInfo("1 Samuel", 9, "Old Testament", "1sa"),
        "2 Samuel" to BookInfo("2 Samuel", 10, "Old Testament", "2sa"),
        "1 Kings" to BookInfo("1 Kings", 11, "Old Testament", "1ki"),
        "2 Kings" to BookInfo("2 Kings", 12, "Old Testament", "2ki"),
        "1 Chronicles" to BookInfo("1 Chronicles", 13, "Old Testament", "1ch"),
        "2 Chronicles" to BookInfo("2 Chronicles", 14, "Old Testament", "2ch"),
        "Ezra" to BookInfo("Ezra", 15, "Old Testament", "ezr"),
        "Nehemiah" to BookInfo("Nehemiah", 16, "Old Testament", "neh"),
        "Esther" to BookInfo("Esther", 17, "Old Testament", "est"),
        "Job" to BookInfo("Job", 18, "Old Testament", "job"),
        "Psalms" to BookInfo("Psalms", 19, "Old Testament", "psa"),
        "Proverbs" to BookInfo("Proverbs", 20, "Old Testament", "pro"),
        "Ecclesiastes" to BookInfo("Ecclesiastes", 21, "Old Testament", "ecc"),
        "Song of Solomon" to BookInfo("Song of Solomon", 22, "Old Testament", "sng"),
        "Isaiah" to BookInfo("Isaiah", 23, "Old Testament", "isa"),
        "Jeremiah" to BookInfo("Jeremiah", 24, "Old Testament", "jer"),
        "Lamentations" to BookInfo("Lamentations", 25, "Old Testament", "lam"),
        "Ezekiel" to BookInfo("Ezekiel", 26, "Old Testament", "ezk"),
        "Daniel" to BookInfo("Daniel", 27, "Old Testament", "dan"),
        "Hosea" to BookInfo("Hosea", 28, "Old Testament", "hos"),
        "Joel" to BookInfo("Joel", 29, "Old Testament", "jol"),
        "Amos" to BookInfo("Amos", 30, "Old Testament", "amo"),
        "Obadiah" to BookInfo("Obadiah", 31, "Old Testament", "oba"),
        "Jonah" to BookInfo("Jonah", 32, "Old Testament", "jon"),
        "Micah" to BookInfo("Micah", 33, "Old Testament", "mic"),
        "Nahum" to BookInfo("Nahum", 34, "Old Testament", "nam"),
        "Habakkuk" to BookInfo("Habakkuk", 35, "Old Testament", "hab"),
        "Zephaniah" to BookInfo("Zephaniah", 36, "Old Testament", "zep"),
        "Haggai" to BookInfo("Haggai", 37, "Old Testament", "hag"),
        "Zechariah" to BookInfo("Zechariah", 38, "Old Testament", "zec"),
        "Malachi" to BookInfo("Malachi", 39, "Old Testament", "mal"),

        // New Testament - 27 books
        "Matthew" to BookInfo("Matthew", 40, "New Testament", "mat"),
        "Mark" to BookInfo("Mark", 41, "New Testament", "mrk"),
        "Luke" to BookInfo("Luke", 42, "New Testament", "luk"),
        "John" to BookInfo("John", 43, "New Testament", "jhn"),
        "Acts" to BookInfo("Acts", 44, "New Testament", "act"),
        "Romans" to BookInfo("Romans", 45, "New Testament", "rom"),
        "1 Corinthians" to BookInfo("1 Corinthians", 46, "New Testament", "1co"),
        "2 Corinthians" to BookInfo("2 Corinthians", 47, "New Testament", "2co"),
        "Galatians" to BookInfo("Galatians", 48, "New Testament", "gal"),
        "Ephesians" to BookInfo("Ephesians", 49, "New Testament", "eph"),
        "Philippians" to BookInfo("Philippians", 50, "New Testament", "php"),
        "Colossians" to BookInfo("Colossians", 51, "New Testament", "col"),
        "1 Thessalonians" to BookInfo("1 Thessalonians", 52, "New Testament", "1th"),
        "2 Thessalonians" to BookInfo("2 Thessalonians", 53, "New Testament", "2th"),
        "1 Timothy" to BookInfo("1 Timothy", 54, "New Testament", "1ti"),
        "2 Timothy" to BookInfo("2 Timothy", 55, "New Testament", "2ti"),
        "Titus" to BookInfo("Titus", 56, "New Testament", "tit"),
        "Philemon" to BookInfo("Philemon", 57, "New Testament", "phm"),
        "Hebrews" to BookInfo("Hebrews", 58, "New Testament", "heb"),
        "James" to BookInfo("James", 59, "New Testament", "jas"),
        "1 Peter" to BookInfo("1 Peter", 60, "New Testament", "1pe"),
        "2 Peter" to BookInfo("2 Peter", 61, "New Testament", "2pe"),
        "1 John" to BookInfo("1 John", 62, "New Testament", "1jn"),
        "2 John" to BookInfo("2 John", 63, "New Testament", "2jn"),
        "3 John" to BookInfo("3 John", 64, "New Testament", "3jn"),
        "Jude" to BookInfo("Jude", 65, "New Testament", "jud"),
        "Revelation" to BookInfo("Revelation", 66, "New Testament", "rev")
    )

    fun getBookInfo(bookName: String): BookInfo? {
        return bookMapping[bookName]
    }

    fun getAllBooks(): List<Pair<String, BookInfo>> {
        return bookMapping.toList().sortedBy { it.second.order }
    }
}