package com.rentitem.features.itempublications.presentation.viewmodels

import android.net.Uri
import com.rentitem.features.itempublications.domain.entities.Publication

sealed interface PublicationsUiState {
    object Loading : PublicationsUiState
    data class Success(val publications: List<Publication>) : PublicationsUiState
    data class Error(val message: String) : PublicationsUiState
}

data class CreatePublicationFormState(
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val priceType: String = "por_hora",
    val category: String = "Herramientas",
    val selectedImageUri: Uri? = null,
    val isFormLoading: Boolean = false,
    val formError: String? = null
)