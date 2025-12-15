package com.example.choiceiomobile.ui.screens.auth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.compose.rememberNavController
import com.example.choiceiomobile.ui.auth.AuthViewModel
import com.example.choiceiomobile.ui.components.buttons.BaseButton
import com.example.choiceiomobile.ui.components.inputs.BaseTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit = {}
) {

    val viewModel: AuthViewModel = viewModel()

    val username by viewModel.loginUsername.collectAsState()
    val password by viewModel.loginPassword.collectAsState()
    val isLoading by viewModel.loginLoading.collectAsState()
    val error by viewModel.loginError.collectAsState()


    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Login"
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
                if(error.isNotEmpty()){
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                BaseTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = "username",
                    value = username,
                    onValueChange = { viewModel.setLoginUsername(it) }
                )

                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )

                BaseTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = "password",
                    value = password,
                    isPassword = true,
                    onValueChange = { viewModel.setLoginPassword(it) }
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
                    text = "register",
                    onClick = onRegisterClick,
                    initialIsWhiteTheme = true
                )

                BaseButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = if(isLoading) "Loading..." else "log in",
                    onClick = { viewModel.login(onLoginSuccess) },
                    enabled = !isLoading && username.isNotEmpty() && password.isNotEmpty()
                )
            }
        }
    }
}