package com.rentitem.features.itempublications.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rentitem.core.di.AppContainer
import com.rentitem.features.itempublications.data.repositories.PublicationRepositoryImpl
import com.rentitem.features.itempublications.domain.repositories.PublicationRepository
import com.rentitem.features.itempublications.domain.usecases.CreatePublicationUseCase
import com.rentitem.features.itempublications.domain.usecases.DeletePublicationUseCase
import com.rentitem.features.itempublications.domain.usecases.GetPublicationsUseCase
import com.rentitem.features.itempublications.presentation.viewmodels.PublicationsViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PublicationModule {

    @Binds
    @Singleton
    abstract fun bindPublicationRepository(
        impl: PublicationRepositoryImpl
    ): PublicationRepository

    companion object {
        fun provideFactory(appContainer: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PublicationsViewModel(
                        getPublicationsUseCase = GetPublicationsUseCase(appContainer.publicationRepository),
                        createPublicationUseCase = CreatePublicationUseCase(appContainer.publicationRepository),
                        deletePublicationUseCase = DeletePublicationUseCase(appContainer.publicationRepository),
                        gpsManager = appContainer.gpsManager
                    ) as T
                }
            }
    }
}
