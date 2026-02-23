package com.valentinerutto.divinedatagpt.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.valentinerutto.divinedatagpt.ui.theme.screens.BibleScreen
import com.valentinerutto.divinedatagpt.ui.theme.screens.DailyReflectionScreen
import com.valentinerutto.divinedatagpt.ui.theme.screens.HomeScreen
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
                onBible = {
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
        composable(Screen.Daily.route) {
            BibleScreen()

            DailyReflectionScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Bible.route) {
            BibleScreen()
        }
    }
}