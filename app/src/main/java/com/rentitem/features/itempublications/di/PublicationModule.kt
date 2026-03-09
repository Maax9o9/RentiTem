package com.rentitem.features.itempublications.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rentitem.core.di.AppContainer
import com.rentitem.features.itempublications.domain.usecases.CreatePublicationUseCase
import com.rentitem.features.itempublications.domain.usecases.DeletePublicationUseCase
import com.rentitem.features.itempublications.domain.usecases.GetPublicationsUseCase
import com.rentitem.features.itempublications.presentation.viewmodels.PublicationsViewModel

object PublicationModule {
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
