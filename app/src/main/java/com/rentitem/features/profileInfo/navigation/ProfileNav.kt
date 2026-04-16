package com.rentitem.features.profileInfo.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rentitem.core.navigation.Screen
import com.rentitem.features.profileInfo.presentation.screens.ProfileScreen

fun NavGraphBuilder.profileScreen(
    onBack: () -> Unit,
    onLogoutSuccess: () -> Unit = {}
) {
    composable<Screen.Profile> {
        ProfileScreen(
            onBack = onBack,
            onLogoutSuccess = onLogoutSuccess
        )
    }
}
