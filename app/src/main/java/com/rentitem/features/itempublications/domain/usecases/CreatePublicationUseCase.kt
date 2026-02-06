package com.rentitem.features.itempublications.domain.usecases

import com.rentitem.features.itempublications.domain.entities.Publication
import com.rentitem.features.itempublications.domain.repositories.PublicationRepository
import java.io.File

class CreatePublicationUseCase(private val repository: PublicationRepository) {

    suspend operator fun invoke(
        title: String,
        description: String,
        price: Double,
        priceType: String,
        category: String,
        imageFile: File
    ): Result<Publication> {
        return repository.createPublication(
            title, description, price, priceType, category, imageFile
        )
    }
}