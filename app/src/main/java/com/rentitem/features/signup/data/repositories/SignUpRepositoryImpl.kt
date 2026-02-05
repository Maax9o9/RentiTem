package com.rentitem.features.signup.data.repositories

import com.rentitem.core.network.RentiTemApi
import com.rentitem.features.signup.data.datasources.remote.model.SignUpRequest
import com.rentitem.features.signup.domain.entities.SignUpEntity
import com.rentitem.features.signup.domain.repositories.SignUpRepository

class SignUpRepositoryImpl(
    private val api: RentiTemApi
) : SignUpRepository {
    override suspend fun signUp(
        fullName: String,
        email: String,
        pass: String,
        phone: String,
        address: String
    ): SignUpEntity {
        val request = SignUpRequest(
            fullName = fullName,
            email = email,
            password = pass,
            phone = phone,
            address = address,
            role = "user"
        )
        val response = api.signUp(request)
        
        // Manejamos el caso de que 'user' sea nulo en la respuesta
        return SignUpEntity(
            fullName = response.user?.fullName ?: fullName,
            email = response.user?.email ?: email,
            phone = response.user?.phone ?: phone,
            address = response.user?.address ?: address,
            message = "Registro exitoso"
        )
    }
}
