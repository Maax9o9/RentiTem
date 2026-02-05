package com.rentitem.features.itempublications.data.datasources.remote.model

import com.rentitem.features.itempublications.domain.entities.Publication
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublicationDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("price") val price: Double,
    @SerialName("description") val description: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class CreatePublicationRequest(
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("price") val price: Double
)

fun PublicationDto.toDomain() = Publication(
    id = id,
    title = title,
    price = price,
    description = description,
    imageUrl = imageUrl,
    createdAt = createdAt
)
