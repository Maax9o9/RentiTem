package com.rentitem.features.itempublications.presentation.viewmodels

import com.rentitem.features.itempublications.domain.entities.Publication

sealed interface PublicationsUiState {
    object Loading : PublicationsUiState
    data class Success(val publications: List<Publication>) : PublicationsUiState
    data class Error(val message: String) : PublicationsUiState
}
