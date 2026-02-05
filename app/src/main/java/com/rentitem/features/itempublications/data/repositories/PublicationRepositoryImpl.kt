package com.rentitem.features.itempublications.data.repositories

import com.rentitem.core.network.RentiTemApi
import com.rentitem.features.itempublications.data.datasources.remote.model.CreatePublicationRequest
import com.rentitem.features.itempublications.data.datasources.remote.model.toDomain
import com.rentitem.features.itempublications.domain.entities.Publication
import com.rentitem.features.itempublications.domain.repositories.PublicationRepository

class PublicationRepositoryImpl(
    private val api: RentiTemApi
) : PublicationRepository {

    override suspend fun getPublications(): List<Publication> {
        return api.getPublications().map { it.toDomain() }
    }

    override suspend fun createPublication(title: String, price: Double, description: String): Publication {
        val request = CreatePublicationRequest(
            title = title,
            description = description,
            price = price
        )
        return api.createPublication(request).toDomain()
    }
}
