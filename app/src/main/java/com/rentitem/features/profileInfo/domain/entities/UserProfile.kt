package com.rentitem.features.profileInfo.domain.entities

data class UserProfile(
    val id: Int,
    val fullName: String,
    val email: String,
    val address: String,
    val phone: String,
    val profilePic: String?,
    val role: String,
    val completionPercentage: Int = 100,
    val missingFields: List<String> = emptyList()
) {
    val isComplete: Boolean
        get() = completionPercentage == 100
}
