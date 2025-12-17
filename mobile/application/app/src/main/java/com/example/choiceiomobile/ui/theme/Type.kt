package com.example.choiceiomobile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.choiceiomobile.R


val Montserrat = FontFamily(
    Font(R.font.montserratbold, FontWeight.Bold),
    Font(R.font.montserratmedium, FontWeight.Medium),
    Font(R.font.montserratregular, FontWeight.Normal)

)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)