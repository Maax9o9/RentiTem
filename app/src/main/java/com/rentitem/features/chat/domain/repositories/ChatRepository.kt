package com.rentitem.features.chat.domain.repositories

import com.rentitem.features.chat.domain.entities.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(conversationId: String): Flow<List<Message>>
    suspend fun sendMessage(conversationId: String, text: String, senderId: String)
}
