package com.example.choiceiomobile.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
import com.example.choiceiomobile.ui.components.inputs.BaseTextField
import com.example.choiceiomobile.ui.screens.mood.TopAppBarButton
import com.example.choiceiomobile.ui.theme.Montserrat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController? = null,
    onFavoritesClick: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
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
                            .padding(start = 9.dp)
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
                    // Правая кнопка - Избранное (синяя)
                    Box(
                        modifier = Modifier
                            .padding(end = 9.dp)
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
                // Отображение логина пользователя (только для чтения)
                BaseTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "username",
                    value = "guest", // Используем переданный логин
                    onValueChange = {}, // Пустая функция, так как поле только для чтения
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                // Кнопка выхода из аккаунта
                BaseButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "leave",
                    onClick = {
                        onLogoutSuccess()  // Вызываем вместо прямой навигации
                    }
                )
            }
        }
    }
}

