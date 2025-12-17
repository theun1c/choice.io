package com.example.choiceiomobile.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.choiceiomobile.ui.auth.AuthViewModel
import com.example.choiceiomobile.ui.components.buttons.BaseButton
import com.example.choiceiomobile.ui.components.inputs.BaseTextField
import com.example.choiceiomobile.ui.theme.Montserrat

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
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "register",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
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
                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = Color(0xFFFF383C),
                        fontFamily = Montserrat,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                BaseTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "username",
                    value = username,
                    onValueChange = { viewModel.setRegisterUsername(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                BaseTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "password",
                    value = password,
                    isPassword = true,
                    onValueChange = { viewModel.setRegisterPassword(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                BaseTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "confirm password",
                    value = confirmPassword,
                    isPassword = true,
                    onValueChange = { viewModel.setRegisterConfirmPassword(it) }
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                BaseButton(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    text = "log in",
                    onClick = onLoginClick,
                    initialIsWhiteTheme = true
                )

                BaseButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = if(isLoading) "Registering ..." else "register",
                    onClick = {
                        viewModel.register(onRegisterSuccess)
                    },
                    enabled = !isLoading &&
                            username.isNotEmpty() &&
                            password.isNotEmpty() &&
                            confirmPassword.isNotEmpty() &&
                            password == confirmPassword
                )
            }
        }
    }
}

