package com.rentitem.core.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rentitem.core.di.AppContainer
import com.rentitem.core.navigation.Screen
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
fun MainScreen(appContainer: AppContainer) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()

    val items = listOf(
        BottomNavItem(Screen.Publications, "Inicio", Icons.Rounded.Home),
        BottomNavItem(Screen.Map, "Mapa", Icons.Rounded.Map),
        BottomNavItem(Screen.Profile, "Perfil", Icons.Rounded.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    val isSelected = currentBackStack?.destination?.route
                        ?.contains(item.route::class.simpleName ?: "") == true

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
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
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Publications
        ) {
            composable<Screen.Publications> {
                val publicationsViewModel: PublicationsViewModel = viewModel(
                    factory = PublicationModule.provideFactory(appContainer)
                )
                PublicationsScreen(
                    viewModel = publicationsViewModel,
                    onProfileClick = {
                        navController.navigate(Screen.Profile) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                ProfileScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
