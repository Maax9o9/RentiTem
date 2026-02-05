package com.rentitem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rentitem.core.di.AppContainer
import com.rentitem.core.di.AppContainerImpl
import com.rentitem.core.navigation.NavigationWrapper
import com.rentitem.ui.theme.RentiTemTheme

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        appContainer = AppContainerImpl(this)
        
        enableEdgeToEdge()
        setContent {
            RentiTemTheme {
                NavigationWrapper(appContainer)
            }
        }
    }
}
