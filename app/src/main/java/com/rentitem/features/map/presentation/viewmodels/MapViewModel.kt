package com.rentitem.features.map.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rentitem.features.map.domain.usecases.GetPublicationsForMapUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val getPublicationsForMapUseCase: GetPublicationsForMapUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadPublications()
    }

    fun loadPublications() {
        viewModelScope.launch {
            _uiState.value = MapUiState.Loading
            try {
                val publications = getPublicationsForMapUseCase()
                _uiState.value = MapUiState.Success(publications)
            } catch (e: Exception) {
                _uiState.value = MapUiState.Error(e.message ?: "Error al cargar el mapa")
            }
        }
    }

    companion object {
        fun provideFactory(
            getPublicationsForMapUseCase: GetPublicationsForMapUseCase
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MapViewModel(getPublicationsForMapUseCase) as T
                }
            }
    }
}
