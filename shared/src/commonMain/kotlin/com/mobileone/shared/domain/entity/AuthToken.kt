package com.mobileone.shared.domain.entity

/**
 * Sessão autenticada retornada pelo [com.mobileone.shared.domain.repository.AuthRepository].
 * Ver SPEC-001.
 */
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val userName: String,
    val expiresAtEpochMillis: Long
)
