package com.mobileone.android.ui.component

/**
 * Deriva as iniciais exibidas nos badges de marca/avatar (ex: "Banco Principal" → "BP",
 * "Heitor Bastos" → "HB") a partir das duas primeiras palavras do nome — mesmo algoritmo
 * usado nos mockups do Figma (Splash, Boas-vindas + Biometria).
 */
fun brandInitials(name: String): String =
    name.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")
