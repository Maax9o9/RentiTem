package com.rentitem.features.itempublications.data.datasources.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rentitem.features.itempublications.data.datasources.local.model.PublicationEntity

@Dao
interface PublicationDao {

    @Query("SELECT * FROM publications ORDER BY createdAt DESC")
    suspend fun getAll(): List<PublicationEntity>

    @Upsert
    suspend fun upsertAll(publications: List<PublicationEntity>)

    @Query("DELETE FROM publications WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM publications")
    suspend fun clearAll()

    @Query("SELECT cachedAt FROM publications LIMIT 1")
    suspend fun getLastCacheTime(): Long?
}