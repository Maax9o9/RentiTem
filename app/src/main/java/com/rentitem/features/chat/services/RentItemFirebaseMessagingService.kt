package com.rentitem.features.chat.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rentitem.MainActivity
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RentItemFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        val title = remoteMessage.notification?.title ?: data["title"] ?: "Nuevo mensaje"
        val body = remoteMessage.notification?.body ?: data["body"] ?: "Tienes un nuevo mensaje"
        val profilePicUrl = data["profilePicUrl"]

        // Hacer la carga de la imagen fuera del hilo principal usando Coroutinas (pero lanzando en IO)
        CoroutineScope(Dispatchers.IO).launch {
            var bitmap: Bitmap? = null
            if (!profilePicUrl.isNullOrEmpty()) {
                bitmap = getBitmapFromUrl(profilePicUrl)
            }
            withContext(Dispatchers.Main) {
                sendNotification(title, body, bitmap)
            }
        }
    }

    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun sendNotification(title: String, messageBody: String, bitmap: Bitmap?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "chat_notifications"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Si tenemos la foto del remitente, la ponemos como LargeIcon (círculo pequeño)
        // No usamos BigPictureStyle para que no se vea gigante
        if (bitmap != null) {
            notificationBuilder.setLargeIcon(bitmap)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones de Chat",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Muestra mensajes de chat con foto del remitente"
                // Aquí podrías habilitar/deshabilitar vibración según el estado de la app
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        // Ignoramos ya que nuestro login flow captura el token eficientemente.
        super.onNewToken(token)
    }
}
