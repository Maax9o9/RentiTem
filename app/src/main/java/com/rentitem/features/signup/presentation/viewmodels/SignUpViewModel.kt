package com.rentitem.features.signup.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rentitem.features.signup.domain.usecases.SignUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    var name by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var phone by mutableStateOf("")
        private set
    var address by mutableStateOf("")
        private set

    fun onNameChange(v: String) { name = v }
    fun onEmailChange(v: String) { email = v }
    fun onPasswordChange(v: String) { password = v }
    fun onPhoneChange(v: String) { phone = v }
    fun onAddressChange(v: String) { address = v }

    fun resetState() {
        _uiState.value = SignUpUiState.Idle
    }

    fun signUp() {
        if (password.length < 8) {
            _uiState.value = SignUpUiState.Error("La contraseña debe tener al menos 8 caracteres.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading
            try {
                val response = signUpUseCase(
                    fullName = name,
                    email = email,
                    pass = password,
                    phone = phone,
                    address = address
                )
                _uiState.value = SignUpUiState.Success("¡Cuenta creada exitosamente!")
            } catch (e: HttpException) {
                val errorMessage = if (e.code() == 400 || e.code() == 409) {
                    "Perfil con esa información ya registrado."
                } else {
                    "Error en el registro: ${e.message()}"
                }
                _uiState.value = SignUpUiState.Error(errorMessage)
            } catch (e: Exception) {
                _uiState.value = SignUpUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
