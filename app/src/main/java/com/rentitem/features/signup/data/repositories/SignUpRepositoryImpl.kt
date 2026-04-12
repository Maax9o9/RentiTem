package com.rentitem.features.signup.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.rentitem.core.network.RentiTemApi
import com.rentitem.features.signup.data.datasources.remote.model.SignUpRequest
import com.rentitem.features.signup.domain.entities.SignUpEntity
import com.rentitem.features.signup.domain.repositories.SignUpRepository
import kotlinx.coroutines.tasks.await

class SignUpRepositoryImpl(
    private val api: RentiTemApi
) : SignUpRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    override suspend fun signUp(
        fullName: String,
        email: String,
        pass: String,
        phone: String,
        address: String
    ): SignUpEntity {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
        val user = authResult.user ?: throw Exception("Error creating user in Firebase")

        val request = SignUpRequest(
            firebaseUid = user.uid,
            fullName = fullName,
            email = email,
            phone = phone,
            address = address,
            role = "user"
        )
        api.signUp(request)
        
        return SignUpEntity(
            fullName = fullName,
            email = email,
            phone = phone,
            address = address,
            message = "Registro exitoso"
        )
    }
}
