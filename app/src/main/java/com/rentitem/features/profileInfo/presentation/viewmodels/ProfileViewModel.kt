package com.rentitem.features.profileInfo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rentitem.features.profileInfo.domain.usecases.GetProfileUseCase
import com.rentitem.features.profileInfo.domain.usecases.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileFormState(
    val fullName: String = "",
    val phone: String = "",
    val address: String = "",
    val profilePicUri: String? = null,
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false,
    val updateError: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(ProfileFormState())
    val formState: StateFlow<ProfileFormState> = _formState.asStateFlow()

    init {
        loadProfile()
    }

    fun onFullNameChange(v: String) { _formState.update { it.copy(fullName = v) } }
    fun onPhoneChange(v: String) { _formState.update { it.copy(phone = v) } }
    fun onAddressChange(v: String) { _formState.update { it.copy(address = v) } }
    fun onProfilePicChange(uri: String?) { _formState.update { it.copy(profilePicUri = uri) } }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            getProfileUseCase()
                .onSuccess { profile ->
                    _uiState.value = ProfileUiState.Success(profile)
                    _formState.update {
                        it.copy(
                            fullName = profile.fullName,
                            phone = profile.phone,
                            address = profile.address,
                            profilePicUri = profile.profilePic
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(error.message ?: "Error al cargar el perfil")
                }
        }
    }

    fun updateProfile() {
        val currentForm = _formState.value
        viewModelScope.launch {
            _formState.update { it.copy(isUpdating = true, updateError = null, updateSuccess = false) }
            updateProfileUseCase(
                fullName = currentForm.fullName,
                phone = currentForm.phone,
                address = currentForm.address,
                profilePicUri = currentForm.profilePicUri
            ).onSuccess { updatedProfile ->
                _uiState.value = ProfileUiState.Success(updatedProfile)
                _formState.update { it.copy(isUpdating = false, updateSuccess = true) }
            }.onFailure { error ->
                _formState.update { it.copy(isUpdating = false, updateError = error.message) }
            }
        }
    }    
    fun resetUpdateState() {
        _formState.update { it.copy(updateSuccess = false, updateError = null) }
    }
}
