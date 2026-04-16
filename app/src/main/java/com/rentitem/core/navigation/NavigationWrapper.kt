package com.rentitem.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rentitem.core.di.AppContainer
import com.rentitem.features.login.navigation.loginScreen
import com.rentitem.features.signup.navigation.signUpScreen

import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavigationWrapper(appContainer: AppContainer) {
    val navController = rememberNavController()
    
    // Verificamos si ya hay una sesión activa en Firebase y tenemos token local
    val currentUser = FirebaseAuth.getInstance().currentUser
    val hasLocalToken = appContainer.tokenManager.getToken() != null
    val startDestination = if (currentUser != null && hasLocalToken) Screen.Main else Screen.Login

    NavHost(navController = navController, startDestination = startDestination) {
        loginScreen(
            appContainer = appContainer,
            onNavigateToSignUp = { navController.navigate(Screen.SignUp) },
            onLoginSuccess = {
                navController.navigate(Screen.Main) {
                    popUpTo(Screen.Login) { inclusive = true }
                }
            }
        )
        signUpScreen(
            appContainer = appContainer,
            onNavigateBack = { navController.popBackStack() }
        )
        composable<Screen.Main> {
            MainScreen(
                appContainer = appContainer,
                onLogout = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Main) { inclusive = true }
                    }
                }
            )
        }
    }
}
