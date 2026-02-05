package com.rentitem.features.signup.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rentitem.core.di.AppContainer
import com.rentitem.core.navigation.Screen
import com.rentitem.features.signup.di.SignUpModule
import com.rentitem.features.signup.presentation.screens.SignUpScreen
import com.rentitem.features.signup.presentation.viewmodels.SignUpViewModel

fun NavGraphBuilder.signUpScreen(
    appContainer: AppContainer,
    onNavigateBack: () -> Unit
) {
    composable<Screen.SignUp> {
        val viewModel: SignUpViewModel = viewModel(
            factory = SignUpModule.provideFactory(appContainer.signUpRepository)
        )
        SignUpScreen(
            viewModel = viewModel,
            onBack = onNavigateBack
        )
    }
}
