package com.rentitem.features.itempublications.domain.usecases

import com.rentitem.features.itempublications.domain.entities.Publication
import com.rentitem.features.itempublications.domain.repositories.PublicationRepository

class CreatePublicationUseCase(private val repository: PublicationRepository) {
    suspend operator fun invoke(title: String, price: Double, description: String): Publication {
        if (title.isBlank()) throw Exception("El título no puede estar vacío")
        if (price <= 0) throw Exception("El precio debe ser mayor a 0")
        
        return repository.createPublication(title, price, description)
    }
}
