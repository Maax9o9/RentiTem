package com.rentitem.features.chat.data.datasources.remote.model

import com.google.firebase.Timestamp

data class MessageDto(
    val senderId: String = "",
    val text: String = "",
    val createdAt: Timestamp? = null,
    val read: Boolean = false
)
