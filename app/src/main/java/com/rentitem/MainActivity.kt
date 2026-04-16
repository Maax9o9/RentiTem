package com.rentitem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rentitem.core.di.AppContainer
import com.rentitem.core.di.AppContainerImpl
import com.rentitem.core.navigation.NavigationWrapper
import com.rentitem.ui.theme.RentiTemTheme
import dagger.hilt.android.AndroidEntryPoint

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        appContainer = AppContainerImpl(this)

        val currentUser = appContainer.firebaseAuth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            if (appContainer.tokenManager.getUid() == null) {
                appContainer.tokenManager.saveUid(uid)
            }
            syncFcmToken(uid)
        }
        
        enableEdgeToEdge()
        setContent {
            RentiTemTheme {
                NavigationWrapper(appContainer)
            }
        }
    }

    private fun syncFcmToken(uid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = appContainer.firebaseMessaging.token.await()
                val firestore = appContainer.firestore
                val data = hashMapOf("fcmToken" to token, "uid" to uid)
                firestore.collection("users").document(uid).set(data, SetOptions.merge()).await()
                android.util.Log.d("FCM_SYNC", "Token sincronizado: $token")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
