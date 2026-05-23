package com.valentinerutto.divinedatagpt.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.navDeepLink
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.valentinerutto.divinedatagpt.ui.theme.screens.BibleNotesRoute
import com.valentinerutto.divinedatagpt.ui.theme.screens.BibleReaderRoute
import com.valentinerutto.divinedatagpt.ui.theme.screens.DailyReflectionScreen
import com.valentinerutto.divinedatagpt.ui.theme.screens.HomeScreen
import com.valentinerutto.divinedatagpt.ui.theme.screens.ReadingPlansRoute
import com.valentinerutto.divinedatagpt.ui.theme.screens.ReflectionScreen


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
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
                onReadingPlans = {
                    navController.navigate(Screen.ReadingPlans.route)
                },
                onNavigateToBible = {
                    navController.navigate(Screen.Bible.route)
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
        composable(
            route = Screen.Daily.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "divinedatagpt://daily-reflection"
                }
            )
        ) {

            DailyReflectionScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Bible.route) {
            BibleReaderRoute(
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
//                        popUpTo(Screen.Home.route) {
//                            inclusive = false
//                        }
//                        launchSingleTop = true
                    }
                },
                onBibleClick = {
                    navController.navigate(Screen.Bible.route)
                },
                onNotesClick = {
                    navController.navigate(Screen.BibleNotes.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.ReadingPlans.route) {
            ReadingPlansRoute(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.BibleNotes.route) {
            BibleNotesRoute(
                onHomeClick = {
                    navController.navigate(Screen.Home.route)
                },
                onBibleClick = {
                    navController.navigate(Screen.Bible.route)
                },
                onNotesClick = {
                    navController.navigate(Screen.BibleNotes.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
