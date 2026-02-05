package com.rentitem.core.di

import android.content.Context
import com.rentitem.core.network.AuthInterceptor
import com.rentitem.core.network.RentiTemApi
import com.rentitem.features.itempublications.data.repositories.PublicationRepositoryImpl
import com.rentitem.features.itempublications.domain.repositories.PublicationRepository
import com.rentitem.features.login.data.repositories.LoginRepositoryImpl
import com.rentitem.features.login.domain.repositories.LoginRepository
import com.rentitem.features.signup.data.repositories.SignUpRepositoryImpl
import com.rentitem.features.signup.domain.repositories.SignUpRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.rentitem.core.storage.TokenManager

interface AppContainer {
    val loginRepository: LoginRepository
    val signUpRepository: SignUpRepository
    val publicationRepository: PublicationRepository
}

class AppContainerImpl (private val context: Context) : AppContainer {
    private val baseUrl = "https://codigoverse.space/"

    private val tokenManager: TokenManager by lazy {
        TokenManager(context)
    }

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = AuthInterceptor(tokenManager)

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val api: RentiTemApi by lazy {
        retrofit.create(RentiTemApi::class.java)
    }

    override val loginRepository: LoginRepository by lazy {
        LoginRepositoryImpl(api, tokenManager)
    }

    override val signUpRepository: SignUpRepository by lazy {
        SignUpRepositoryImpl(api)
    }

    override val publicationRepository: PublicationRepository by lazy {
        PublicationRepositoryImpl(api)
    }
}
