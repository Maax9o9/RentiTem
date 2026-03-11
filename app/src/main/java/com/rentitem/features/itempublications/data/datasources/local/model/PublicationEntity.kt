package com.rentitem.features.itempublications.data.datasources.local.model

import com.rentitem.features.itempublications.domain.entities.Publication

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "publications")
data class PublicationEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val imageUrl: String?,
    val createdAt: String?,
    val city: String?,
    val state: String?,
    val latitude: Double?,
    val longitude: Double?,
    val cachedAt: Long = System.currentTimeMillis()
)

fun PublicationEntity.toDomain(): Publication = Publication(
    id = id, title = title, price = price,
    description = description, imageUrl = imageUrl,
    createdAt = createdAt, city = city, state = state,
    latitude = latitude, longitude = longitude
)

fun Publication.toEntity(): PublicationEntity = PublicationEntity(
    id = id, title = title, price = price,
    description = description, imageUrl = imageUrl,
    createdAt = createdAt, city = city, state = state,
    latitude = latitude, longitude = longitude
)
