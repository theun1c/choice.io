package com.example.choiceiomobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.example.choiceiomobile.ui.components.cards.CardPlaceholder
import com.example.choiceiomobile.ui.navigation.AppNavigation
import com.example.choiceiomobile.ui.screens.auth.LoginScreen
import com.example.choiceiomobile.ui.screens.auth.RegisterScreen
import com.example.choiceiomobile.ui.screens.mood.FavouritesScreen
import com.example.choiceiomobile.ui.screens.mood.FeedScreen
import com.example.choiceiomobile.ui.screens.mood.MoodScreen
import com.example.choiceiomobile.ui.screens.user.ProfileScreen
import com.example.choiceiomobile.ui.theme.ChoiceiomobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChoiceiomobileTheme {
                AppNavigation()
            }
        }
    }
}