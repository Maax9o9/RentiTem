package com.rentitem.features.signup.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.rentitem.core.network.RentiTemApi
import com.rentitem.core.storage.TokenManager
import com.rentitem.features.signup.data.datasources.remote.model.SignUpRequest
import com.rentitem.features.signup.domain.entities.SignUpEntity
import com.rentitem.features.signup.domain.repositories.SignUpRepository
import kotlinx.coroutines.tasks.await

class SignUpRepositoryImpl(
    private val api: RentiTemApi,
    private val tokenManager: TokenManager,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : SignUpRepository {

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

    override suspend fun signUpWithGoogle(idToken: String): SignUpEntity {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(credential).await()
        val user = authResult.user ?: throw Exception("Error logging in with Google")

        val tokenResult = user.getIdToken(true).await()
        val token = tokenResult.token ?: throw Exception("Error getting Firebase token")

        tokenManager.saveToken(token)

        try {
            api.syncUser()
        } catch (e: Exception) {
            throw e
        }

        return SignUpEntity(
            fullName = user.displayName ?: "Usuario Google",
            email = user.email ?: "",
            phone = "",
            address = "",
            message = "Registro con Google exitoso"
        )
    }
}
