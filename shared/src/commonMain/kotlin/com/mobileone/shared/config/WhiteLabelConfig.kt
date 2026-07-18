package com.mobileone.shared.config

/**
 * Configuração de marca (white-label) consumida pela UI nativa (Compose/SwiftUI) via
 * [com.mobileone.shared.config.AppStateRepository]. Ver SPEC-004 e ADR-004.
 */
data class WhiteLabelConfig(
    val brandId: String,
    val brandName: String,
    val logoAsset: String,
    val logoUrl: String?,
    val theme: ThemeTokens,
    val features: FeatureFlags,
    val supportContact: SupportContact,
    val onboarding: OnboardingConfig
)

/**
 * Tokens de tema em formato agnóstico de plataforma (cores em hex, radius em dp lógico).
 * A camada de presentation nativa (Compose/SwiftUI) é responsável por converter estes
 * valores para os tipos concretos de cada plataforma — nunca o inverso.
 */
data class ThemeTokens(
    val colorPrimary: String,
    val colorPrimaryVariant: String,
    val colorSecondary: String,
    val colorBackground: String,
    val colorSurface: String,
    val colorError: String,
    val colorOnPrimary: String,
    val colorOnBackground: String,
    /**
     * Texto secundário/muted sobre [colorSurface] (ex: subtítulos, hints) — token
     * `color/on-surface` documentado em `.cursor/rules/02-architecture.mdc`, adicionado na
     * SPEC-001 pois o Figma de Login/Biometria já o usa e ele não existia até então.
     */
    val colorOnSurface: String,
    val fontFamilyName: String,
    val borderRadiusDp: Int,
    val elevationEnabled: Boolean
)

data class FeatureFlags(
    val pixEnabled: Boolean = true,
    val scheduledPixEnabled: Boolean = true,
    val investmentsEnabled: Boolean = false,
    val creditCardEnabled: Boolean = true,
    val openFinanceEnabled: Boolean = true,
    val biometricLoginEnabled: Boolean = true,
    val virtualCardEnabled: Boolean = true,
    val cashbackEnabled: Boolean = false,
    val isVipClient: Boolean = false
)

data class SupportContact(
    val phone: String,
    val whatsappNumber: String?,
    val email: String,
    val chatEnabled: Boolean
)

data class OnboardingConfig(
    val welcomeTitle: String,
    val welcomeSubtitle: String,
    val backgroundAsset: String
)
