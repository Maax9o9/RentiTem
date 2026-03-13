package com.rentitem.features.itempublications.domain.usecases

import com.rentitem.features.itempublications.domain.entities.Publication
import com.rentitem.features.itempublications.domain.repositories.PublicationRepository
import java.io.File
import javax.inject.Inject

class CreatePublicationUseCase @Inject constructor(
    private val repository: PublicationRepository
) {

    suspend operator fun invoke(
        title: String,
        description: String,
        price: Double,
        priceType: String,
        category: String,
        imageFile: File,
        location: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ): Result<Publication> {
        return repository.createPublication(
            title, description, price, priceType, category, imageFile, location,
            latitude, longitude
        )
    }
}
