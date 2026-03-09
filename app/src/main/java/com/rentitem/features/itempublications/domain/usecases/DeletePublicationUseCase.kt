package com.rentitem.features.itempublications.domain.usecases

import com.rentitem.features.itempublications.domain.repositories.PublicationRepository

class DeletePublicationUseCase(
    private val repository: PublicationRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return repository.deletePublication(id)
    }
}
