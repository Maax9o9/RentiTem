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
    @SerialName("CreatedAt") val createdAt: String? = null,
    @SerialName("City") val city: String? = null,
    @SerialName("State") val state: String? = null
)

@Serializable
data class CreatePublicationRequest(
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("price") val price: Double
)

@Serializable
data class CreateItemResponse(
    @SerialName("item") val item: PublicationDto? = null,
    @SerialName("ID") val id: Int? = null,
    @SerialName("Title") val title: String? = null,
    @SerialName("Price") val price: Double? = null,
    @SerialName("Description") val description: String? = null,
    @SerialName("ImageURL") val imageUrl: String? = null,
    @SerialName("CreatedAt") val createdAt: String? = null,
    @SerialName("City") val city: String? = null,
    @SerialName("State") val state: String? = null
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
        createdAt = createdAt,
        city = city,
        state = state
    )
}
