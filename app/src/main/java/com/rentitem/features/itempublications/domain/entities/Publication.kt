package com.rentitem.features.itempublications.domain.entities

data class Publication(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val imageUrl: String?,
    val createdAt: String?
)
