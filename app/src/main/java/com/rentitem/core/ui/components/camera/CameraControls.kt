package com.rentitem.core.ui.components.camera

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cameraswitch
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CameraTopBar(
    onDismiss: () -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(8.dp)
    ) {
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Cerrar cámara",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CameraBottomControls(
    isCapturing: Boolean,
    onCapture: () -> Unit,
    onFlipCamera: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Espaciador izquierdo — mantiene el botón de captura centrado
        Box(modifier = Modifier.size(48.dp))

        CaptureButton(
            isCapturing = isCapturing,
            onClick = onCapture
        )

        // Flip de cámara
        IconButton(
            onClick = onFlipCamera,
            enabled = !isCapturing
        ) {
            Icon(
                imageVector = Icons.Rounded.Cameraswitch,
                contentDescription = "Cambiar cámara",
                tint = if (isCapturing)
                    Color.White.copy(alpha = 0.3f)
                else
                    Color.White
            )
        }
    }
}

@Composable
private fun CaptureButton(
    isCapturing: Boolean,
    onClick: () -> Unit
) {
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
            // Círculo blanco interior — el botón real
            Surface(
                onClick = onClick,
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(68.dp)
            ) {}

            // Anillo exterior — solo visual, estilo Google/Apple camera
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
}
