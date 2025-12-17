package com.example.choiceiomobile.ui.screens.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.choiceiomobile.ui.components.buttons.BaseButton
import com.example.choiceiomobile.ui.theme.Montserrat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    navController: NavController? = null,
    onProfileClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "favourite",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    // Левая кнопка - Стрелка назад (синяя)
                    Box(
                        modifier = Modifier
                            .padding(start = 9.dp) // Добавляем отступ слева
                    ) {
                        TopAppBarButton(
                            onClick = { navController?.popBackStack() },
                            icon = Icons.Filled.ArrowBack,
                            backgroundColor = Color(0xFF0022FF),
                            iconColor = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                },
                actions = {
                    // Правая кнопка - Профиль (теперь тоже синяя)
                    Box(
                        modifier = Modifier
                            .padding(end = 9.dp) // Добавляем отступ справа
                    ) {
                        TopAppBarButton(
                            onClick = onProfileClick,
                            icon = Icons.Filled.Person,
                            backgroundColor = Color(0xFF0022FF), // Синий цвет
                            iconColor = Color.White, // Белая иконка
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
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Ваш список избранного
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                BaseButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "edit",
                    onClick = {
                        // Редактирование избранного
                    }
                )
            }
        }
    }
}