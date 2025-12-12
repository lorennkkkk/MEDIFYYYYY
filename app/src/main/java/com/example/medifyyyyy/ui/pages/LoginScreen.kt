package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.medifyyyyy.ui.common.UiResult
import com.example.medifyyyyy.ui.viewmodel.AuthViewModel


@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateRegister: () -> Unit,

    ) {
    val state by viewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Spacer(modifier = Modifier.height(24.dp))
        Column(Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(24.dp)
                    .size(120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            ) {
                Text(
                    text = "Medify",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text("Login", style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(password, { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
            Box(
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { viewModel.login(email, password) },
                    enabled = state !is UiResult.Loading
                ) {
                    Text(if (state is UiResult.Loading) "Loading..." else "Login")
                }
            }
            TextButton(onClick = onNavigateRegister) { Text("Belum punya akun? Register", color = MaterialTheme.colorScheme.secondary) }
            if (state is UiResult.Error) Text((state as UiResult.Error).message, color = MaterialTheme.colorScheme.error)

        }
    }
}