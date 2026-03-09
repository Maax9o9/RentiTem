package com.rentitem.features.itempublications.data.repositories

import android.util.Log
import com.rentitem.core.network.RentiTemApi
import com.rentitem.features.itempublications.data.datasources.remote.model.toDomain
import com.rentitem.features.itempublications.domain.entities.Publication
import com.rentitem.features.itempublications.domain.repositories.PublicationRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PublicationRepositoryImpl(
    private val api: RentiTemApi
) : PublicationRepository {

    override suspend fun getPublications(): List<Publication> {
        return api.getPublications().map { it.toDomain() }
    }

    override suspend fun createPublication(
        title: String,
        description: String,
        price: Double,
        priceType: String,
        category: String,
        imageFile: File,
        location: String?
    ): Result<Publication> {
        return try {
            Log.d("API_CREATE_ITEM", "Enviando campos al backend: title=$title, location=$location")

            val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val pricePart = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val priceTypePart = priceType.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryPart = category.toRequestBody("text/plain".toMediaTypeOrNull())
            
            val city: String?
            val state: String?
            if (location != null && location.contains(",")) {
                val parts = location.split(",")
                city = parts[0].trim()
                state = if (parts.size > 1) parts[1].trim() else ""
            } else {
                city = location
                state = ""
            }

            val cityPart = city?.toRequestBody("text/plain".toMediaTypeOrNull())
            val statePart = state?.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            )

            val response = api.createPublication(
                titlePart, descPart, pricePart, priceTypePart, categoryPart, cityPart, statePart, imagePart
            )

            // Manejo flexible de la respuesta por si 'item' viene nulo
            val resultDto = response.item ?: com.rentitem.features.itempublications.data.datasources.remote.model.PublicationDto(
                id = response.id ?: 0,
                title = response.title ?: title,
                price = response.price ?: price,
                description = response.description ?: description,
                imageUrl = response.imageUrl,
                createdAt = response.createdAt,
                city = response.city ?: city,
                state = response.state ?: state
            )

            Result.success(resultDto.toDomain())
        } catch (e: Exception) {
            Log.e("API_CREATE_ITEM", "Error detallado: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deletePublication(id: Int): Result<Unit> {
        return try {
            api.deletePublication(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
