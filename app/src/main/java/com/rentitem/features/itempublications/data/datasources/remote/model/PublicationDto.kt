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
    @SerialName("State") val state: String? = null,
    @SerialName("Latitude") val latitude: Double? = null,
    @SerialName("Longitude") val longitude: Double? = null,
    @SerialName("owner_profile_pic") val ownerProfilePic: String? = null
)

@Serializable
data class CreatePublicationRequest(
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("price") val price: Double,
    @SerialName("price_type") val priceType: String,
    @SerialName("category") val category: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("city") val city: String,
    @SerialName("state") val state: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double
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
    @SerialName("State") val state: String? = null,
    @SerialName("Latitude") val latitude: Double? = null,
    @SerialName("Longitude") val longitude: Double? = null
)

fun PublicationDto.toDomain(): Publication {
    val baseUrl = "http://192.168.1.7:8080/"

    return Publication(
        id = id,
        title = title,
        price = price,
        description = description,
        imageUrl = if (!imageUrl.isNullOrBlank()) {
            if (imageUrl.startsWith("http")) imageUrl else {
                val cleanPath = imageUrl.removePrefix("/")
                "$baseUrl$cleanPath"
            }
        } else null,
        createdAt = createdAt,
        city = city,
        state = state,
        latitude = latitude,
        longitude = longitude,
        ownerProfilePic = if (!ownerProfilePic.isNullOrBlank()) {
            if (ownerProfilePic.startsWith("http")) ownerProfilePic else {
                val cleanPath = ownerProfilePic.removePrefix("/")
                "$baseUrl$cleanPath"
            }
        } else null
    )
}
