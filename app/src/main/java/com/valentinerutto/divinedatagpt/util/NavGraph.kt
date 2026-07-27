package com.valentinerutto.divinedatagpt.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.valentinerutto.divinedatagpt.ui.theme.screens.BibleNotesRoute
import com.valentinerutto.divinedatagpt.ui.theme.screens.BibleReaderRoute
import com.valentinerutto.divinedatagpt.ui.theme.screens.HomeScreen
import com.valentinerutto.divinedatagpt.ui.theme.screens.JournalScreen
import com.valentinerutto.divinedatagpt.ui.theme.screens.ReflectionScreen


@Composable
fun NavGraph(
    navController: NavHostController, modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route, modifier = modifier

    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onEmotionSelected = { emotion ->
                    navController.navigate(Screen.Reflection.createRoute(emotion))
                },
                onStartReflection = {
                    navController.navigate(Screen.Reflection.createRoute("general"))
                },
                onDailyReflection = {
                    navController.navigate(Screen.Daily.route)
                },
                onJournal = {
                    navController.navigate(Screen.Journal.route)
                },
                onChatClick = {
                    navController.navigate(Screen.Reflection.createRoute("general"))
                }
            )
        }

        composable(Screen.Reflection.route) { backStack ->
            val emotion = backStack.arguments?.getString("emotion") ?: "general"
            ReflectionScreen(
                emotion = emotion,
                onBack = { navController.popBackStack() },
                onViewDaily = { navController.navigate(Screen.Daily.route) }
            )
        }

        composable(Screen.Bible.route) {
            BibleReaderRoute()
            // onHomeClick / onBibleClick / onNotesClick / onSettingsClick removed —
            // BibleReaderRoute no longer draws its own nav bar.
        }

        composable(Screen.BibleNotes.route) {
            BibleNotesRoute()
        }

        composable(Screen.Journal.route) {
            JournalScreen()
        }

    }
}
