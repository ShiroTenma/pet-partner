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
    const val SCAN = "scan"
    const val DIAG_RESULT = "diag_result"
    const val CHAT = "chat"
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

        composable("${Route.SCAN}/{petId}",
            arguments = listOf(navArgument("petId"){ type = NavType.LongType })
        ) { back ->
            val petId = back.arguments!!.getLong("petId")
            com.shirotenma.petpartnertest.diagnose.ScanScreen(nav = nav, petId = petId)
        }

        composable(
            route = "${Route.DIAG_RESULT}?petId={petId}&cond={cond}&sev={sev}&conf={conf}&tips={tips}&uri={uri}",
            arguments = listOf(
                navArgument("petId"){ type = NavType.LongType },
                navArgument("cond"){ type = NavType.StringType },
                navArgument("sev"){ type = NavType.StringType },
                navArgument("conf"){ type = NavType.StringType },
                navArgument("tips"){ type = NavType.StringType },
                navArgument("uri"){ type = NavType.StringType },
            )
        ) { back ->
            val petId   = back.arguments!!.getLong("petId")
            val cond    = back.arguments!!.getString("cond")!!
            val sev     = back.arguments!!.getString("sev")!!
            val confStr = back.arguments!!.getString("conf")!!
            val tipsStr = back.arguments!!.getString("tips")!!
            val uriStr  = back.arguments!!.getString("uri")!!

            com.shirotenma.petpartnertest.diagnose.DiagnoseResultScreen(
                nav = nav,
                petId = petId,
                condition = cond,
                severity = sev,
                confidence = confStr.toDoubleOrNull() ?: 0.0,
                tips = tipsStr.split("|;|").filter { it.isNotBlank() },
                photoUri = uriStr
            )
        }

        composable(
            route = "${Route.CHAT}?petId={petId}&cond={cond}&sev={sev}&conf={conf}&tips={tips}&uri={uri}",
            arguments = listOf(
                navArgument("petId"){ type = NavType.LongType; defaultValue = -1L },
                navArgument("cond"){ type = NavType.StringType; nullable = true },
                navArgument("sev"){ type = NavType.StringType; nullable = true },
                navArgument("conf"){ type = NavType.StringType; nullable = true },
                navArgument("tips"){ type = NavType.StringType; nullable = true },
                navArgument("uri"){ type = NavType.StringType; nullable = true },
            )
        ){ back ->
            val petId   = back.arguments?.getLong("petId") ?: -1L
            val cond    = back.arguments?.getString("cond")
            val sev     = back.arguments?.getString("sev")
            val confStr = back.arguments?.getString("conf")
            val tipsStr = back.arguments?.getString("tips")
            val uriStr  = back.arguments?.getString("uri")

            com.shirotenma.petpartnertest.chat.ChatScreen(
                nav = nav,
                petId = petId.takeIf { it >= 0 },
                cond = cond, sev = sev,
                confidence = confStr?.toDoubleOrNull(),
                tips = tipsStr?.split("|;|")?.filter { it.isNotBlank() } ?: emptyList(),
                photoUri = uriStr
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
