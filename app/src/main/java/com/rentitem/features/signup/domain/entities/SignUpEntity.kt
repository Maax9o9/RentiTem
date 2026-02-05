package com.rentitem.features.signup.domain.entities

data class SignUpEntity(
    val fullName: String,
    val email: String,
    val phone: String,
    val address: String,
    val message: String? = null
)
