package com.rentitem.features.itempublications.data.datasources.local

import com.rentitem.features.itempublications.data.datasources.local.model.toDomain
import com.rentitem.features.itempublications.data.datasources.local.model.toEntity
import com.rentitem.features.itempublications.domain.entities.Publication

class LocalPublicationDataSource(private val dao: PublicationDao) {

    companion object {
        private const val CACHE_TIMEOUT_MS = 5 * 60 * 1000L // 5 minutos
    }

    suspend fun getPublications(): List<Publication> =
        dao.getAll().map { it.toDomain() }

    suspend fun savePublications(publications: List<Publication>) {
        dao.clearAll()
        dao.upsertAll(publications.map { it.toEntity() })
    }

    suspend fun savePublication(publication: Publication) {
        dao.upsertAll(listOf(publication.toEntity()))
    }

    suspend fun deletePublication(id: Int) {
        dao.deleteById(id)
    }

    suspend fun isCacheValid(): Boolean {
        val lastCached = dao.getLastCacheTime() ?: return false
        return (System.currentTimeMillis() - lastCached) < CACHE_TIMEOUT_MS
    }
}