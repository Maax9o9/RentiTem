package com.rentitem.core.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Login : Screen()

    @Serializable
    data object SignUp : Screen()

    @Serializable
    data object Main : Screen()

    @Serializable
    data object Publications : Screen()

    @Serializable
    data object Map : Screen()

    @Serializable
    data object Profile : Screen()
}
