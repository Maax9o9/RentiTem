package com.rentitem.features.signup.domain.repositories

import com.rentitem.features.signup.domain.entities.SignUpEntity

interface SignUpRepository {
    suspend fun signUp(
        fullName: String,
        email: String,
        pass: String,
        phone: String,
        address: String
    ): SignUpEntity
}
