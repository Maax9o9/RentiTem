package com.rentitem.features.login.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rentitem.features.login.domain.usecases.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginFormState(
    val email: String = "",
    val password: String = ""
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    fun onEmailChange(newValue: String) {
        _formState.update { it.copy(email = newValue) }
    }

    fun onPasswordChange(newValue: String) {
        _formState.update { it.copy(password = newValue) }
    }

    fun login() {
        val currentForm = _formState.value
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val response = loginUseCase(currentForm.email, currentForm.password)
                _uiState.value = LoginUiState.Success(response.token)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
