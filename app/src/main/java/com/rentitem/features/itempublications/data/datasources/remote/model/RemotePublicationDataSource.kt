package com.rentitem.features.itempublications.data.datasources.remote.model

import com.google.firebase.auth.FirebaseAuth
import com.rentitem.core.network.RentiTemApi
import com.rentitem.features.itempublications.domain.entities.Publication
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.round

@Singleton
class RemotePublicationDataSource @Inject constructor(
    private val api: RentiTemApi,
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun getPublications(): List<Publication> {
        val dtos = api.getPublications()
        dtos.forEach { dto ->
            android.util.Log.d("API_DEBUG", "Publicación recibida -> ID: ${dto.id}, Titulo: ${dto.title}, UserID: '${dto.userId}'")
        }
        return dtos.map { it.toDomain() }
    }

    private fun roundToSixDecimals(value: Double): Double {
        return (round(value * 1000000.0) / 1000000.0)
    }

    suspend fun createPublication(
        title: String,
        description: String,
        price: Double,
        priceType: String,
        category: String,
        imageUrl: String,
        location: String?,
        latitude: Double?,
        longitude: Double?
    ): Publication {
        val city: String
        val state: String
        if (location != null && location.contains(",")) {
            val parts = location.split(",")
            city = parts[0].trim()
            state = if (parts.size > 1) parts[1].trim() else ""
        } else {
            city = location ?: "Desconocida"
            state = ""
        }

        val request = CreatePublicationRequest(
            title = title,
            description = description,
            price = price,
            priceType = priceType,
            category = category,
            imageUrl = imageUrl,
            city = city,
            state = state,
            // Redondeamos para evitar errores de validación por exceso de decimales en el GPS físico
            latitude = if (latitude != null) roundToSixDecimals(latitude) else 0.0,
            longitude = if (longitude != null) roundToSixDecimals(longitude) else 0.0
        )

        val response = api.createPublication(request)

        val dto = response.item ?: PublicationDto(
            id = response.id ?: 0,
            title = title,
            price = price,
            description = description,
            imageUrl = imageUrl,
            createdAt = null,
            city = city,
            state = state,
            latitude = request.latitude,
            longitude = request.longitude,
            userId = firebaseAuth.currentUser?.uid
        )

        return dto.toDomain()
    }

    suspend fun deletePublication(id: Int) = api.deletePublication(id)
}
