package com.rentitem.features.itempublications.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.rentitem.features.chat.presentation.viewmodels.ChatListViewModel
import com.rentitem.core.hardware.domain.CameraManager
import com.rentitem.features.itempublications.presentation.components.CreatePublicationModal
import com.rentitem.features.itempublications.presentation.components.HomeHeader
import com.rentitem.features.itempublications.presentation.components.PublicationCard
import com.rentitem.features.itempublications.presentation.viewmodels.PublicationsUiState
import com.rentitem.features.itempublications.presentation.viewmodels.PublicationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationsScreen(
    viewModel: PublicationsViewModel,
    currentUserId: String,
    onProfileClick: () -> Unit,
    onChatClick: (String, String) -> Unit,
    onChatListClick: () -> Unit,
    cameraManager: CameraManager,
    chatListViewModel: ChatListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val unreadCount by chatListViewModel.totalUnreadCount.collectAsStateWithLifecycle()
    var showModal by remember { mutableStateOf(false) }
    var isCameraOpen by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HomeHeader(
                searchText = formState.searchText,
                profilePic = userProfile?.profilePic,
                unreadCount = unreadCount,
                onSearchChange = { viewModel.onSearchChange(it) },
                onTriggerClick = { showModal = true },
                onProfileClick = onProfileClick,
                onChatListClick = onChatListClick
            )

            // Profile Completion Banner
            if (userProfile != null && !userProfile!!.isComplete) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .clickable { onProfileClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Advertencia",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Completa tu perfil (${userProfile!!.completionPercentage}%)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            
                            val missingText = if (userProfile!!.missingFields.isNotEmpty()) {
                                "Falta: ${userProfile!!.missingFields.joinToString(", ")}."
                            } else {
                                "Agrega información para generar más confianza."
                            }
                            
                            Text(
                                text = missingText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                when (val state = uiState) {
                    is PublicationsUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is PublicationsUiState.Success -> {
                        if (state.publications.isEmpty()) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Sin publicaciones aún",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "¡Sé el primero en anunciar algo!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.publications) { item ->
                                    PublicationCard(
                                        publication = item,
                                        currentUserId = currentUserId,
                                        onDelete = { id -> viewModel.deletePublication(id) },
                                        onChatClick = onChatClick
                                    )
                                }
                            }
                        }
                    }
                    is PublicationsUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadPublications() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }

        if (showModal) {
            CreatePublicationModal(
                viewModel = viewModel,
                onDismiss = { showModal = false },
                cameraManager = cameraManager,
                onCameraStateChange = { isOpen -> isCameraOpen = isOpen }
            )
        }
    }
}
