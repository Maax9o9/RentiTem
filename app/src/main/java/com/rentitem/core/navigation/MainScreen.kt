package com.rentitem.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.hilt.navigation.compose.hiltViewModel
import com.rentitem.features.chat.presentation.screens.ChatScreen
import com.rentitem.features.chat.presentation.viewmodels.ChatViewModel
import com.rentitem.features.chat.presentation.screens.ChatListScreen
import com.rentitem.features.chat.presentation.viewmodels.ChatListViewModel
import com.rentitem.core.di.AppContainer
import com.rentitem.features.itempublications.di.PublicationModule
import com.rentitem.features.itempublications.presentation.screens.PublicationsScreen
import com.rentitem.features.itempublications.presentation.viewmodels.PublicationsViewModel
import com.rentitem.features.map.di.MapModule
import com.rentitem.features.map.presentation.screens.MapScreen
import com.rentitem.features.map.presentation.viewmodels.MapViewModel
import com.rentitem.features.profileInfo.presentation.screens.ProfileScreen

data class BottomNavItem(
    val route: Any,
    val label: String,
    val icon: ImageVector
)

@Composable
fun MainScreen(appContainer: AppContainer, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()

    // ViewModel de publicaciones para observar el estado de la cámara
    val publicationsViewModel: PublicationsViewModel = viewModel(
        factory = PublicationModule.provideFactory(appContainer)
    )
    val formState by publicationsViewModel.formState.collectAsStateWithLifecycle()
    val isCameraOpen = formState.showCamera

    val items = listOf(
        BottomNavItem(Screen.Publications, "Inicio", Icons.Rounded.Home),
        BottomNavItem(Screen.Map, "Mapa", Icons.Rounded.Map),
        BottomNavItem(Screen.Profile, "Perfil", Icons.Rounded.Person)
    )

    Scaffold(
        bottomBar = {
            // Solo mostramos la barra si la cámara NO está abierta
            if (!isCameraOpen) {
                NavigationBar {
                    items.forEach { item ->
                        val isSelected = currentBackStack?.destination?.route
                            ?.contains(item.route::class.simpleName ?: "") == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) return@NavigationBarItem
                                
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = false
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Publications,
            modifier = Modifier.padding(if (isCameraOpen) PaddingValues(0.dp) else padding)
        ) {
            composable<Screen.Publications> {
                PublicationsScreen(
                    viewModel = publicationsViewModel,
                    currentUserId = appContainer.tokenManager.getUid() ?: "",
                    onProfileClick = {
                        navController.navigate(Screen.Profile) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onChatClick = { userId, contactName ->  
                        val myUid = appContainer.tokenManager.getUid() ?: ""
                        val conversationId = if (myUid < userId) "${myUid}_$userId" else "${userId}_$myUid"
                        navController.navigate(Screen.Chat(conversationId = conversationId, contactName = contactName))
                    },
                    onChatListClick = {
                        navController.navigate(Screen.ChatList)
                    },
                    cameraManager = appContainer.cameraManager
                )
            }

            composable<Screen.Map> {
                val mapViewModel: MapViewModel = viewModel(
                    factory = MapModule.provideFactory(appContainer)
                )
                MapScreen(viewModel = mapViewModel)
            }

            composable<Screen.Profile> {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onLogoutSuccess = onLogout
                )
            }

            composable<Screen.ChatList> {
                val chatListViewModel: ChatListViewModel = hiltViewModel()
                ChatListScreen(
                    viewModel = chatListViewModel,
                    onChatClick = { conversationId, contactName ->
                        navController.navigate(Screen.Chat(conversationId = conversationId, contactName = contactName))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable<Screen.Chat> { backStackEntry ->
                val chatArgs = backStackEntry.toRoute<Screen.Chat>()
                val chatViewModel: ChatViewModel = hiltViewModel()

                ChatScreen(
                    viewModel = chatViewModel,
                    contactName = chatArgs.contactName,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
