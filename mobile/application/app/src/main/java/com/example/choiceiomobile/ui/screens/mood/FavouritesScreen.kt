package com.example.choiceiomobile.ui.screens.mood

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.choiceiomobile.ui.components.buttons.BaseButton
import com.example.choiceiomobile.ui.favourites.FavouritesManager
import com.example.choiceiomobile.ui.theme.Montserrat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    navController: NavController? = null,
    onProfileClick: () -> Unit = {}
) {
    // Используем менеджер вместо ViewModel
    val favouriteIds by FavouritesManager.favouriteIds.collectAsState()

    // Состояние для анимации удаления
    var removingIds by remember { mutableStateOf<Set<Int>>(emptySet()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "favourite (${favouriteIds.size})",
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
            if (favouriteIds.isEmpty()) {
                EmptyFavouritesScreen()
            } else {
                FavouritesList(
                    favouriteIds = favouriteIds.toList().sorted(),
                    removingIds = removingIds,
                    onRemoveClick = { animeId ->
                        // Добавляем в список удаляемых для анимации
                        removingIds = removingIds + animeId

                        // Удаляем через 300ms (после анимации)
                        kotlinx.coroutines.GlobalScope.launch {
                            delay(300)
                            FavouritesManager.removeFavourite(animeId)
                            removingIds = removingIds - animeId
                        }
                    }
                )

                // Кнопка очистки
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    BaseButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "clean all",
                        onClick = {
                            // Добавляем все ID в список удаляемых
                            removingIds = favouriteIds.toSet()

                            // Удаляем через анимацию
                            kotlinx.coroutines.GlobalScope.launch {
                                delay(300)
                                favouriteIds.forEach { id ->
                                    FavouritesManager.removeFavourite(id)
                                }
                                removingIds = emptySet()
                            }
                        },
                        enabled = favouriteIds.isNotEmpty()
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyFavouritesScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Иконка сердечка
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFF2D2D2D), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Пустое избранное",
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(60.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Нет избранных аниме",
                    color = Color.White,
                    fontFamily = Montserrat,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Свайпайте аниме вправо в ленте,\nчтобы добавить их сюда",
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = Montserrat,
                    fontSize = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
fun FavouritesList(
    favouriteIds: List<Int>,
    removingIds: Set<Int>,
    onRemoveClick: (Int) -> Unit
) {
    // Используем AnimatedVisibility для анимации появления/исчезновения
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        favouriteIds.forEach { animeId ->
            // Пропускаем карточки, которые сейчас удаляются
            if (animeId !in removingIds) {
                item {
                    AnimatedVisibility(
                        visible = animeId !in removingIds,
                        enter = fadeIn() + slideInVertically(animationSpec = tween(300)),
                        exit = fadeOut() + slideOutVertically(animationSpec = tween(300))
                    ) {
                        FavouriteAnimeCard(
                            animeId = animeId,
                            onRemoveClick = { onRemoveClick(animeId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavouriteAnimeCard(
    animeId: Int,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1a1a1a)
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Заглушка для изображения (в будущем можно загружать по ID)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2D2D2D),
                                Color(0xFF1a1a1a)
                            )
                        )
                    )
            ) {
                // Здесь могло бы быть изображение аниме
                // Image(painter = rememberAsyncImagePainter(model = "url"), ...)

                // Временный номер ID
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(Color(0xFF0022FF).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "ID: $animeId",
                        color = Color.White,
                        fontFamily = Montserrat,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Затемнение сверху для текста
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.7f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Затемнение снизу
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // Информация об аниме
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Здесь мог бы быть заголовок аниме
                Text(
                    text = "Аниме #$animeId",
                    color = Color.White,
                    fontFamily = Montserrat,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Здесь мог бы быть жанр или описание
                Text(
                    text = "Добавлено в избранное",
                    color = Color.White.copy(alpha = 0.8f),
                    fontFamily = Montserrat,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Кнопка удаления
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            ) {
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier
                        .background(Color(0xFFF44336), RoundedCornerShape(12.dp))
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Удалить из избранного",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Рамка
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        ),
                        alpha = 0.5f
                    )
            )

            // Тонкая белая рамка
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(24.dp)
                    )
            )
        }
    }
}

// Версия с загружаемыми изображениями (если есть доступ к данным аниме)
@Composable
fun FavouriteAnimeCardWithImage(
    animeId: Int,
    title: String? = null,
    imageUrl: String? = null,
    genres: List<String>? = null,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1a1a1a)
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Изображение аниме
            if (!imageUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = imageUrl,
                        error = rememberAsyncImagePainter(model = null)
                    ),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Заглушка если нет изображения
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF2D2D2D),
                                    Color(0xFF1a1a1a)
                                )
                            )
                        )
                )
            }

            // Затемнение сверху
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.7f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Затемнение снизу
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // Информация об аниме
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Заголовок
                Text(
                    text = title ?: "Аниме #$animeId",
                    color = Color.White,
                    fontFamily = Montserrat,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Жанры (если есть)
                genres?.takeIf { it.isNotEmpty() }?.let { genreList ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = genreList.joinToString(", ").take(50) + if (genreList.joinToString(", ").length > 50) "..." else "",
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = Montserrat,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } ?: run {
                    Text(
                        text = "Добавлено в избранное",
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = Montserrat,
                        fontSize = 14.sp
                    )
                }
            }

            // Кнопка удаления
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            ) {
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier
                        .background(Color(0xFFF44336), RoundedCornerShape(12.dp))
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Удалить из избранного",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Рамка
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(24.dp)
                    )
            )
        }
    }
}
