package com.rentitem.features.itempublications.data.repositories

import android.util.Log
import com.rentitem.features.itempublications.data.datasources.local.LocalPublicationDataSource
import com.rentitem.features.itempublications.data.datasources.remote.model.RemotePublicationDataSource
import com.rentitem.features.itempublications.domain.entities.Publication
import com.rentitem.features.itempublications.domain.repositories.PublicationRepository
import java.io.File
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PublicationRepositoryImpl(
    private val remote: RemotePublicationDataSource,
    private val local: LocalPublicationDataSource
) : PublicationRepository {

    override suspend fun getPublications(): List<Publication> {
        if (local.isCacheValid()) {
            Log.d("REPO", "Cache válida — retornando local")
            val cached = local.getPublications()
            if (cached.isNotEmpty()) return cached
        }

        return try {
            Log.d("REPO", "Actualizando desde API...")
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
            // 1. Upload to Firebase Storage
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
            val fileName = "items/${uid}/${UUID.randomUUID()}.jpg"
            val ref = FirebaseStorage.getInstance().reference.child(fileName)
            
            ref.putFile(Uri.fromFile(imageFile)).await()
            val imageUrl = ref.downloadUrl.await().toString()

            // 2. Create in Backend via JSON
            val publication = remote.createPublication(
                title, description, price, priceType,
                category, imageUrl, location, latitude, longitude
            )
            
            // 3. Save to local cache
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
