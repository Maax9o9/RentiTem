package com.rentitem.core.di

import android.content.Context
import com.rentitem.core.database.AppDatabase
import com.rentitem.core.hardware.data.AndroidCameraManager
import com.rentitem.core.hardware.data.AndroidGpsManager
import com.rentitem.core.hardware.domain.CameraManager
import com.rentitem.core.hardware.domain.GpsManager
import com.rentitem.core.network.AuthInterceptor
import com.rentitem.core.network.RentiTemApi
import com.rentitem.core.storage.TokenManager
import com.rentitem.features.itempublications.data.datasources.local.LocalPublicationDataSource
import com.rentitem.features.itempublications.data.datasources.remote.model.RemotePublicationDataSource
import com.rentitem.features.itempublications.data.repositories.PublicationRepositoryImpl
import com.rentitem.features.itempublications.domain.repositories.PublicationRepository
import com.rentitem.features.login.data.repositories.LoginRepositoryImpl
import com.rentitem.features.login.domain.repositories.LoginRepository
import com.rentitem.features.profileInfo.data.repositories.ProfileRepositoryImpl
import com.rentitem.features.profileInfo.domain.repositories.ProfileRepository
import com.rentitem.features.profileInfo.domain.usecases.GetProfileUseCase
import com.rentitem.features.signup.data.repositories.SignUpRepositoryImpl
import com.rentitem.features.signup.domain.repositories.SignUpRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

interface AppContainer {
    val tokenManager: TokenManager
    val loginRepository: LoginRepository
    val signUpRepository: SignUpRepository
    val publicationRepository: PublicationRepository
    val gpsManager: GpsManager
    val cameraManager: CameraManager
    val profileRepository: ProfileRepository
    val getProfileUseCase: GetProfileUseCase
}

class AppContainerImpl(private val context: Context) : AppContainer {
    private val baseUrl = "http://192.168.1.9:8080/"

    override val tokenManager: TokenManager by lazy {
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
        SignUpRepositoryImpl(api, tokenManager)
    }

    override val gpsManager: GpsManager by lazy {
        AndroidGpsManager(context)
    }

    override val cameraManager: CameraManager by lazy {
        AndroidCameraManager(context)
    }

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    private val localPublicationDataSource: LocalPublicationDataSource by lazy {
        LocalPublicationDataSource(database.publicationDao())
    }

    private val remotePublicationDataSource: RemotePublicationDataSource by lazy {
        RemotePublicationDataSource(api)
    }

    override val publicationRepository: PublicationRepository by lazy {
        PublicationRepositoryImpl(remotePublicationDataSource, localPublicationDataSource)
    }

    override val profileRepository: ProfileRepository by lazy {
        ProfileRepositoryImpl(api)
    }

    override val getProfileUseCase: GetProfileUseCase by lazy {
        GetProfileUseCase(profileRepository)
    }
}
