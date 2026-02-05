package com.rentitem.features.signup.domain.usecases

import com.rentitem.features.signup.domain.entities.SignUpEntity
import com.rentitem.features.signup.domain.repositories.SignUpRepository

class SignUpUseCase(
    private val repository: SignUpRepository
) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        pass: String,
        phone: String,
        address: String
    ): SignUpEntity {
        if (fullName.isBlank() || email.isBlank() || pass.isBlank()) {
            throw Exception("Por favor, completa los campos obligatorios.")
        }
        return repository.signUp(fullName, email, pass, phone, address)
    }
}
