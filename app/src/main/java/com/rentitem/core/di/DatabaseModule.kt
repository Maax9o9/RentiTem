package com.rentitem.core.di

import android.content.Context
import com.rentitem.core.database.AppDatabase
import com.rentitem.features.itempublications.data.datasources.local.PublicationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun providePublicationDao(database: AppDatabase): PublicationDao {
        return database.publicationDao()
    }
}
