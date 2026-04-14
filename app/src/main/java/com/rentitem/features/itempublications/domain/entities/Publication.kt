package com.rentitem.features.itempublications.domain.entities

data class Publication(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val imageUrl: String?,
    val createdAt: String?,
    val city: String? = null,
    val state: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val ownerProfilePic: String? = null,
    val userId: String? = null
)
