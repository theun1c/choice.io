package com.example.choiceiomobile.ui.screens.mood

import android.annotation.SuppressLint
import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.choiceiomobile.ui.components.buttons.BaseButton
import com.example.choiceiomobile.ui.components.cards.SwipeableAnimeCard
import com.example.choiceiomobile.ui.feed.AnimeFeedViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    mood: String,
    onMoodChoiceClick: () -> Unit
) {
    val viewModel: AnimeFeedViewModel = viewModel()

    var currentIndex by remember { mutableStateOf(0) }
    var cardKey by remember { mutableStateOf(0) }

    // Состояние для отслеживания свайпа текущей карточки
    val currentCardOffsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(mood) {
        viewModel.setMood(mood)
        currentIndex = 0
        cardKey = 0
        currentCardOffsetX.snapTo(0f)
    }

    val animeList by viewModel.animeList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val error by viewModel.error.collectAsState()
    val canLoadMore by viewModel.canLoadMore.collectAsState()

    LaunchedEffect(currentIndex, animeList.size) {
        if (animeList.isNotEmpty() &&
            canLoadMore &&
            !isLoadingMore &&
            !isLoading &&
            currentIndex >= animeList.size - 3) {
            viewModel.loadMoreAnime()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (animeList.isNotEmpty()) {
                            "choice.io • $mood • ${currentIndex + 1}"
                        } else {
                            "choice.io • $mood"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.9f)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (isLoading && animeList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (error != null && animeList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = error!!,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        BaseButton(
                            text = "Повторить",
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.refresh()
                                    currentIndex = 0
                                    cardKey = 0
                                    currentCardOffsetX.snapTo(0f)
                                }
                            }
                        )
                    }
                }
            } else if (animeList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет аниме для настроения: $mood",
                        color = Color.White
                    )
                }
            } else {
                // Основной контейнер для карточек
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // 1. Сначала рисуем фон со свечением
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawGlowBehindCard(currentCardOffsetX.value)
                    )

                    // 2. Предварительная карточка (следующая)
                    if (currentIndex + 1 < animeList.size) {
                        SwipeableAnimeCard(
                            key = cardKey + 1000,
                            anime = animeList[currentIndex + 1],
                            onSwipedLeft = { /* неактивная */ },
                            onSwipedRight = { /* неактивная */ },
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = 0.95f
                                    scaleY = 0.95f
                                    alpha = 0.7f
                                }
                        )
                    } else if (isLoadingMore) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = 0.95f
                                    scaleY = 0.95f
                                    alpha = 0.7f
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(color = Color.White)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Загружаем новые аниме...",
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // 3. Активная карточка с отслеживанием свайпа
                    val currentAnime = animeList[currentIndex]

                    SwipeableAnimeCard(
                        key = cardKey,
                        anime = currentAnime,
                        onSwipedLeft = {
                            coroutineScope.launch {
                                cardKey++
                                if (currentIndex < animeList.size - 1) {
                                    currentIndex++
                                }
                                // Сбрасываем offset при свайпе
                                currentCardOffsetX.animateTo(0f, tween(300))
                            }
                        },
                        onSwipedRight = {
                            coroutineScope.launch {
                                cardKey++
                                if (currentIndex < animeList.size - 1) {
                                    currentIndex++
                                }
                                currentCardOffsetX.animateTo(0f, tween(300))
                            }
                        },
                        onDragUpdate = { offsetX ->
                            coroutineScope.launch {
                                currentCardOffsetX.snapTo(offsetX)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Статус загрузки внизу
                if (isLoadingMore) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Загружаем новые аниме...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                // Кнопка выбора настроения внизу
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    BaseButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        text = "choice mood",
                        onClick = onMoodChoiceClick
                    )
                }
            }
        }
    }
}

// Кастомный модификатор для создания эффекта свечения ЗА карточкой
@SuppressLint("SuspiciousModifierThen")
private fun Modifier.drawGlowBehindCard(offsetX: Float): Modifier = this.then(
    drawBehind {
        // Только если есть свайп
        if (abs(offsetX) > 30f) {
            val width = size.width
            val height = size.height

            // Определяем цвет свечения
            val glowColor = when {
                offsetX > 0 -> Color(0xFF4CAF50) // Зеленый для свайпа вправо (LIKE)
                else -> Color(0xFFF44336) // Красный для свайпа влево (PASS)
            }

            // Интенсивность свечения (0-1) - уменьшена для более тусклого эффекта
            val intensity = (abs(offsetX) / 500f).coerceIn(0f, 0.4f)

            // Центральная точка свечения (смещаем выше центра)
            // Поднимаем свечение на 20% выше центра и слегка смещаем по горизонтали
            val centerX = width / 2 + offsetX * 0.3f
            val centerY = height / 2 - height * 0.2f // Поднимаем выше

            // Уменьшенный радиус свечения
            val glowRadius = minOf(width, height) * 0.5f

            // Создаем радиальный градиент для эффекта свечения
            val paint = Paint().apply {
                this.color = glowColor
                this.alpha = intensity * 0.6f // Сделано более тусклым
                this.asFrameworkPaint().apply {
                    maskFilter = BlurMaskFilter(
                        glowRadius * 0.5f, // Уменьшено размытие
                        BlurMaskFilter.Blur.NORMAL
                    )
                }
                blendMode = BlendMode.Screen // Для эффекта свечения
            }

            drawIntoCanvas { canvas ->
                // Рисуем круг свечения
                canvas.drawCircle(
                    center = Offset(centerX, centerY),
                    radius = glowRadius,
                    paint = paint
                )
            }
        }
    }
)