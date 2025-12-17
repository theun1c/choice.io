package com.example.choiceiomobile.ui.screens.mood
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.choiceiomobile.ui.components.buttons.BaseButton
import com.example.choiceiomobile.ui.components.inputs.BaseTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodScreen(
    onApproveClick: (String) -> Unit
) {
    var selectedMood by remember { mutableStateOf<String?>(null) }

    Scaffold (
        topBar = {
            TopAppBar(title = { Text("Mood") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                listOf("happy", "sad", "calm", "energetic").forEach { mood ->
                    MoodButton(
                        mood = mood,
                        isSelected = selectedMood == mood,
                        onClick = {
                            if (selectedMood == mood) {
                                // Клик на уже выбранную - снимаем выбор
                                selectedMood = null
                            } else {
                                // Выбираем новое настроение
                                selectedMood = mood
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                BaseButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "approve",
                    onClick = {
                        selectedMood?.let { mood ->
                            onApproveClick(mood)
                        }
                    },
                    enabled = selectedMood != null
                )
            }
        }
    }
}

@Composable
private fun MoodButton(
    mood: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // выключаем  isColorChangeable
    BaseButton(
        modifier = Modifier.fillMaxWidth(),
        text = mood,
        onClick = onClick,
        initialIsWhiteTheme = !isSelected,
        isColorChangeable = false, // ВАЖНО отключаем авто-переключение
        enabled = true
    )
}