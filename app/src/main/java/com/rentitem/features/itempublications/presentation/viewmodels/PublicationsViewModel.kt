package com.rentitem.features.itempublications.presentation.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rentitem.core.utils.uriToFile
import com.rentitem.features.itempublications.domain.usecases.CreatePublicationUseCase
import com.rentitem.features.itempublications.domain.usecases.GetPublicationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PublicationsViewModel(
    private val getPublicationsUseCase: GetPublicationsUseCase,
    private val createPublicationUseCase: CreatePublicationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PublicationsUiState>(PublicationsUiState.Loading)
    val uiState: StateFlow<PublicationsUiState> = _uiState.asStateFlow()


    private val _formState = MutableStateFlow(CreatePublicationFormState())
    val formState = _formState.asStateFlow()


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


    fun onTitleChange(text: String) {
        _formState.update { it.copy(title = text) }
    }

    fun onDescriptionChange(text: String) {
        _formState.update { it.copy(description = text) }
    }

    fun onPriceChange(text: String) {
        _formState.update { it.copy(price = text) }
    }

    fun onPriceTypeChange(type: String) {
        _formState.update { it.copy(priceType = type) }
    }

    fun onCategoryChange(category: String) {
        _formState.update { it.copy(category = category) }
    }

    fun onImageSelected(uri: Uri?) {
        _formState.update { it.copy(selectedImageUri = uri) }
    }
    fun createPublication(context: Context, onSuccess: () -> Unit) {
        val currentForm = _formState.value

        if (currentForm.title.isBlank() || currentForm.description.isBlank()) {
            _formState.update { it.copy(formError = "El título y la descripción son obligatorios") }
            return
        }

        if (currentForm.selectedImageUri == null) {
            _formState.update { it.copy(formError = "Debes seleccionar una imagen") }
            return
        }

        val priceValue = currentForm.price.toDoubleOrNull()
        if (priceValue == null || priceValue <= 0.0) {
            _formState.update { it.copy(formError = "Ingresa un precio válido") }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isFormLoading = true, formError = null) }

            try {
                val imageFile = uriToFile(context, currentForm.selectedImageUri)

                if (imageFile == null) {
                    _formState.update {
                        it.copy(isFormLoading = false, formError = "No se pudo procesar la imagen")
                    }
                    return@launch
                }

                val result = createPublicationUseCase(
                    title = currentForm.title,
                    description = currentForm.description,
                    price = priceValue,
                    priceType = currentForm.priceType,
                    category = currentForm.category,
                    imageFile = imageFile
                )

                result.onSuccess {
                    _formState.update { it.copy(isFormLoading = false) }
                    resetForm()
                    loadPublications()
                    onSuccess()
                }.onFailure { error ->
                    _formState.update {
                        it.copy(isFormLoading = false, formError = error.message ?: "Error al publicar")
                    }
                }

            } catch (e: Exception) {
                _formState.update {
                    it.copy(isFormLoading = false, formError = e.message ?: "Error desconocido")
                }
            }
        }
    }
    private fun resetForm() {
        _formState.value = CreatePublicationFormState()
    }

}
