package com.rentitem.core.network

import com.rentitem.features.itempublications.data.datasources.remote.model.CreateItemResponse
import com.rentitem.features.itempublications.data.datasources.remote.model.CreatePublicationRequest
import com.rentitem.features.itempublications.data.datasources.remote.model.PublicationDto
import com.rentitem.features.login.data.datasources.remote.model.AuthSyncResponse
import com.rentitem.features.profileInfo.data.datasources.remote.model.UpdateProfileRequest
import com.rentitem.features.profileInfo.data.datasources.remote.model.UserProfileDto
import com.rentitem.features.signup.data.datasources.remote.model.SignUpRequest
import com.rentitem.features.signup.data.datasources.remote.model.SignUpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface RentiTemApi {
    @GET("api/v1/auth/sync")
    suspend fun syncUser(): AuthSyncResponse

    @POST("api/v1/auth/register")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse

    @GET("api/v1/items")
    suspend fun getPublications(): List<PublicationDto>

    @POST("api/v1/items")
    suspend fun createPublication(@Body request: CreatePublicationRequest): CreateItemResponse


    @DELETE("api/v1/items/{id}")
    suspend fun deletePublication(@Path("id") id: Int): Unit

    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): UserProfileDto

    @PUT("api/v1/users/me")
    suspend fun updateCurrentUser(@Body request: UpdateProfileRequest): UserProfileDto
}
