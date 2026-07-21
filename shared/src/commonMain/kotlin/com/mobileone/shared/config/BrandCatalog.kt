package com.mobileone.shared.config

/**
 * Catálogo inicial de marcas (SPEC-004 / SPEC-005). Os tokens de [ThemeTokens] seguem a
 * tabela da SPEC-005. `colorPrimaryVariant` reutiliza `colorPrimary` até existir um token
 * específico de variante.
 *
 * As marcas são factories Kotlin em memória, sem I/O de plataforma. O carregamento a partir
 * de JSON bundled ou configuração remota deve ser definido em spec incremental.
 */
object BrandCatalog {

    const val DEFAULT_BRAND_ID = "banco_principal"

    fun bancoPrincipal(): WhiteLabelConfig = WhiteLabelConfig(
        brandId = "banco_principal",
        brandName = "Banco Principal",
        logoAsset = "logo_banco_principal",
        logoUrl = null,
        theme = ThemeTokens(
            colorPrimary = "#003B6F",
            colorPrimaryVariant = "#003B6F",
            colorSecondary = "#F7941D",
            colorBackground = "#F5F7FA",
            colorSurface = "#FFFFFF",
            colorError = "#DC2626",
            colorOnPrimary = "#FFFFFF",
            colorOnBackground = "#1A1A2E",
            colorOnSurface = "#6B7280",
            fontFamilyName = "Roboto",
            borderRadiusDp = 12,
            elevationEnabled = true
        ),
        features = FeatureFlags(
            pixEnabled = true,
            scheduledPixEnabled = true,
            investmentsEnabled = true,
            creditCardEnabled = true,
            openFinanceEnabled = true,
            biometricLoginEnabled = true,
            virtualCardEnabled = true,
            cashbackEnabled = true,
            isVipClient = false
        ),
        supportContact = SupportContact(
            phone = "0800 001 0001",
            whatsappNumber = "+5511900000001",
            email = "atendimento@bancoprincipal.com.br",
            chatEnabled = true
        ),
        onboarding = OnboardingConfig(
            welcomeTitle = "Bem-vindo ao Banco Principal",
            welcomeSubtitle = "Sua conta completa, do jeito que você confia.",
            backgroundAsset = "onboarding_banco_principal"
        )
    )

    fun fintechVerde(): WhiteLabelConfig = WhiteLabelConfig(
        brandId = "fintech_verde",
        brandName = "Fintech Verde",
        logoAsset = "logo_fintech_verde",
        logoUrl = null,
        theme = ThemeTokens(
            colorPrimary = "#00A86B",
            colorPrimaryVariant = "#00A86B",
            colorSecondary = "#1A1A2E",
            colorBackground = "#F0FAF5",
            colorSurface = "#FFFFFF",
            colorError = "#EF4444",
            colorOnPrimary = "#FFFFFF",
            colorOnBackground = "#1A1A2E",
            colorOnSurface = "#6B7280",
            fontFamilyName = "Inter",
            borderRadiusDp = 16,
            elevationEnabled = false
        ),
        features = FeatureFlags(
            pixEnabled = true,
            scheduledPixEnabled = true,
            investmentsEnabled = false,
            creditCardEnabled = true,
            openFinanceEnabled = true,
            biometricLoginEnabled = true,
            virtualCardEnabled = true,
            cashbackEnabled = true,
            isVipClient = false
        ),
        supportContact = SupportContact(
            phone = "0800 002 0002",
            whatsappNumber = "+5511900000002",
            email = "ola@fintechverde.com.br",
            chatEnabled = true
        ),
        onboarding = OnboardingConfig(
            welcomeTitle = "Bem-vindo à Fintech Verde",
            welcomeSubtitle = "Simples, digital e sem burocracia.",
            backgroundAsset = "onboarding_fintech_verde"
        )
    )

    fun bancoPremium(): WhiteLabelConfig = WhiteLabelConfig(
        brandId = "banco_premium",
        brandName = "Banco Premium",
        logoAsset = "logo_banco_premium",
        logoUrl = null,
        theme = ThemeTokens(
            colorPrimary = "#7B2D00",
            colorPrimaryVariant = "#7B2D00",
            colorSecondary = "#C9A84C",
            colorBackground = "#FAFAF8",
            colorSurface = "#FFFFFF",
            colorError = "#B91C1C",
            colorOnPrimary = "#FFFFFF",
            colorOnBackground = "#1A1A1A",
            colorOnSurface = "#6B6B6B",
            fontFamilyName = "Georgia",
            borderRadiusDp = 4,
            elevationEnabled = true
        ),
        features = FeatureFlags(
            pixEnabled = true,
            scheduledPixEnabled = true,
            investmentsEnabled = true,
            creditCardEnabled = true,
            openFinanceEnabled = true,
            biometricLoginEnabled = true,
            virtualCardEnabled = true,
            cashbackEnabled = true,
            isVipClient = true
        ),
        supportContact = SupportContact(
            phone = "0800 003 0003",
            whatsappNumber = "+5511900000003",
            email = "concierge@bancopremium.com.br",
            chatEnabled = true
        ),
        onboarding = OnboardingConfig(
            welcomeTitle = "Bem-vindo ao Banco Premium",
            welcomeSubtitle = "Uma experiência exclusiva para você.",
            backgroundAsset = "onboarding_banco_premium"
        )
    )

    fun all(): List<WhiteLabelConfig> = listOf(bancoPrincipal(), fintechVerde(), bancoPremium())

    fun byId(brandId: String): WhiteLabelConfig? = all().firstOrNull { it.brandId == brandId }
}
