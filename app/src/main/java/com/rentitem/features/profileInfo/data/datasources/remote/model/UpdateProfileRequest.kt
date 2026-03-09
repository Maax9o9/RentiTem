package com.rentitem.features.profileInfo.data.datasources.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    @SerialName("full_name") val fullName: String,
    @SerialName("phone") val phone: String,
    @SerialName("address") val address: String,
    @SerialName("profile_pic") val profilePic: String? = null
)
