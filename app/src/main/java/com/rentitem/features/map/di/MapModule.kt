package com.rentitem.features.map.di

import androidx.lifecycle.ViewModelProvider
import com.rentitem.core.di.AppContainer
import com.rentitem.features.map.domain.usecases.GetPublicationsForMapUseCase
import com.rentitem.features.map.presentation.viewmodels.MapViewModel

object MapModule {
    fun provideFactory(appContainer: AppContainer): ViewModelProvider.Factory =
        MapViewModel.provideFactory(
            GetPublicationsForMapUseCase(appContainer.publicationRepository)
        )
}
