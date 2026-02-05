package com.rentitem.core.network

import com.rentitem.features.itempublications.data.datasources.remote.model.CreatePublicationRequest
import com.rentitem.features.itempublications.data.datasources.remote.model.PublicationDto
import com.rentitem.features.login.data.datasources.remote.model.LoginRequest
import com.rentitem.features.login.data.datasources.remote.model.LoginResponse
import com.rentitem.features.signup.data.datasources.remote.model.SignUpRequest
import com.rentitem.features.signup.data.datasources.remote.model.SignUpResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RentiTemApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/v1/auth/register")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse

    @GET("api/v1/publications")
    suspend fun getPublications(): List<PublicationDto>

    @POST("api/v1/items")
    suspend fun createPublication(@Body request: CreatePublicationRequest): PublicationDto
}
