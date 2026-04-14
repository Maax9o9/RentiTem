package com.rentitem.features.profileInfo.data.datasources.remote.model

import com.rentitem.features.profileInfo.domain.entities.UserProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    @SerialName("id") val id: Int,
    @SerialName("full_name") val fullName: String,
    @SerialName("email") val email: String,
    @SerialName("address") val address: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("profile_pic") val profilePic: String? = null,
    @SerialName("role") val role: String,
    @SerialName("completion_percentage") val completionPercentage: Int = 100,
    @SerialName("missing_fields") val missingFields: List<String> = emptyList()
)

fun UserProfileDto.toDomain(): UserProfile {
    val baseUrl = "http://10.0.2.2:8080/"
    return UserProfile(
        id = id,
        fullName = fullName,
        email = email,
        address = address ?: "",
        phone = phone ?: "",
        profilePic = if (!profilePic.isNullOrBlank()) {
            if (profilePic.startsWith("http")) profilePic else {
                val cleanPath = profilePic.removePrefix("/")
                "$baseUrl$cleanPath"
            }
        } else null,
        role = role,
        completionPercentage = completionPercentage,
        missingFields = missingFields
    )
}
