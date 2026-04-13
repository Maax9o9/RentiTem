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

    override suspend fun updateProfile(fullName: String, phone: String, address: String, profilePicUri: String?): Result<UserProfile> {
        return try {
            var downloadUrl: String? = profilePicUri
            
            if (profilePicUri != null && !profilePicUri.startsWith("http")) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: UUID.randomUUID().toString()
                val ref = FirebaseStorage.getInstance().reference.child("profiles/$uid/profile_pic.jpg")
                ref.putFile(Uri.parse(profilePicUri)).await()
                downloadUrl = ref.downloadUrl.await().toString()
            }

            val request = UpdateProfileRequest(fullName, phone, address, downloadUrl)
            val response = api.updateCurrentUser(request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
