package com.rentitem.features.chat.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.rentitem.core.database.AppDatabase
import com.rentitem.core.storage.TokenManager
import com.rentitem.features.chat.data.repositories.ChatRepositoryImpl
import com.rentitem.features.chat.domain.repositories.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideChatRepository(
        @ApplicationContext context: Context,
        tokenManager: TokenManager
    ): ChatRepository {
        val firestore = FirebaseFirestore.getInstance()
        val chatDao = AppDatabase.getInstance(context).chatDao()
        
        return ChatRepositoryImpl(
            context = context,
            firestore = firestore,
            chatDao = chatDao,
            tokenManager = tokenManager
        )
    }
}
