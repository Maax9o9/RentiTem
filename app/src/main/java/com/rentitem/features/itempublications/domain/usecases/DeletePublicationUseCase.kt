package com.rentitem.features.itempublications.domain.usecases

import com.rentitem.features.itempublications.domain.repositories.PublicationRepository
import javax.inject.Inject

class DeletePublicationUseCase @Inject constructor(
    private val repository: PublicationRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return repository.deletePublication(id)
    }
}
