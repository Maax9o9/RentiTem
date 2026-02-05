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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rentitem.features.itempublications.presentation.viewmodels.PublicationsUiState
import com.rentitem.features.itempublications.presentation.viewmodels.PublicationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePublicationModal(
    viewModel: PublicationsViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState is PublicationsUiState.Loading

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPriceType by remember { mutableStateOf("día") }
    var expanded by remember { mutableStateOf(false) }

    val priceTypes = listOf("hora", "día", "semana", "mes", "uso")

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    val isFormComplete = viewModel.title.isNotBlank() && 
                         viewModel.description.isNotBlank() && 
                         viewModel.price.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp)
        ) {
            // --- HEADER ---
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

            // --- USER INFO ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(46.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Usuario RentiTem",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
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

            // --- INPUT AREA ---
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.onTitleChange(it) },
                placeholder = { Text("Título de tu anuncio", fontSize = 18.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            TextField(
                value = viewModel.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                placeholder = { Text("¿Qué estás pensando en anunciar?", fontSize = 16.sp, color = Color.Gray) },
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

            // --- PRECIO INPUT Y SELECTOR ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.price,
                    onValueChange = { viewModel.onPriceChange(it) },
                    label = { Text("Precio ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = "por $selectedPriceType",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Frecuencia") },
                        modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        priceTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text("por $type") },
                                onClick = {
                                    selectedPriceType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // --- IMAGEN PREVIEW ---
            if (selectedImageUri != null) {
                Box(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth().height(150.dp)) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                    )
                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Borrar foto", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ADD TO POST BAR ---
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
                        IconButton(onClick = { /* TODO: Feature Ubicación */ }) {
                            Icon(Icons.Default.Place, contentDescription = "Ubicación", tint = Color(0xFFF44336))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SUBMIT BUTTON ---
            Button(
                onClick = { viewModel.createPublication { onDismiss() } },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = isFormComplete && !isLoading,
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Publicar")
                }
            }
        }
    }
}
