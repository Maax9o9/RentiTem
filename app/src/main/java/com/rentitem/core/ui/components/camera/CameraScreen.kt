package com.rentitem.core.ui.components.camera

import android.Manifest
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cameraswitch
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.rentitem.core.hardware.domain.CameraManager
import com.rentitem.core.hardware.presentation.CameraUiState
import com.rentitem.core.hardware.presentation.CameraViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onPhotoCaptured: (uri: String) -> Unit,
    onDismiss: () -> Unit,
    cameraManager: CameraManager
) {
    val viewModel: CameraViewModel = viewModel(
        factory = CameraViewModel.provideFactory(cameraManager)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val flashEnabled by viewModel.flashEnabled.collectAsStateWithLifecycle()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    BackHandler { onDismiss() }

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
                    flashEnabled = flashEnabled,
                    onCapture = viewModel::capturePhoto,
                    onToggleFlash = viewModel::toggleFlash,
                    onRegisterImageCapture = viewModel::registerImageCapture,
                    onDismiss = onDismiss,
                    modifier = Modifier.fillMaxSize()
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
    flashEnabled: Boolean,
    onCapture: () -> Unit,
    onToggleFlash: () -> Unit,
    onRegisterImageCapture: (ImageCapture) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var useFrontCamera by remember { mutableStateOf(false) }

    val cameraSelector = remember(useFrontCamera) {
        if (useFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA
        else CameraSelector.DEFAULT_BACK_CAMERA
    }

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    LaunchedEffect(cameraSelector, flashEnabled) {
        val provider = ProcessCameraProvider.awaitInstance(context)

        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(
                if (flashEnabled) ImageCapture.FLASH_MODE_ON
                else ImageCapture.FLASH_MODE_OFF
            )
            .build()

        onRegisterImageCapture(imageCapture)

        try {
            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .zIndex(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Cerrar cámara",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = onToggleFlash,
                enabled = !useFrontCamera,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = if (flashEnabled) Icons.Rounded.FlashOn
                    else Icons.Rounded.FlashOff,
                    contentDescription = if (flashEnabled) "Flash encendido"
                    else "Flash apagado",
                    tint = when {
                        flashEnabled    -> Color.Yellow
                        useFrontCamera  -> Color.White.copy(alpha = 0.3f)
                        else            -> Color.White
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(48.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                if (isCapturing) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(72.dp)
                    )
                } else {
                    Surface(
                        onClick = onCapture,
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(68.dp)
                    ) {}
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .border(
                                width = 3.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            IconButton(
                onClick = { useFrontCamera = !useFrontCamera },
                enabled = !isCapturing
            ) {
                Icon(
                    imageVector = Icons.Rounded.Cameraswitch,
                    contentDescription = "Cambiar cámara",
                    tint = if (isCapturing) Color.White.copy(alpha = 0.3f)
                    else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

