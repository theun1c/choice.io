package com.example.choiceiomobile.ui.components.buttons

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.choiceiomobile.ui.theme.Montserrat

@Composable
fun BaseButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    initialIsWhiteTheme: Boolean = false,
    isColorChangeable: Boolean = false,
    isRedButton: Boolean = false,
    enabled: Boolean = true
) {
    var isWhiteTheme by remember { mutableStateOf(initialIsWhiteTheme) }

    val displayColorIsWhite = if (isColorChangeable) {
        isWhiteTheme // берем состояние которое меняется, если можно менять
    } else {
        initialIsWhiteTheme // берем статический флаг фолз
    }

    var containerColor = if (displayColorIsWhite) Color.White else Color(0xFF0022FF)
    var textColor = if (displayColorIsWhite) Color.Black else Color.White

    if (isRedButton){
        containerColor = Color(0xFFFF383C)
        textColor = Color.White
    }

    Button(

        onClick = {
            if(enabled){
                onClick()
                if (isColorChangeable){
                    isWhiteTheme = !isWhiteTheme
                }
            }
        },
        modifier = modifier
            .width(290.dp)
            .height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(16.dp),


    ) {
        Text(
            text = text,
            fontSize = 30.sp,
            fontFamily = Montserrat,
            fontWeight = FontWeight.Bold
        )
    }
}