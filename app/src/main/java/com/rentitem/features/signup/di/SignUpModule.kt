package com.rentitem.features.signup.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.rentitem.features.signup.domain.repositories.SignUpRepository
import com.rentitem.features.signup.domain.usecases.SignUpUseCase
import com.rentitem.features.signup.presentation.viewmodels.SignUpViewModel

object SignUpModule {
    fun provideFactory(repository: SignUpRepository): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return SignUpViewModel(SignUpUseCase(repository)) as T
            }
        }
}
