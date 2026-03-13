package com.rentitem.features.itempublications.domain.usecases

import com.rentitem.features.itempublications.domain.entities.Publication
import com.rentitem.features.itempublications.domain.repositories.PublicationRepository
import javax.inject.Inject

class GetPublicationsUseCase @Inject constructor(
    private val repository: PublicationRepository
) {
    suspend operator fun invoke(): List<Publication> {
        return repository.getPublications()
    }
}
