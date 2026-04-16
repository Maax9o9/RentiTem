package com.rentitem.features.chat.domain.entities

data class Conversation(
    val id: String,
    val otherUserId: String,
    val otherUserName: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int = 0
)
