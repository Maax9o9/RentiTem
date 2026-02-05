package com.rentitem.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rentitem.core.di.AppContainer
import com.rentitem.features.itempublications.navigation.itemPublicationsScreen
import com.rentitem.features.login.navigation.loginScreen
import com.rentitem.features.signup.navigation.signUpScreen

@Composable
fun NavigationWrapper(appContainer: AppContainer) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login) {
        loginScreen(
            appContainer = appContainer,
            onNavigateToSignUp = { navController.navigate(Screen.SignUp) },
            onLoginSuccess = { 
                navController.navigate(Screen.Publications) {
                    popUpTo(Screen.Login) { inclusive = true }
                }
            }
        )
        signUpScreen(
            appContainer = appContainer,
            onNavigateBack = { navController.popBackStack() }
        )
        itemPublicationsScreen(appContainer = appContainer)
    }
}
