package com.shirotenma.petpartnertest

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

object Route {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PETS = "pets"
    const val PET_EDIT = "pet_edit"
    const val SETTINGS = "settings"
    const val RECORDS = "records"
    const val RECORD_EDIT = "record_edit"
}

@Composable
fun AppNavHost(nav: NavHostController, ownerName: String) {
    NavHost(navController = nav, startDestination = Route.SPLASH) {

        // Splash: cek token lalu arahkan ke HOME/LOGIN
        composable(Route.SPLASH) {
            SplashScreen { loggedIn ->
                nav.navigateSingleTop(if (loggedIn) Route.HOME else Route.LOGIN) {
                    popUpTo(Route.SPLASH) { inclusive = true }
                }
            }
        }

        // Login → Home
        composable(Route.LOGIN) {
            LoginScreen(
                onRegister = { nav.navigateSingleTop(Route.REGISTER) },
                onLoggedIn = {
                    nav.navigateSingleTop(Route.HOME) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Register (opsional)
        composable(Route.REGISTER) {
            RegisterScreen(
                onBack = { nav.popBackStack() },
                onRegistered = {
                    nav.navigate(Route.HOME) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Home (SATU KALI SAJA, dan kirim ownerName)
        composable(Route.HOME) {
            val gateVm: AuthViewModel = hiltViewModel()
            val token by gateVm.tokenState.collectAsState()
            var lastToken by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(token) {
                val wasLoggedIn = !lastToken.isNullOrBlank()
                val isLoggedOutNow = token.isNullOrBlank()
                if (wasLoggedIn && isLoggedOutNow) {
                    nav.navigateSingleTop(Route.LOGIN) {
                        popUpTo(Route.HOME) { inclusive = true }
                    }
                }
                lastToken = token
            }

            HomeScreen(nav = nav, ownerName = ownerName)
        }

        // List Pets
        composable(Route.PETS) {
            com.shirotenma.petpartnertest.pet.PetListScreen(nav = nav)
        }

        // Add Pet (tanpa argumen)
        composable(Route.PET_EDIT) {
            com.shirotenma.petpartnertest.pet.PetEditScreen(nav = nav, petId = null)
        }

        // Edit Pet (arg wajib Long, non-nullable)
        composable(
            route = "${Route.PET_EDIT}/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.LongType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments!!.getLong("petId")
            com.shirotenma.petpartnertest.pet.PetEditScreen(nav = nav, petId = petId)
        }

        // Settings
        composable(Route.SETTINGS) {
            com.shirotenma.petpartnertest.settings.SettingsScreen(nav = nav)
        }
// List records milik petId
        composable("${Route.RECORDS}/{petId}",
            arguments = listOf(navArgument("petId"){ type = NavType.LongType })
        ) { backStack ->
            val petId = backStack.arguments!!.getLong("petId")
            com.shirotenma.petpartnertest.pet.record.PetRecordListScreen(nav = nav, petId = petId)
        }

// Add record
        composable(
            route = "${Route.RECORD_EDIT}/{petId}",
            arguments = listOf(navArgument("petId"){ type = NavType.LongType })
        ) { backStack ->
            val petId = backStack.arguments!!.getLong("petId")
            com.shirotenma.petpartnertest.pet.record.PetRecordEditScreen(
                nav = nav,
                petId = petId,
                id = null       // ⬅️ dulu: recordId = null
            )
        }

// Edit record
        composable(
            route = "${Route.RECORD_EDIT}/{petId}/{recordId}",
            arguments = listOf(
                navArgument("petId"){ type = NavType.LongType },
                navArgument("recordId"){ type = NavType.LongType },
            )
        ) { backStack ->
            val petId = backStack.arguments!!.getLong("petId")
            val recordId = backStack.arguments!!.getLong("recordId")
            com.shirotenma.petpartnertest.pet.record.PetRecordEditScreen(
                nav = nav,
                petId = petId,
                id = recordId    // ⬅️ map ke parameter 'id'
            )
        }


    }
}

/** Helper: navigate singleTop + opsional builder */
private inline fun NavHostController.navigateSingleTop(
    route: String,
    crossinline builder: androidx.navigation.NavOptionsBuilder.() -> Unit = {}
) {
    this.navigate(route) {
        launchSingleTop = true
        builder()
    }
}
