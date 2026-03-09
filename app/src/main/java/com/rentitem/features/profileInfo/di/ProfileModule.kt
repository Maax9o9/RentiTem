package com.rentitem.features.profileInfo.di

import com.rentitem.core.network.RentiTemApi
import com.rentitem.features.profileInfo.data.repositories.ProfileRepositoryImpl
import com.rentitem.features.profileInfo.domain.repositories.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository
}
