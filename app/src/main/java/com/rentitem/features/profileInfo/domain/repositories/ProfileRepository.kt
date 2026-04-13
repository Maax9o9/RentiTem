package com.rentitem.features.profileInfo.domain.repositories

import com.rentitem.features.profileInfo.domain.entities.UserProfile

interface ProfileRepository {
    suspend fun getProfile(): Result<UserProfile>
    suspend fun updateProfile(fullName: String, phone: String, address: String, profilePicUri: String? = null): Result<UserProfile>
}
