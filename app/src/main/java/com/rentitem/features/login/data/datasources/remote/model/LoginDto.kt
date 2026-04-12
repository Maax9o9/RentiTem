package com.rentitem.features.login.data.datasources.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthSyncResponse(
    @SerialName("message") val message: String
)
