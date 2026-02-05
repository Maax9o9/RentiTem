package com.rentitem.features.itempublications.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rentitem.features.itempublications.domain.usecases.CreatePublicationUseCase
import com.rentitem.features.itempublications.domain.usecases.GetPublicationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublicationsViewModel(
    private val getPublicationsUseCase: GetPublicationsUseCase,
    private val createPublicationUseCase: CreatePublicationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PublicationsUiState>(PublicationsUiState.Loading)
    val uiState: StateFlow<PublicationsUiState> = _uiState.asStateFlow()

    // Form state para la creación (UI)
    var title by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var price by mutableStateOf("")
        private set

    fun onTitleChange(v: String) { title = v }
    fun onDescriptionChange(v: String) { description = v }
    fun onPriceChange(v: String) { price = v }

    init {
        loadPublications()
    }

    fun loadPublications() {
        viewModelScope.launch {
            _uiState.value = PublicationsUiState.Loading
            try {
                val publications = getPublicationsUseCase()
                _uiState.value = PublicationsUiState.Success(publications)
            } catch (e: Exception) {
                _uiState.value = PublicationsUiState.Error(e.message ?: "Error al cargar publicaciones")
            }
        }
    }

    fun createPublication(onSuccess: () -> Unit) {
        val priceValue = price.toDoubleOrNull() ?: 0.0
        viewModelScope.launch {
            try {
                createPublicationUseCase(title, priceValue, description)
                loadPublications() // Recargar la lista tras crear
                onSuccess() // Cerrar modal o limpiar UI
                clearForm()
            } catch (e: Exception) {
                _uiState.value = PublicationsUiState.Error(e.message ?: "Error al crear publicación")
            }
        }
    }

    private fun clearForm() {
        title = ""
        description = ""
        price = ""
    }
}
