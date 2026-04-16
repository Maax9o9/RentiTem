package com.rentitem.features.profileInfo.domain.usecases

import com.google.firebase.auth.FirebaseAuth
import com.rentitem.core.storage.TokenManager
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val tokenManager: TokenManager,
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke() {
        tokenManager.clearSession()
        firebaseAuth.signOut()
    }
}
