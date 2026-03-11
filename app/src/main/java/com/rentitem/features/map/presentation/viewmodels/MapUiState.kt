package com.rentitem.features.map.presentation.viewmodels

import com.rentitem.features.itempublications.domain.entities.Publication

sealed interface MapUiState {
    object Loading : MapUiState
    data class Success(val publications: List<Publication>) : MapUiState
    data class Error(val message: String) : MapUiState
}
