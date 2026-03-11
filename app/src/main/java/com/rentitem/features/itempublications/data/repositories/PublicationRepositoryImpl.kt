package com.rentitem.features.itempublications.data.repositories

import android.util.Log
import com.rentitem.features.itempublications.data.datasources.local.LocalPublicationDataSource
import com.rentitem.features.itempublications.data.datasources.remote.model.RemotePublicationDataSource
import com.rentitem.features.itempublications.domain.entities.Publication
import com.rentitem.features.itempublications.domain.repositories.PublicationRepository
import java.io.File

class PublicationRepositoryImpl(
    private val remote: RemotePublicationDataSource,
    private val local: LocalPublicationDataSource
) : PublicationRepository {

    override suspend fun getPublications(): List<Publication> {
        if (local.isCacheValid()) {
            Log.d("REPO", "Cache válida — retornando local")
            return local.getPublications()
        }

        return try {
            Log.d("REPO", "Cache vencida — actualizando desde API")
            val publications = remote.getPublications()
            local.savePublications(publications)
            publications
        } catch (e: Exception) {
            Log.w("REPO", "API falló — modo offline: ${e.message}")
            local.getPublications()
        }
    }

    override suspend fun createPublication(
        title: String,
        description: String,
        price: Double,
        priceType: String,
        category: String,
        imageFile: File,
        location: String?,
        latitude: Double?,
        longitude: Double?
    ): Result<Publication> {
        return try {
            val publication = remote.createPublication(
                title, description, price, priceType,
                category, imageFile, location, latitude, longitude
            )
            local.savePublication(publication)
            Result.success(publication)
        } catch (e: Exception) {
            Log.e("REPO", "Error al crear publicación: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deletePublication(id: Int): Result<Unit> {
        return try {
            remote.deletePublication(id)
            local.deletePublication(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
