package com.rentitem.features.itempublications.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rentitem.core.di.AppContainer
import com.rentitem.core.navigation.Screen
import com.rentitem.features.itempublications.di.PublicationModule
import com.rentitem.features.itempublications.presentation.screens.PublicationsScreen
import com.rentitem.features.itempublications.presentation.viewmodels.PublicationsViewModel

fun NavGraphBuilder.itemPublicationsScreen(
    appContainer: AppContainer,
    onNavigateToProfile: () -> Unit,
    onNavigateToChat: (String, String) -> Unit = { _, _ -> },
    onNavigateToChatList: () -> Unit = {}
) {
    composable<Screen.Publications> {
        val viewModel: PublicationsViewModel = viewModel(
            factory = PublicationModule.provideFactory(appContainer)
        )
        PublicationsScreen(
            viewModel = viewModel,
            currentUserId = appContainer.tokenManager.getUid() ?: "",
            onProfileClick = onNavigateToProfile,
            onChatClick = onNavigateToChat,
            onChatListClick = onNavigateToChatList,
            cameraManager = appContainer.cameraManager
        )
    }
}
