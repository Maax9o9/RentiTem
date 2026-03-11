package com.rentitem.features.map.domain.usecases

import com.rentitem.features.itempublications.domain.entities.Publication
import com.rentitem.features.itempublications.domain.repositories.PublicationRepository

class GetPublicationsForMapUseCase(
    private val repository: PublicationRepository
) {
    suspend operator fun invoke(): List<Publication> {
        return repository.getPublications()
            .filter { it.latitude != null && it.longitude != null }
    }
}
