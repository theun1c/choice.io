package com.example.choiceiomobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.choiceiomobile.ui.auth.AuthViewModel
import com.example.choiceiomobile.ui.screens.auth.LoginScreen
import com.example.choiceiomobile.ui.screens.auth.RegisterScreen
import com.example.choiceiomobile.ui.screens.mood.FavouritesScreen
import com.example.choiceiomobile.ui.screens.mood.FeedScreen
import com.example.choiceiomobile.ui.screens.mood.MoodScreen
import com.example.choiceiomobile.ui.screens.user.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "LoginScreen"
    ) {
        composable("LoginScreen") {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate("RegisterScreen")
                },
                onLoginSuccess = {
                    // Получаем userId после успешного логина
                    val userId = authViewModel.userId.value ?: 1
                    navController.navigate("MoodScreen") {
                        popUpTo("LoginScreen") { inclusive = true }
                    }
                }
            )
        }

        composable("RegisterScreen") {
            RegisterScreen(
                onLoginClick = {
                    navController.navigate("LoginScreen")
                },
                onRegisterSuccess = {
                    // Получаем userId после успешной регистрации
                    val userId = authViewModel.userId.value ?: 1
                    navController.navigate("MoodScreen") {
                        popUpTo("LoginScreen") { inclusive = true }
                    }
                }
            )
        }

        composable("FavouritesScreen") { backStackEntry ->
            // Получаем userId для избранного
            val userId = authViewModel.userId.value ?: 1

            FavouritesScreen(
                navController = navController,
                onProfileClick = {
                    navController.navigate("ProfileScreen")
                }
            )
        }

        composable("MoodScreen") {
            MoodScreen(
                onApproveClick = { selectedMood ->
                    if (!selectedMood.isNullOrEmpty()) {
                        navController.navigate("FeedScreen/$selectedMood")
                    }
                },
                onFavoritesClick = {
                    navController.navigate("FavouritesScreen")
                },
                onProfileClick = {
                    navController.navigate("ProfileScreen")
                }
            )
        }

        composable(
            route = "FeedScreen/{mood}",
            arguments = listOf(navArgument("mood") { type = NavType.StringType })
        ) { backStackEntry ->
            val mood = backStackEntry.arguments?.getString("mood") ?: ""
            val userId = authViewModel.userId.value ?: 1

            FeedScreen(
                mood = mood,
                onMoodChoiceClick = { navController.navigate("MoodScreen") },
                onFavoritesClick = {
                    navController.navigate("FavouritesScreen")
                },
                onProfileClick = {
                    navController.navigate("ProfileScreen")
                }
            )
        }

        composable("ProfileScreen") { backStackEntry ->
            // Передаем userId для выхода
            val userId = authViewModel.userId.value ?: 1

            ProfileScreen(
                navController = navController,
                onFavoritesClick = {
                    navController.navigate("FavouritesScreen")
                },
                onLogoutSuccess = {
                    // Очищаем данные и возвращаем на логин
                    authViewModel.logout()
                    navController.navigate("LoginScreen") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}