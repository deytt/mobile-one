package com.mobileone.shared.config

/**
 * Catálogo das 3 marcas da POC (SPEC-004). Os tokens de [ThemeTokens] seguem a tabela de
 * referência extraída do Figma em `.cursor/rules/02-architecture.mdc` — fonte de verdade para
 * valores de código. `colorPrimaryVariant` não tem token próprio no Figma ainda; usamos o
 * mesmo valor de `colorPrimary` como placeholder até existir um tom de variante definido.
 *
 * Nesta fundação (SPEC-004) as marcas são factories Kotlin em memória, sem I/O de plataforma.
 * O carregamento a partir de JSON bundled (um arquivo por marca em `resources/white_label`,
 * conforme descrito na spec) fica para uma spec/PR futura, quando a tela de Brand Switcher
 * precisar de fato trocar de configuração em runtime a partir de um arquivo.
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
            colorPrimary = "#782D00",
            colorPrimaryVariant = "#782D00",
            colorSecondary = "#C9A84C",
            colorBackground = "#FAFAF8",
            colorSurface = "#FFFFFF",
            colorError = "#B91C1C",
            colorOnPrimary = "#FFFFFF",
            colorOnBackground = "#1A1A1A",
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
