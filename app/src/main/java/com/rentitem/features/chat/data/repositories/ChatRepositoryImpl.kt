package com.rentitem.features.chat.data.repositories

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rentitem.features.chat.data.datasources.local.ChatDao
import com.rentitem.features.chat.data.datasources.local.model.MessageEntity
import com.rentitem.features.chat.data.datasources.remote.model.MessageDto
import com.rentitem.features.chat.data.workers.SendMessageWorker
import com.rentitem.features.chat.domain.entities.Conversation
import com.rentitem.features.chat.domain.entities.Message
import com.rentitem.core.storage.TokenManager
import com.rentitem.features.chat.domain.repositories.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChatRepositoryImpl(
    private val context: Context,
    private val firestore: FirebaseFirestore,
    private val chatDao: ChatDao,
    private val tokenManager: TokenManager
) : ChatRepository {

    private val currentUserId: String
        get() = tokenManager.getUid() ?: ""

    override fun getConversations(): Flow<List<Conversation>> = callbackFlow {
        if (currentUserId.isEmpty()) {
            trySend(emptyList())
            awaitClose()
            return@callbackFlow
        }

        val query = firestore.collection("conversations")
            .whereArrayContains("participants", currentUserId)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                launch {
                    val conversationsList = mutableListOf<Conversation>()
                    
                    for (doc in snapshot.documents) {
                        try {
                            val participants = doc.get("participants") as? List<String> ?: emptyList()
                            val otherUid = participants.find { it != currentUserId } ?: continue
                            
                            // Datos del otro usuario
                            val userDoc = firestore.collection("users").document(otherUid).get().await()
                            val otherName = userDoc.getString("fullName") ?: "Usuario"
                            
                            // Último mensaje
                            val lastMsgQuery = doc.reference.collection("messages")
                                .orderBy("createdAt", Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .await()
                            
                            val lastMsg = lastMsgQuery.documents.firstOrNull()
                            val lastText = lastMsg?.getString("text") ?: "Sin mensajes"
                            val lastTime = lastMsg?.getTimestamp("createdAt")?.toDate()?.time ?: 0L

                            conversationsList.add(
                                Conversation(
                                    id = doc.id,
                                    otherUserId = otherUid,
                                    otherUserName = otherName,
                                    lastMessage = lastText,
                                    lastMessageTime = lastTime
                                )
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    trySend(conversationsList)
                }
            }
        }

        awaitClose { listener.remove() }
    }

    override fun getTotalUnreadCount(): Flow<Int> = callbackFlow {
        if (currentUserId.isEmpty()) {
            trySend(0)
            awaitClose()
            return@callbackFlow
        }

        val query = firestore.collection("conversations")
            .whereArrayContains("participants", currentUserId)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                var totalUnread = 0
                for (doc in snapshot.documents) {
                    val unreadCountMap = doc.get("unreadCount") as? Map<String, Long>
                    totalUnread += (unreadCountMap?.get(currentUserId)?.toInt() ?: 0)
                }
                trySend(totalUnread)
            }
        }

        awaitClose { listener.remove() }
    }

    override fun getMessages(conversationId: String): Flow<List<Message>> {
        val firebaseListenerFlow = callbackFlow {
            val collectionRef = firestore.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .orderBy("createdAt", Query.Direction.ASCENDING)

            val listener = collectionRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Resetear contador de no leídos al entrar al chat
                    if (currentUserId.isNotEmpty()) {
                        firestore.collection("conversations")
                            .document(conversationId)
                            .update("unreadCount.$currentUserId", 0)
                    }

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
                    
                    launch {
                        chatDao.insertMessages(newMessages)
                    }
                }
                trySend(Unit)
            }

            awaitClose { listener.remove() }
        }

        return firebaseListenerFlow.flatMapLatest {
            chatDao.getMessagesForConversation(conversationId).map { entities ->
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
