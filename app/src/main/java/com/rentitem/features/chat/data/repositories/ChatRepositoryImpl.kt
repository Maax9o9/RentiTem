package com.rentitem.features.chat.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rentitem.features.chat.data.workers.SendMessageWorker
import com.rentitem.features.chat.data.datasources.local.ChatDao
import com.rentitem.features.chat.data.datasources.local.model.MessageEntity
import com.rentitem.features.chat.data.datasources.remote.model.MessageDto
import com.rentitem.features.chat.domain.entities.Message
import com.rentitem.features.chat.domain.repositories.ChatRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class ChatRepositoryImpl(
    private val context: Context,
    private val firestore: FirebaseFirestore,
    private val chatDao: ChatDao,
    private val currentUserId: String
) : ChatRepository {

    override fun getMessages(conversationId: String): Flow<List<Message>> {
        // Implementación Offline-First: Single Source of Truth
        setupFirebaseListener(conversationId)

        return chatDao.getMessagesForConversation(conversationId).map { entities ->
            entities.map { entity ->
                Message(
                    id = entity.id,
                    text = entity.text,
                    senderId = entity.senderId,
                    createdAt = entity.createdAt,
                    isMine = entity.senderId == currentUserId,
                    isPending = entity.isPending
                )
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setupFirebaseListener(conversationId: String) {
        val collectionRef = firestore.collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)

        collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                error.printStackTrace()
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val newMessages = snapshot.documents.mapNotNull { doc ->
                    val dto = doc.toObject(MessageDto::class.java)
                    if (dto != null && !doc.metadata.hasPendingWrites()) {
                        MessageEntity(
                            id = doc.id,
                            conversationId = conversationId,
                            senderId = dto.senderId,
                            text = dto.text,
                            createdAt = dto.createdAt?.toDate()?.time ?: System.currentTimeMillis(),
                            read = dto.read,
                            isPending = false
                        )
                    } else null
                }

                // Se delega a GlobalScope (o un Scope custom de inyección)
                // para evitar bloquear el hilo del Snapshot con I/O de Room.
                GlobalScope.launch {
                    chatDao.insertMessages(newMessages)
                }
            }
        }
    }

    override suspend fun sendMessage(conversationId: String, text: String, senderId: String) {
        val localId = UUID.randomUUID().toString()
        val tempEntity = MessageEntity(
            id = localId,
            conversationId = conversationId,
            senderId = senderId,
            text = text,
            createdAt = System.currentTimeMillis(),
            read = false,
            isPending = true
        )
        chatDao.insertMessage(tempEntity)
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val uploadWorkRequest = OneTimeWorkRequestBuilder<SendMessageWorker>()
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(context).enqueue(uploadWorkRequest)
    }
}
