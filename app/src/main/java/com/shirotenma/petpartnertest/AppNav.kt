package com.shirotenma.petpartnertest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

object Route {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PETS = "pets"
    const val PET_EDIT = "pet_edit"          // add / edit base
}

@Composable
fun AppNavHost(nav: NavHostController) {
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
            RegisterScreen(onBack = { nav.popBackStack() })
        }

        // Home
        composable(Route.HOME) {
            HomeScreen(nav = nav) // pastikan tombol ke Pets: nav.navigate(Route.PETS)
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
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType } // non-nullable
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments!!.getLong("petId")
            com.shirotenma.petpartnertest.pet.PetEditScreen(nav = nav, petId = petId)
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
