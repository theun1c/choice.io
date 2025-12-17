package com.example.choiceiomobile.ui.screens.auth

import android.util.Log
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
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit = {} // Убрали параметр username
) {
    val viewModel: AuthViewModel = viewModel()

    val username by viewModel.loginUsername.collectAsState()
    val password by viewModel.loginPassword.collectAsState()
    val isLoading by viewModel.loginLoading.collectAsState()
    val error by viewModel.loginError.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "log in",
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
                    onValueChange = { viewModel.setLoginUsername(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                BaseTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "password",
                    value = password,
                    isPassword = true,
                    onValueChange = { viewModel.setLoginPassword(it) }
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                BaseButton(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    text = "register",
                    onClick = onRegisterClick,
                    initialIsWhiteTheme = true
                )

                BaseButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = if(isLoading) "Loading..." else "log in",
                    onClick = {
                        viewModel.login(onLoginSuccess)
                    },
                    enabled = !isLoading && username.isNotEmpty() && password.isNotEmpty()
                )
            }
        }
    }
}