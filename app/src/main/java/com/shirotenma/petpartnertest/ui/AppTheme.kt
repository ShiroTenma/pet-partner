package com.shirotenma.petpartnertest.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun PetPartnerTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) darkColorScheme() else lightColorScheme()
    MaterialTheme(
        colorScheme = scheme,
        typography = Typography(),
        content = content
    )
}
