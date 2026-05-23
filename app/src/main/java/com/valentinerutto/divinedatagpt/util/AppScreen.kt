package com.valentinerutto.divinedatagpt.util

enum class AppScreen {
    HOME,
    REFLECTION,
    BIBLE,
    NOTES,
    SETTINGS
}
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Bible : Screen("bible")
    object BibleNotes : Screen("bible_notes")
    object ReadingPlans : Screen("reading_plans")
    object Journal : Screen("journal")
    object Reflection : Screen("reflection/{emotion}") {
        fun createRoute(emotion: String) = "reflection/$emotion"
    }

    object Daily : Screen("daily")

}
