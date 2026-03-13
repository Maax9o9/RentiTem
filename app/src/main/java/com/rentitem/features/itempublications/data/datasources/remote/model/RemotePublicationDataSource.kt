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
        imageFile: File,
        location: String?,
        latitude: Double?,
        longitude: Double?
    ): Publication {
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

        val response = api.createPublication(
            title.toRequestBody("text/plain".toMediaTypeOrNull()),
            description.toRequestBody("text/plain".toMediaTypeOrNull()),
            price.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            priceType.toRequestBody("text/plain".toMediaTypeOrNull()),
            category.toRequestBody("text/plain".toMediaTypeOrNull()),
            city?.toRequestBody("text/plain".toMediaTypeOrNull()),
            state?.toRequestBody("text/plain".toMediaTypeOrNull()),
            latitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
            longitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
            MultipartBody.Part.createFormData(
                "image", imageFile.name,
                imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
        )

        val dto = response.item ?: PublicationDto(
            id = response.id ?: 0,
            title = response.title ?: title,
            price = response.price ?: price,
            description = response.description ?: description,
            imageUrl = response.imageUrl,
            createdAt = response.createdAt,
            city = response.city ?: city,
            state = response.state ?: state,
            latitude = response.latitude ?: latitude,
            longitude = response.longitude ?: longitude
        )

        return dto.toDomain()
    }

    suspend fun deletePublication(id: Int) = api.deletePublication(id)
}
