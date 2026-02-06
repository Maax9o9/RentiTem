package com.rentitem.core.network

import com.rentitem.features.itempublications.data.datasources.remote.model.CreateItemResponse
import com.rentitem.features.itempublications.data.datasources.remote.model.CreatePublicationRequest
import com.rentitem.features.itempublications.data.datasources.remote.model.PublicationDto
import com.rentitem.features.login.data.datasources.remote.model.LoginRequest
import com.rentitem.features.login.data.datasources.remote.model.LoginResponse
import com.rentitem.features.signup.data.datasources.remote.model.SignUpRequest
import com.rentitem.features.signup.data.datasources.remote.model.SignUpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RentiTemApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/v1/auth/register")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse

    @GET("api/v1/items")
    suspend fun getPublications(): List<PublicationDto>

    @Multipart
    @POST("api/v1/items")
    suspend fun createPublication(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("price_type") priceType: RequestBody,
        @Part("category") category: RequestBody,
        @Part image: MultipartBody.Part
    ): CreateItemResponse
}
