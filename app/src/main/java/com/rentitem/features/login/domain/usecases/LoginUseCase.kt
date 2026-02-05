package com.rentitem.features.login.domain.usecases

import com.rentitem.features.login.domain.entities.AuthEntity
import com.rentitem.features.login.domain.repositories.LoginRepository

class LoginUseCase(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(email: String, pass: String): AuthEntity {
        return repository.login(email, pass)
    }
}
