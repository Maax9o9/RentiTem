package com.rentitem.features.profileInfo.data.repositories

import com.rentitem.core.network.RentiTemApi
import com.rentitem.features.profileInfo.data.datasources.remote.model.UpdateProfileRequest
import com.rentitem.features.profileInfo.data.datasources.remote.model.toDomain
import com.rentitem.features.profileInfo.domain.entities.UserProfile
import com.rentitem.features.profileInfo.domain.repositories.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: RentiTemApi
) : ProfileRepository {

    override suspend fun getProfile(): Result<UserProfile> {
        return try {
            val response = api.getCurrentUser()
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(fullName: String, phone: String, address: String): Result<UserProfile> {
        return try {
            val request = UpdateProfileRequest(fullName, phone, address)
            val response = api.updateCurrentUser(request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
