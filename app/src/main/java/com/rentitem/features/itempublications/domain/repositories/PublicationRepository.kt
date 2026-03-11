package com.rentitem.features.itempublications.domain.repositories

import com.rentitem.features.itempublications.domain.entities.Publication
import java.io.File

interface PublicationRepository {
    suspend fun getPublications(): List<Publication>
    suspend fun createPublication(
        title: String,
        description: String,
        price: Double,
        priceType: String,
        category: String,
        imageFile: File,
        location: String? = null,
        latitude: Double?,
        longitude: Double?
    ): Result<Publication>

    suspend fun deletePublication(id: Int): Result<Unit>
}
