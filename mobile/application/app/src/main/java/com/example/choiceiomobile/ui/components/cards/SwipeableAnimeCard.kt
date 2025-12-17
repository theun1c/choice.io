package com.example.choiceiomobile.ui.components.cards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.choiceiomobile.ui.feed.AnimeUiModel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.absoluteValue

@Composable
fun SwipeableAnimeCard(
    key: Int,
    anime: AnimeUiModel,
    onSwipedLeft: () -> Unit = {},
    onSwipedRight: () -> Unit = {},
    onDragUpdate: (Float) -> Unit = {}, // НОВЫЙ параметр для отслеживания свайпа
    modifier: Modifier = Modifier
) {
    var isVisible by remember(key) { mutableStateOf(true) }

    if (!isVisible) {
        return
    }

    val offsetXAnim = remember(key) { Animatable(0f) }
    val rotationZAnim = remember(key) { Animatable(0f) }
    val rotationYAnim = remember(key) { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    var isFlipped by remember(key) { mutableStateOf(false) }

    val displayTitle = anime.englishTitle ?: anime.title

    // Отслеживаем изменение offset и передаем в onDragUpdate
    LaunchedEffect(offsetXAnim.value) {
        onDragUpdate(offsetXAnim.value)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .graphicsLayer {
                translationX = offsetXAnim.value
                rotationZ = rotationZAnim.value
                rotationY = rotationYAnim.value

                cameraDistance = 8 * density

                alpha = if (offsetXAnim.value > 500f || offsetXAnim.value < -500f) {
                    (1f - (abs(offsetXAnim.value) - 500f) / 500f).coerceIn(0f, 1f)
                } else {
                    1f
                }
            }
            .pointerInput(key) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        if (isVisible && !isFlipped) {
                            change.consume()
                            coroutineScope.launch {
                                offsetXAnim.snapTo(offsetXAnim.value + dragAmount.x)
                                rotationZAnim.snapTo(offsetXAnim.value * 0.05f)
                            }
                        }
                    },
                    onDragEnd = {
                        if (isVisible && !isFlipped) {
                            coroutineScope.launch {
                                when {
                                    offsetXAnim.value > 200f -> {
                                        onSwipedRight()
                                        isVisible = false
                                        offsetXAnim.animateTo(1000f, tween(300))
                                        rotationZAnim.animateTo(30f, tween(300))
                                    }
                                    offsetXAnim.value < -200f -> {
                                        onSwipedLeft()
                                        isVisible = false
                                        offsetXAnim.animateTo(-1000f, tween(300))
                                        rotationZAnim.animateTo(-30f, tween(300))
                                    }
                                    else -> {
                                        offsetXAnim.animateTo(0f, tween(300))
                                        rotationZAnim.animateTo(0f, tween(300))
                                    }
                                }
                            }
                        }
                    }
                )
            }
            .pointerInput(key) {
                detectTapGestures(
                    onDoubleTap = {
                        coroutineScope.launch {
                            val newFlippedState = !isFlipped
                            isFlipped = newFlippedState
                            rotationYAnim.animateTo(
                                if (newFlippedState) 180f else 0f,
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    }
                )
            }
            .clip(RoundedCornerShape(24.dp))
    ) {
        // УБИРАЕМ цветные фоны из карточки - они теперь в FeedScreen

        // Передняя сторона карточки
        if (rotationYAnim.value < 90f) {
            FrontCardContent(
                anime = anime,
                displayTitle = displayTitle,
                offsetX = offsetXAnim.value,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Задняя сторона карточки
        if (rotationYAnim.value >= 90f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = 180f
                    }
            ) {
                BackCardContent(
                    anime = anime,
                    displayTitle = displayTitle,
                    onBackClick = {
                        coroutineScope.launch {
                            isFlipped = false
                            rotationYAnim.animateTo(
                                0f,
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Подсказка для переворота
        if (!isFlipped && offsetXAnim.value.absoluteValue < 50) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Двойной тап",
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Рамка
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(24.dp))
                .drawBehind {
                    drawRect(
                        color = Color.White.copy(alpha = 0.1f),
                        size = size,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 1.dp.toPx()
                        )
                    )
                }
        )
    }
}

@Composable
private fun FrontCardContent(
    anime: AnimeUiModel,
    displayTitle: String,
    offsetX: Float, // Добавляем offsetX для внутренних эффектов
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF1a1a1a))
    ) {
        // Загрузка изображения
        if (!anime.imageUrl.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = anime.imageUrl,
                    error = rememberAsyncImagePainter(
                        model = null
                    )
                ),
                contentDescription = displayTitle,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Слегка затемняем изображение при сильном свайпе для лучшего эффекта
            if (offsetX.absoluteValue > 100) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(
                                alpha = offsetX.absoluteValue / 800f * 0.3f
                            )
                        )
                )
            }
        }

        // Затемнение сверху
        Box(
            modifier = Modifier
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
                .align(Alignment.TopCenter)
        )

        // Затемнение снизу для заголовка
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // Заголовок
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column {
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp
                    ),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                // Оригинальное название если отличается
                if (anime.englishTitle != null && anime.englishTitle != anime.title && displayTitle == anime.englishTitle) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = anime.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Italic
                        ),
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun BackCardContent(
    anime: AnimeUiModel,
    displayTitle: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1a1a1a),
                        Color(0xFF2d2d2d),
                        Color(0xFF1a1a1a)
                    )
                )
            )
    ) {
        // Кнопка возврата
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.White
                )
            }
        }

        // Контент задней стороны
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Заголовок
            Text(
                text = displayTitle,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Разделитель
            Divider(
                color = Color.White.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Описание на английском или оригинальное
            val description = anime.fullSynopsis ?: anime.shortSynopsis
            if (!description.isNullOrEmpty()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    ),
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Описание отсутствует",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f),
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            // Рейтинг (если есть)
            if (anime.score != "N/A") {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2d2d2d)
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "⭐ ${anime.score}/10",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}