package com.rentitem.features.profileInfo.domain.usecases

import com.google.firebase.auth.FirebaseAuth
import com.rentitem.core.storage.TokenManager
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val tokenManager: TokenManager
) {
    operator fun invoke() {
        tokenManager.clearSession()
        FirebaseAuth.getInstance().signOut()
    }
}
