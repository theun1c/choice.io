package com.example.choiceiomobile.ui.screens.auth

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.choiceiomobile.ui.auth.AuthViewModel
import com.example.choiceiomobile.ui.components.buttons.BaseButton
import com.example.choiceiomobile.ui.components.inputs.BaseTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit = {}
) {

    val viewModel: AuthViewModel = viewModel()

    val username by viewModel.registerUsername.collectAsState()
    val password by viewModel.registerPassword.collectAsState()
    val confirmPassword by viewModel.registerConfirmPassword.collectAsState()
    val isLoading by viewModel.registerLoading.collectAsState()
    val error by viewModel.registerError.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Register"
                    )
                }
            )
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
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
                    isPassword = false,
                    value = username,
                    onValueChange = { viewModel.setRegisterUsername(it)},
                    label = "username"
                )

                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )

                BaseTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    isPassword = true,
                    value = password,
                    onValueChange = { viewModel.setRegisterPassword(it)},
                    label = "password"
                )

                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )

                BaseTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    isPassword = true,
                    value = confirmPassword,
                    onValueChange = { viewModel.setRegisterConfirmPassword(it)},
                    label = "confirm password"
                )

            }

            Column (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BaseButton(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    text = "log in",
                    initialIsWhiteTheme = true,
                    onClick = onLoginClick
                )

                BaseButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = if(isLoading) "Registering ..." else "register",
                    onClick = { viewModel.register(onRegisterSuccess)},
                    enabled = !isLoading
                )
            }
        }
    }
}