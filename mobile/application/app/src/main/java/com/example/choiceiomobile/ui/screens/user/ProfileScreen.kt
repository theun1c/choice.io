package com.example.choiceiomobile.ui.screens.user

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.choiceiomobile.ui.components.buttons.BaseButton
import com.example.choiceiomobile.ui.components.inputs.BaseTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile"
                    )
                }
            )
        }
    ) {
            paddingValues ->
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
                BaseTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = "username",
                    value = "texttext",
                    onValueChange = {} // заглушка на время
                )

                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )

                BaseTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = "password",
                    value = "texttext",
                    isPassword = true,
                    onValueChange = { } // заглушка на время
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BaseButton(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    text = "delete",
                    onClick = {},
                    initialIsWhiteTheme = true,
                    isRedButton = true
                )

                BaseButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "leave",
                    onClick = {}
                )
            }
        }
    }
}