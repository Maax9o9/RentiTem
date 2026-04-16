package com.rentitem.features.profileInfo.data.repositories

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.rentitem.core.network.RentiTemApi
import com.rentitem.features.profileInfo.data.datasources.remote.model.UpdateProfileRequest
import com.rentitem.features.profileInfo.data.datasources.remote.model.toDomain
import com.rentitem.features.profileInfo.domain.entities.UserProfile
import com.rentitem.features.profileInfo.domain.repositories.ProfileRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: RentiTemApi,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage
) : ProfileRepository {

    override suspend fun getProfile(): Result<UserProfile> {
        return try {
            val response = api.getCurrentUser()
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(fullName: String, phone: String, address: String, profilePicUri: String?): Result<UserProfile> {
        return try {
            var downloadUrl: String? = profilePicUri
            
            if (profilePicUri != null && !profilePicUri.startsWith("http")) {
                val uid = firebaseAuth.currentUser?.uid ?: UUID.randomUUID().toString()
                val ref = firebaseStorage.reference.child("profiles/$uid/profile_pic.jpg")
                ref.putFile(Uri.parse(profilePicUri)).await()
                downloadUrl = ref.downloadUrl.await().toString()
            }

            val request = UpdateProfileRequest(fullName, phone, address, downloadUrl)
            try {
                api.updateCurrentUser(request)
            } catch (e: Exception) {

            }
            

            val updatedProfile = UserProfile(
                id = 0,
                fullName = fullName,
                email = "",
                phone = phone,
                address = address,
                profilePic = downloadUrl,
                role = "user",
                completionPercentage = 100,
                missingFields = emptyList()
            )
            Result.success(updatedProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
