package com.example.choiceiomobile.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.choiceiomobile.ui.theme.Montserrat

@Composable
fun BaseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        contentColor = Color.Black,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.CenterVertically)
                .padding(horizontal = 24.dp),
            textStyle = TextStyle(
                fontFamily = Montserrat,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
            singleLine = true,

            visualTransformation = if(isPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },

            decorationBox = {
                innerTextField ->
                if (value.isEmpty()){
                    Text(
                        text = label,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
                innerTextField()
            }
        )
    }
}