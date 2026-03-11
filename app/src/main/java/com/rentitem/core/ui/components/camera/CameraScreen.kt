package com.rentitem.core.ui.components.camera

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.rentitem.core.hardware.presentation.CameraUiState
import com.rentitem.core.hardware.presentation.CameraViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onPhotoCaptured: (uri: String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    LaunchedEffect(uiState) {
        if (uiState is CameraUiState.Success) {
            val photo = (uiState as CameraUiState.Success).photo
            onPhotoCaptured(photo.uri)
            viewModel.resetState()
            onDismiss()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is CameraUiState.Error) {
            val error = uiState as CameraUiState.Error
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = error.message,
                    actionLabel = "OK"
                )
            }
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            cameraPermission.status.isGranted -> {
                CameraContent(
                    isCapturing = uiState is CameraUiState.Capturing,
                    onCapture = viewModel::capturePhoto,
                    onRegisterImageCapture = viewModel::registerImageCapture,
                    onDismiss = onDismiss
                )
            }

            cameraPermission.status.shouldShowRationale -> {
                CameraPermissionRationale(
                    onRequest = { cameraPermission.launchPermissionRequest() },
                    onDismiss = onDismiss
                )
            }

            else -> {
                LaunchedEffect(Unit) {
                    cameraPermission.launchPermissionRequest()
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun CameraContent(
    isCapturing: Boolean,
    onCapture: () -> Unit,
    onRegisterImageCapture: (ImageCapture) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current


    val surfaceRequests = remember { MutableStateFlow<SurfaceRequest?>(null) }
    val surfaceRequest by surfaceRequests.collectAsStateWithLifecycle()

    var useFrontCamera by remember { mutableStateOf(false) }

    val cameraSelector = remember(useFrontCamera) {
        if (useFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA
        else CameraSelector.DEFAULT_BACK_CAMERA
    }

    LaunchedEffect(cameraSelector) {
        val provider = ProcessCameraProvider.awaitInstance(context)

        val preview = Preview.Builder().build().apply {
            setSurfaceProvider { request ->
                surfaceRequests.value = request
            }
        }

        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        onRegisterImageCapture(imageCapture)

        provider.unbindAll()
        provider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = Modifier.fillMaxSize()
            )
        }

        CameraTopBar(
            onDismiss = onDismiss,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        CameraBottomControls(
            isCapturing = isCapturing,
            onCapture = onCapture,
            onFlipCamera = { useFrontCamera = !useFrontCamera },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
