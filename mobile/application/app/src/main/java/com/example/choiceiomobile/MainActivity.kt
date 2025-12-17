package com.example.choiceiomobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.choiceiomobile.ui.navigation.AppNavigation
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