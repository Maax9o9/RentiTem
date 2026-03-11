package com.rentitem.core.hardware.presentation

import com.rentitem.core.hardware.domain.model.CapturedPhoto

sealed interface CameraUiState {

    data object Idle : CameraUiState

    data object Capturing : CameraUiState

    data class Success(
        val photo: CapturedPhoto
    ) : CameraUiState

    data class Error(
        val message: String,
        val previousPhoto: CapturedPhoto? = null
    ) : CameraUiState
}
