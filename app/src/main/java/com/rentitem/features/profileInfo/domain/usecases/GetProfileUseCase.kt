package com.rentitem.features.profileInfo.domain.usecases

import com.rentitem.features.profileInfo.domain.entities.UserProfile
import com.rentitem.features.profileInfo.domain.repositories.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<UserProfile> {
        return repository.getProfile()
    }
}
