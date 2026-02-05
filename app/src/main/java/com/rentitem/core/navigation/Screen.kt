package com.rentitem.core.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Login : Screen()

    @Serializable
    data object SignUp : Screen()

    @Serializable
    data object Publications : Screen()
}
