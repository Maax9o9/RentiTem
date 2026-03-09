package com.rentitem.features.profileInfo.presentation.viewmodels

import com.rentitem.features.profileInfo.domain.entities.UserProfile

sealed interface ProfileUiState {
    object Idle : ProfileUiState
    object Loading : ProfileUiState
    data class Success(val profile: UserProfile) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
