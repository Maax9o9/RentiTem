package com.rentitem.core.ui.components.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CameraPermissionRationale(
    onRequest: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            Text(
                text = "Acceso a la cámara",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                text = "Necesitamos acceso a tu cámara para tomar fotos de tus artículos y agregarlas a tu publicación.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Permitir acceso")
            }
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Ahora no",
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}
