package com.shirotenma.petpartnertest

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onDone: (loggedIn: Boolean) -> Unit) {
    val vm: AuthGateViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val token by vm.tokenState.collectAsState()

    // trigger animasi
    var start by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (start) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "splashAlpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (start) 1f else 0.94f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "splashScale"
    )

    LaunchedEffect(Unit) {
        start = true                 // mulai animasi
        delay(1200)                  // tampilkan splash sebentar
        onDone(!token.isNullOrBlank())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Jika punya logo: ganti ic_launcher_foreground dengan drawable kamu
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // ← bukan mipmap
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Splash",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp, fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
