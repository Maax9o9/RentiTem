package com.rentitem.features.signup.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rentitem.features.signup.presentation.viewmodels.SignUpUiState
import com.rentitem.features.signup.presentation.viewmodels.SignUpViewModel

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is SignUpUiState.Success) {
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registro RentiTem", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        OutlinedTextField(
            value = formState.name,
            onValueChange = { viewModel.onNameChange(it) },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = formState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = formState.phone,
            onValueChange = { viewModel.onPhoneChange(it) },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = formState.address,
            onValueChange = { viewModel.onAddressChange(it) },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = formState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(Modifier.height(24.dp))

        when (uiState) {
            is SignUpUiState.Loading -> CircularProgressIndicator()
            else -> {
                Button(
                    onClick = { viewModel.signUp() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse")
                }
            }
        }

        if (uiState is SignUpUiState.Error) {
            Text(
                text = (uiState as SignUpUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        TextButton(onClick = onBack) {
            Text("Volver al Login")
        }
    }
}
