package com.rentitem.features.itempublications.presentation.viewmodels

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rentitem.core.hardware.domain.GpsManager
import com.rentitem.features.itempublications.domain.usecases.CreatePublicationUseCase
import com.rentitem.features.itempublications.domain.usecases.DeletePublicationUseCase
import com.rentitem.features.itempublications.domain.usecases.GetPublicationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import javax.inject.Inject

data class PublicationFormState(
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val priceType: String = "día",
    val category: String = "Otros",
    val selectedImageUri: Uri? = null,
    val isFormLoading: Boolean = false,
    val searchText: String = "",
    val isPriceTypeMenuExpanded: Boolean = false,
    val locationName: String = "",
    val isLocationLoading: Boolean = false,
    val showCamera: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null
)

@HiltViewModel
class PublicationsViewModel @Inject constructor(
    private val getPublicationsUseCase: GetPublicationsUseCase,
    private val createPublicationUseCase: CreatePublicationUseCase,
    private val deletePublicationUseCase: DeletePublicationUseCase,
    private val gpsManager: GpsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<PublicationsUiState>(PublicationsUiState.Loading)
    val uiState: StateFlow<PublicationsUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(PublicationFormState())
    val formState: StateFlow<PublicationFormState> = _formState.asStateFlow()

    fun onTitleChange(v: String) { _formState.update { it.copy(title = v) } }
    fun onDescriptionChange(v: String) { _formState.update { it.copy(description = v) } }

    fun onPriceChange(v: String) {
        val filtered = v.filter { it.isDigit() || it == '.' }
        if (filtered.count { it == '.' } <= 1) {
            _formState.update { it.copy(price = filtered) }
        }
    }

    fun onPriceTypeChange(v: String) { _formState.update { it.copy(priceType = v, isPriceTypeMenuExpanded = false) } }
    fun onCategoryChange(v: String) { _formState.update { it.copy(category = v) } }
    fun onImageSelected(uri: Uri?) { _formState.update { it.copy(selectedImageUri = uri) } }
    fun onSearchChange(v: String) { _formState.update { it.copy(searchText = v) } }
    fun onPriceTypeMenuToggle(expanded: Boolean) { _formState.update { it.copy(isPriceTypeMenuExpanded = expanded) } }

    fun onOpenCamera() {
        _formState.update { it.copy(showCamera = true) }
    }

    fun onPhotoCaptured(uri: String) {
        _formState.update {
            it.copy(
                selectedImageUri = Uri.parse(uri),
                showCamera = false
            )
        }
    }

    fun onCameraDismissed() {
        _formState.update { it.copy(showCamera = false) }
    }

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

    fun fetchCurrentLocation(context: Context) {
        viewModelScope.launch {
            _formState.update { it.copy(isLocationLoading = true) }
            val location = gpsManager.getCurrentLocation()
            if (location != null) {
                val address = getAddressFromLocation(context, location.latitude, location.longitude)
                _formState.update {
                    it.copy(
                        locationName = address,
                        isLocationLoading = false,
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                }
            } else {
                _formState.update {
                    it.copy(
                        isLocationLoading = false,
                        locationName = "Ubicación no disponible",
                        latitude = null,
                        longitude = null
                    )
                }
            }
        }
    }


    private suspend fun getAddressFromLocation(context: Context, lat: Double, lng: Double): String = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val city = address.locality ?: address.subAdminArea ?: ""
                val state = address.adminArea ?: ""
                if (city.isNotEmpty() && state.isNotEmpty()) "$city, $state" else state.ifEmpty { city }.ifEmpty { "Ubicación desconocida" }
            } else {
                "Ciudad desconocida"
            }
        } catch (e: Exception) {
            "Error al obtener ciudad"
        }
    }

    fun createPublication(context: Context, onSuccess: () -> Unit) {
        val currentForm = _formState.value
        val uri = currentForm.selectedImageUri ?: return
        val priceValue = currentForm.price.toDoubleOrNull() ?: 0.0
        val imageFile = uriToFile(context, uri) ?: return

        _formState.update { it.copy(isFormLoading = true) }
        viewModelScope.launch {
            createPublicationUseCase(
                title = currentForm.title,
                description = currentForm.description,
                price = priceValue,
                priceType = currentForm.priceType,
                category = currentForm.category,
                imageFile = imageFile,
                location = currentForm.locationName,
                latitude = currentForm.latitude,
                longitude = currentForm.longitude
            )
                .onSuccess {
                    loadPublications()
                    onSuccess()
                    clearForm()
                }
                .onFailure {
                    // Manejar error
                }
            _formState.update { it.copy(isFormLoading = false) }
        }
    }


    fun deletePublication(id: Int) {
        viewModelScope.launch {
            deletePublicationUseCase(id)
                .onSuccess { loadPublications() }
        }
    }

    private fun clearForm() {
        val currentSearch = _formState.value.searchText
        _formState.value = PublicationFormState(searchText = currentSearch)
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            null
        }
    }
}
