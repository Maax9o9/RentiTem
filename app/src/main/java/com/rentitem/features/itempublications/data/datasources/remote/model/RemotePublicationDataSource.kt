package com.rentitem.features.itempublications.data.datasources.remote.model

import com.rentitem.core.network.RentiTemApi
import com.rentitem.features.itempublications.domain.entities.Publication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemotePublicationDataSource @Inject constructor(private val api: RentiTemApi) {

    suspend fun getPublications(): List<Publication> =
        api.getPublications().map { it.toDomain() }

    suspend fun createPublication(
        title: String,
        description: String,
        price: Double,
        priceType: String,
        category: String,
        imageUrl: String,
        location: String?,
        latitude: Double?,
        longitude: Double?
    ): Publication {
        val city: String
        val state: String
        if (location != null && location.contains(",")) {
            val parts = location.split(",")
            city = parts[0].trim()
            state = if (parts.size > 1) parts[1].trim() else ""
        } else {
            city = location ?: "Desconocida"
            state = ""
        }

        val request = CreatePublicationRequest(
            title = title,
            description = description,
            price = price,
            priceType = priceType,
            category = category,
            imageUrl = imageUrl,
            city = city,
            state = state,
            latitude = latitude ?: 0.0,
            longitude = longitude ?: 0.0
        )

        val response = api.createPublication(request)

        val dto = response.item ?: PublicationDto(
            id = response.id ?: 0,
            title = title,
            price = price,
            description = description,
            imageUrl = imageUrl,
            createdAt = null,
            city = city,
            state = state,
            latitude = latitude,
            longitude = longitude
        )

        return dto.toDomain()
    }

    suspend fun deletePublication(id: Int) = api.deletePublication(id)
}
