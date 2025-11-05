package com.shirotenma.petpartnertest

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

object Route {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
}

@Composable
fun AppNavHost(nav: NavHostController) {
    NavHost(navController = nav, startDestination = Route.SPLASH) {

        // Splash: tentukan start route berdasar token saat app dibuka
        composable(Route.SPLASH) {
            SplashScreen { loggedIn ->
                nav.navigate(if (loggedIn) Route.HOME else Route.LOGIN) {
                    popUpTo(Route.SPLASH) { inclusive = true }
                }
            }
        }

        // Login → ke Home setelah sukses
        composable(Route.LOGIN) {
            LoginScreen(
                onRegister = { nav.navigate(Route.REGISTER) },
                onLoggedIn = {
                    nav.navigate(Route.HOME) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Register (opsional)
        composable(Route.REGISTER) {
            RegisterScreen(onBack = { nav.popBackStack() })
        }

        // Home: redirect ke Login hanya jika token berubah dari non-null → null (logout)
        composable(Route.HOME) {
            val gateVm: AuthGateViewModel = hiltViewModel()
            val token by gateVm.tokenState.collectAsState()

            // Simpan token sebelumnya untuk membandingkan transisi
            var lastToken by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(token) {
                // Redirect hanya ketika sebelumnya ada token (sudah login),
                // lalu sekarang token menjadi null/blank (logout)
                val wasLoggedIn = !lastToken.isNullOrBlank()
                val isLoggedOutNow = token.isNullOrBlank()
                if (wasLoggedIn && isLoggedOutNow) {
                    nav.navigate(Route.LOGIN) {
                        popUpTo(Route.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                lastToken = token
            }

            HomeScreen() // tombol Logout di sini cukup memanggil vm.logout()
        }
    }
}
