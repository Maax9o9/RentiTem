package com.rentitem.core.ui.components.camera

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cameraswitch
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.flow.MutableStateFlow
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
            .setFlashMode(
                if (flashEnabled) ImageCapture.FLASH_MODE_ON
                else ImageCapture.FLASH_MODE_OFF
            )
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

    Box(
        modifier = modifier
            .background(Color.Black)
    ) {
        // ✅ fillMaxSize propio — no hereda el modifier externo
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = Modifier.fillMaxSize()
            )
        }

// Barra superior: cerrar + flash
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .safeDrawingPadding() // Protege contra el notch y recortes de pantalla curvos
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .zIndex(1f), // Fuerza a la barra a dibujarse SOBRE la cámara
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón Cerrar
            IconButton(
                onClick = onDismiss,
                // Agregamos un fondo circular semitransparente para garantizar contraste
                modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Cerrar cámara",
                    tint = Color.White
                )
            }

            // Botón Flash
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

        // Controles inferiores: captura + flip
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Espaciador izquierdo para centrar el botón de captura
            Box(modifier = Modifier.size(48.dp))

            // Botón de captura
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

            // Flip de cámara
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
