package com.rentitem.features.chat.domain.entities

data class Message(
    val id: String,
    val text: String,
    val senderId: String,
    val createdAt: Long,
    val isMine: Boolean,
    val isPending: Boolean
)
