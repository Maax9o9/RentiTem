package com.rentitem.features.login.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rentitem.core.di.AppContainer
import com.rentitem.core.navigation.Screen
import com.rentitem.features.login.di.LoginModule
import com.rentitem.features.login.presentation.screens.LoginScreen
import com.rentitem.features.login.presentation.viewmodels.LoginViewModel

fun NavGraphBuilder.loginScreen(
    appContainer: AppContainer,
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    composable<Screen.Login> {
        val viewModel: LoginViewModel = viewModel(
            factory = LoginModule.provideFactory(appContainer.loginRepository)
        )
        LoginScreen(
            viewModel = viewModel,
            onNavigateToSignUp = onNavigateToSignUp,
            onLoginSuccess = onLoginSuccess
        )
    }
}
