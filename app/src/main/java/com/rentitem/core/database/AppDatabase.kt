package com.rentitem.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rentitem.features.itempublications.data.datasources.local.PublicationDao
import com.rentitem.features.itempublications.data.datasources.local.model.PublicationEntity
import com.rentitem.features.chat.data.datasources.local.ChatDao
import com.rentitem.features.chat.data.datasources.local.model.MessageEntity

@Database(
    entities = [PublicationEntity::class, MessageEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun publicationDao(): PublicationDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rentitem_db"
                )
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
    }
}
