package com.rentitem.features.itempublications.domain.usecases
import com.rentitem.features.itempublications.domain.repositories.PublicationRepository

class GetPublicationsUseCase(private val repository: PublicationRepository) {
    suspend operator fun invoke() = repository.getPublications()
}