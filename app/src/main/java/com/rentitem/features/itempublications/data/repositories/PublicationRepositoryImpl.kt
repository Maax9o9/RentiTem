package com.rentitem.features.itempublications.data.repositories

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
        imageFile: File
    ): Result<Publication> {
        return try {
            val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val pricePart = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val typePart = priceType.toRequestBody("text/plain".toMediaTypeOrNull())
            val catPart = category.toRequestBody("text/plain".toMediaTypeOrNull())

            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            val response = api.createPublication(
                title = titlePart,
                description = descPart,
                price = pricePart,
                priceType = typePart,
                category = catPart,
                image = imagePart
            )
            val newPublication = Publication(
                id = response.id,
                title = title,
                description = description,
                price = price,
                imageUrl = null,
                createdAt = null
            )
            Result.success(newPublication)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}