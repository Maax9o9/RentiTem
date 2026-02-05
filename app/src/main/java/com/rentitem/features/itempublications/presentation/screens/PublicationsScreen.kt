package com.rentitem.features.itempublications.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rentitem.features.itempublications.presentation.components.CreatePublicationModal
import com.rentitem.features.itempublications.presentation.components.HomeHeader
import com.rentitem.features.itempublications.presentation.components.PublicationCard
import com.rentitem.features.itempublications.presentation.viewmodels.PublicationsUiState
import com.rentitem.features.itempublications.presentation.viewmodels.PublicationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationsScreen(
    viewModel: PublicationsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RentiTem", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.LightGray.copy(alpha = 0.2f))
        ) {
            // --- HEADER COMPONENT (Barra de búsqueda + Trigger de publicación) ---
            HomeHeader(
                onSearch = { /* TODO: Implementar búsqueda */ },
                onTriggerClick = { showModal = true }
            )

            // --- CONTENIDO (Lista de Publicaciones) ---
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is PublicationsUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is PublicationsUiState.Success -> {
                        if (state.publications.isEmpty()) {
                            Text(
                                text = "No hay publicaciones disponibles",
                                modifier = Modifier.align(Alignment.Center),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.publications) { item ->
                                    PublicationCard(publication = item)
                                }
                            }
                        }
                    }
                    is PublicationsUiState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
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
                onDismiss = { showModal = false }
            )
        }
    }
}
