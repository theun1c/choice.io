package com.example.choiceiomobile.ui.screens.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.choiceiomobile.ui.components.buttons.BaseButton
import com.example.choiceiomobile.ui.theme.Montserrat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodScreen(
    onApproveClick: (String) -> Unit,
    onFavoritesClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var selectedMood by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mood",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    // Левая кнопка - Избранное (синяя)
                    Box(
                        modifier = Modifier
                            .padding(start = 9.dp)
                    ) {
                        TopAppBarButton(
                            onClick = onFavoritesClick,
                            icon = Icons.Filled.Favorite,
                            backgroundColor = Color(0xFF0022FF),
                            iconColor = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                },
                actions = {
                    // Правая кнопка - Профиль (синяя)
                    Box(
                        modifier = Modifier
                            .padding(end = 9.dp)
                    ) {
                        TopAppBarButton(
                            onClick = onProfileClick,
                            icon = Icons.Filled.Person,
                            backgroundColor = Color(0xFF0022FF),
                            iconColor = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
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
    BaseButton(
        modifier = Modifier.fillMaxWidth(),
        text = mood,
        onClick = onClick,
        initialIsWhiteTheme = !isSelected,
        isColorChangeable = false,
        enabled = true
    )
}

@Composable
fun TopAppBarButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    iconColor: Color,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(12.dp)),
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = when (icon) {
                Icons.Filled.Favorite -> "Избранное"
                Icons.Filled.Person -> "Профиль"
                else -> "Кнопка"
            },
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}