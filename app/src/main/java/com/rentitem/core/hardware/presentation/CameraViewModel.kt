package com.rentitem.core.hardware.presentation

import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rentitem.core.hardware.data.AndroidCameraManager
import com.rentitem.core.hardware.domain.usecases.CapturePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val capturePhotoUseCase: CapturePhotoUseCase,

    private val androidCameraManager: AndroidCameraManager
) : ViewModel() {


    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()


    fun registerImageCapture(imageCapture: ImageCapture) {
        androidCameraManager.imageCapture = imageCapture
    }

    fun capturePhoto() {
        if (_uiState.value is CameraUiState.Capturing) return

        viewModelScope.launch {

            _uiState.value = CameraUiState.Capturing

            capturePhotoUseCase().fold(
                onSuccess = { photo ->
                    _uiState.value = CameraUiState.Success(photo)
                },
                onFailure = { error ->
                    val previousPhoto = (_uiState.value as? CameraUiState.Success)?.photo
                    _uiState.value = CameraUiState.Error(
                        message = error.message ?: "Error desconocido al capturar",
                        previousPhoto = previousPhoto
                    )
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = CameraUiState.Idle
    }

    fun clearError() {
        val previousPhoto = (_uiState.value as? CameraUiState.Error)?.previousPhoto
        _uiState.value = if (previousPhoto != null) {
            CameraUiState.Success(previousPhoto)
        } else {
            CameraUiState.Idle
        }
    }
}
