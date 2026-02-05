package com.rentitem.features.login.data.repositories

import com.rentitem.core.network.RentiTemApi
import com.rentitem.core.storage.TokenManager
import com.rentitem.features.login.data.datasources.remote.model.LoginRequest
import com.rentitem.features.login.domain.entities.AuthEntity
import com.rentitem.features.login.domain.repositories.LoginRepository

class LoginRepositoryImpl(
    private val api: RentiTemApi,
    private val tokenManager: TokenManager
) : LoginRepository {

    override suspend fun login(email: String, pass: String): AuthEntity {
        val requestDto = LoginRequest(email = email, password = pass)

        val response = api.login(requestDto)

        tokenManager.saveToken(response.token)

        return AuthEntity(email, response.token)
    }
}
