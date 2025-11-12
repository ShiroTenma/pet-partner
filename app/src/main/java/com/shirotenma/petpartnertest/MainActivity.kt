package com.shirotenma.petpartnertest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.shirotenma.petpartnertest.settings.SettingsViewModel
import com.shirotenma.petpartnertest.ui.PetPartnerTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Ambil dark mode & ownerName dari Settings (satu kali di root)
            val settingsVm: SettingsViewModel = hiltViewModel()
            val ui by settingsVm.uiState.collectAsState()

            PetPartnerTheme(darkTheme = ui.darkMode) {
                val nav = rememberNavController()
                // Kirim ownerName ke Home lewat Nav (lihat AppNavHost)
                AppNavHost(nav = nav, ownerName = ui.ownerName)
            }
        }
    }
}
