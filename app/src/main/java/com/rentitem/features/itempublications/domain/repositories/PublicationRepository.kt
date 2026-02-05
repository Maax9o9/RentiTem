package com.rentitem.features.itempublications.domain.repositories

import com.rentitem.features.itempublications.domain.entities.Publication

interface PublicationRepository {
    suspend fun getPublications(): List<Publication>
    suspend fun createPublication(title: String, price: Double, description: String): Publication
}
