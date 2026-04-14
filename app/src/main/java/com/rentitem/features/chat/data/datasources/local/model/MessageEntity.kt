package com.rentitem.features.chat.data.datasources.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val senderId: String,
    val text: String,
    val createdAt: Long,
    val read: Boolean,
    val isPending: Boolean = false // to track offline messages awaiting dispatch
)
