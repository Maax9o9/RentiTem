package com.rentitem.features.profileInfo.domain.usecases

import com.rentitem.features.profileInfo.domain.entities.UserProfile
import com.rentitem.features.profileInfo.domain.repositories.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(fullName: String, phone: String, address: String): Result<UserProfile> {
        return repository.updateProfile(fullName, phone, address)
    }
}
