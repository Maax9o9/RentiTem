package com.rentitem.features.login.domain.repositories

import com.rentitem.features.login.domain.entities.AuthEntity

interface LoginRepository {
    suspend fun login(email: String, pass: String): AuthEntity
}
