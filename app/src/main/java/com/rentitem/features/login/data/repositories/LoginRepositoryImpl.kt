package com.rentitem.features.login.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.rentitem.core.network.RentiTemApi
import com.rentitem.core.storage.TokenManager
import com.rentitem.features.login.domain.entities.AuthEntity
import com.rentitem.features.login.domain.repositories.LoginRepository
import kotlinx.coroutines.tasks.await

class LoginRepositoryImpl(
    private val api: RentiTemApi,
    private val tokenManager: TokenManager
) : LoginRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    override suspend fun login(email: String, pass: String): AuthEntity {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
        val user = authResult.user ?: throw Exception("Error logging in with Firebase")

        val tokenResult = user.getIdToken(true).await()
        val token = tokenResult.token ?: throw Exception("Error getting Firebase token")

        tokenManager.saveToken(token)

        try {
            api.syncUser()
        } catch (e: Exception) {
            throw e
        }

        return AuthEntity(email, token)
    }
}
