package com.rentitem.features.chat.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.rentitem.core.database.AppDatabase
import kotlinx.coroutines.tasks.await

class SendMessageWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getInstance(applicationContext)
        val chatDao = database.chatDao()
        val firestore = FirebaseFirestore.getInstance()

        try {
            val pendingMessages = chatDao.getPendingMessages()
            if (pendingMessages.isEmpty()) return Result.success()

            for (msg in pendingMessages) {
                // 1. Asegurar que el documento de la conversación existe y actualizar metadata
                val participants = msg.conversationId.split("_")
                val convData = mutableMapOf<String, Any>(
                    "lastMessage" to msg.text,
                    "lastMessageTime" to com.google.firebase.Timestamp(java.util.Date(msg.createdAt))
                )
                
                // Intentar recuperar participantes del ID si es posible
                if (participants.size >= 2) {
                    convData["participants"] = participants
                }

                firestore.collection("conversations")
                    .document(msg.conversationId)
                    .set(convData, SetOptions.merge())
                    .await()

                // 2. Subir el mensaje a la subcolección
                val messageData = hashMapOf(
                    "senderId" to msg.senderId,
                    "text" to msg.text,
                    "createdAt" to com.google.firebase.Timestamp(java.util.Date(msg.createdAt)),
                    "read" to false
                )
                
                firestore.collection("conversations")
                    .document(msg.conversationId)
                    .collection("messages")
                    .document(msg.id) 
                    .set(messageData)
                    .await()

                chatDao.markAsSent(msg.id)
            }
            return Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }
}
