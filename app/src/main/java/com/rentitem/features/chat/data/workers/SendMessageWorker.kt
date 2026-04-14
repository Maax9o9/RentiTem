package com.rentitem.features.chat.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
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
                val data = hashMapOf(
                    "senderId" to msg.senderId,
                    "text" to msg.text,
                    "createdAt" to com.google.firebase.Timestamp(java.util.Date(msg.createdAt)),
                    "read" to false
                )
                
                firestore.collection("conversations")
                    .document(msg.conversationId)
                    .collection("messages")
                    .document(msg.id) 
                    .set(data)
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
