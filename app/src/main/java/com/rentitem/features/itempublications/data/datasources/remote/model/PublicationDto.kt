package com.rentitem.features.itempublications.data.datasources.remote.model

import com.rentitem.features.itempublications.domain.entities.Publication
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublicationDto(
    @SerialName("ID") val id: Int,
    @SerialName("Title") val title: String,
    @SerialName("Price") val price: Double,
    @SerialName("Description") val description: String,
    @SerialName("ImageURL") val imageUrl: String? = null,
    @SerialName("CreatedAt") val createdAt: String? = null
)

@Serializable
data class CreatePublicationRequest(
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("price") val price: Double
)

@Serializable
data class CreateItemResponse(
    @SerialName("id") val id: Int
)
fun PublicationDto.toDomain(): Publication {
    val baseUrl = "https://codigoverse.space/"

    return Publication(
        id = id,
        title = title,
        price = price,
        description = description,

        imageUrl = if (!imageUrl.isNullOrBlank()) {
            val cleanPath = imageUrl.removePrefix("/")
            "$baseUrl$cleanPath"
        } else {
            null
        },

        createdAt = createdAt
    )
}
