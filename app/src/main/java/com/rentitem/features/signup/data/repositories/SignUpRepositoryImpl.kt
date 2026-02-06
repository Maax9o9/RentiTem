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
        
        // El error ocurría aquí porque se intentaba acceder a 'user' sin seguridad nula.
        // Ahora usamos los datos de la respuesta si existen, o los del formulario como respaldo.
        return SignUpEntity(
            fullName = response.user?.fullName ?: fullName,
            email = response.user?.email ?: email,
            phone = response.user?.phone ?: phone,
            address = response.user?.address ?: address,
            message = "Registro exitoso"
        )
    }
}
