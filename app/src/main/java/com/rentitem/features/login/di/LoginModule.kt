package com.rentitem.features.login.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.rentitem.features.login.domain.repositories.LoginRepository
import com.rentitem.features.login.domain.usecases.LoginUseCase
import com.rentitem.features.login.presentation.viewmodels.LoginViewModel

object LoginModule {
    fun provideFactory(repository: LoginRepository): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return LoginViewModel(LoginUseCase(repository)) as T
            }
        }
}
