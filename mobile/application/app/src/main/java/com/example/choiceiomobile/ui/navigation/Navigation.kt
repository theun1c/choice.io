package com.example.choiceiomobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.choiceiomobile.ui.screens.auth.LoginScreen
import com.example.choiceiomobile.ui.screens.auth.RegisterScreen
import com.example.choiceiomobile.ui.screens.mood.FavouritesScreen
import com.example.choiceiomobile.ui.screens.mood.FeedScreen
import com.example.choiceiomobile.ui.screens.mood.MoodScreen
import com.example.choiceiomobile.ui.screens.user.ProfileScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "FeedScreen"
    ) {
        composable("LoginScreen") {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate("RegisterScreen")
                }
            )
        }

        composable("RegisterScreen") {
            RegisterScreen(
                onLoginClick = {
                    navController.navigate("LoginScreen")
                }
            )
        }

        composable("FavouritesScreen") {
            FavouritesScreen()
        }

        composable ("FeedScreen") {
            FeedScreen(
                onMoodChoiceClick = {
                    navController.navigate("MoodScreen")
                }
            )
        }

        composable("MoodScreen") {
            MoodScreen(
                onApproveClick = {
                    navController.navigate("FeedScreen")
                }
            )
        }

        composable("ProfileScreen"){
            ProfileScreen()
        }
    }
}