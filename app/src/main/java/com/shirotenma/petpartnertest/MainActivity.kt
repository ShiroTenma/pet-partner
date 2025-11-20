// app/src/main/java/com/shirotenma/petpartnertest/MainActivity.kt
package com.shirotenma.petpartnertest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.shirotenma.petpartnertest.settings.SettingsStore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.hilt.navigation.compose.hiltViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsStore: SettingsStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsVm: com.shirotenma.petpartnertest.settings.SettingsViewModel = hiltViewModel()
            val settingsUi by settingsVm.uiState.collectAsState()

            val navController = rememberNavController()
            AppNavHost(nav = navController, ownerName = settingsUi.ownerName)
        }
    }
}
