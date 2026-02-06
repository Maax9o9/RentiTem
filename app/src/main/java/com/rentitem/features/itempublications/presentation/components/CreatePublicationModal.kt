package com.rentitem.features.itempublications.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // ⚠️ IMPORTANTE
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rentitem.features.itempublications.presentation.viewmodels.PublicationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePublicationModal(
    viewModel: PublicationsViewModel,
    onDismiss: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()

    val context = LocalContext.current

    var expandedPriceType by remember { mutableStateOf(false) }
    val priceTypes = listOf("hora", "día", "semana", "mes", "uso")

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            viewModel.onImageSelected(uri)
        }
    )

    val isFormComplete = formState.title.isNotBlank() &&
            formState.description.isNotBlank() &&
            formState.price.isNotBlank() &&
            formState.selectedImageUri != null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Crear publicación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(Color.LightGray.copy(alpha = 0.2f), CircleShape)
                        .size(30.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", modifier = Modifier.size(18.dp))
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(46.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Usuario RentiTem", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Surface(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = " 🌎 Público ",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = formState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text("Título de tu anuncio") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            TextField(
                value = formState.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                placeholder = { Text("¿Qué estás pensando en anunciar?", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = formState.price,
                    onValueChange = { viewModel.onPriceChange(it) },
                    label = { Text("Precio ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = "por ${formState.priceType}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Frecuencia") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedPriceType = true },
                        trailingIcon = {
                            IconButton(onClick = { expandedPriceType = true }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        },
                        enabled = false
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { expandedPriceType = true }
                    )

                    DropdownMenu(
                        expanded = expandedPriceType,
                        onDismissRequest = { expandedPriceType = false }
                    ) {
                        priceTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text("por $type") },
                                onClick = {
                                    expandedPriceType = false
                                }
                            )
                        }
                    }
                }
            }

            if (formState.selectedImageUri != null) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .height(200.dp) // Un poco más grande
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = formState.selectedImageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = { viewModel.onImageSelected(null) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .background(Color.Black.copy(0.6f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Borrar", tint = Color.White)
                    }
                }
            }

            if (formState.formError != null) {
                Text(
                    text = formState.formError!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Agregar a tu publicación", fontWeight = FontWeight.SemiBold)
                    Row {
                        IconButton(onClick = {
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Foto", tint = Color(0xFF4CAF50))
                        }
                        IconButton(onClick = { /* Feature futura */ }) {
                            Icon(Icons.Default.Place, contentDescription = "Ubicación", tint = Color(0xFFF44336))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.createPublication(context) { onDismiss() } },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = isFormComplete && !formState.isFormLoading,
                shape = RoundedCornerShape(8.dp)
            ) {
                if (formState.isFormLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Subiendo...")
                } else {
                    Text("Publicar")
                }
            }
        }
    }
}