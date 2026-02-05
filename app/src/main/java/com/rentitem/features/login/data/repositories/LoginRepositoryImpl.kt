package com.rentitem.features.login.data.repositories

import com.rentitem.core.network.RentiTemApi
import com.rentitem.features.login.data.datasources.remote.model.LoginRequest
import com.rentitem.features.login.domain.entities.AuthEntity
import com.rentitem.features.login.domain.repositories.LoginRepository

class LoginRepositoryImpl(
    private val api: RentiTemApi
) : LoginRepository {
    override suspend fun login(email: String, pass: String): AuthEntity {
        val response = api.login(LoginRequest(email, pass))
        return AuthEntity(
            email = email,
            token = response.token
        )
    }
}
