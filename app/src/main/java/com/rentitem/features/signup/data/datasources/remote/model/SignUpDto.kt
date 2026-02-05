package com.rentitem.features.signup.data.datasources.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id") val id: Int,
    @SerialName("full_name") val fullName: String,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String,
    @SerialName("address") val address: String
)

@Serializable
data class SignUpRequest(
    @SerialName("full_name") val fullName: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("phone") val phone: String,
    @SerialName("address") val address: String,
    @SerialName("role") val role: String
)

@Serializable
data class SignUpResponse(
    @SerialName("token") val token: String,
    // El campo 'user' ahora es opcional para evitar crashes si no viene en la respuesta
    @SerialName("user") val user: UserDto? = null 
)
