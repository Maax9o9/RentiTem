package com.rentitem.features.signup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rentitem.features.signup.domain.usecases.SignUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class SignUpFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val address: String = ""
)

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(SignUpFormState())
    val formState: StateFlow<SignUpFormState> = _formState.asStateFlow()

    fun onNameChange(v: String) { _formState.update { it.copy(name = v) } }
    fun onEmailChange(v: String) { _formState.update { it.copy(email = v) } }
    fun onPasswordChange(v: String) { _formState.update { it.copy(password = v) } }
    fun onPhoneChange(v: String) { _formState.update { it.copy(phone = v) } }
    fun onAddressChange(v: String) { _formState.update { it.copy(address = v) } }

    fun resetState() {
        _uiState.value = SignUpUiState.Idle
    }

    fun signUp() {
        val currentForm = _formState.value
        if (currentForm.password.length < 8) {
            _uiState.value = SignUpUiState.Error("La contraseña debe tener al menos 8 caracteres.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading
            try {
                signUpUseCase(
                    fullName = currentForm.name,
                    email = currentForm.email,
                    pass = currentForm.password,
                    phone = currentForm.phone,
                    address = currentForm.address
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

    fun signUpWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading
            try {
                signUpUseCase.withGoogle(idToken)
                _uiState.value = SignUpUiState.Success("¡Registro con Google exitoso!")
            } catch (e: Exception) {
                _uiState.value = SignUpUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
