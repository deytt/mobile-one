package com.mobileone.shared.config

/**
 * Erros de domínio tipados para o carregamento de configuração white-label (ver
 * `.cursor/rules/04-kmp-conventions.mdc` — nunca expor `Exception` genérica).
 */
sealed class WhiteLabelConfigError {
    data class UnknownBrand(val brandId: String) : WhiteLabelConfigError()
}

private class UnknownBrandException(val error: WhiteLabelConfigError.UnknownBrand) :
    Exception("Marca desconhecida: ${error.brandId}")

/**
 * Fonte de dados de configuração white-label por `brandId`. Ver SPEC-004.
 */
interface WhiteLabelConfigRepository {
    suspend fun loadConfig(brandId: String): Result<WhiteLabelConfig>
}

/**
 * Implementação da fundação (SPEC-004): resolve a configuração a partir do [BrandCatalog],
 * mantido em memória. Ver TODO no [BrandCatalog] sobre a futura migração para JSON bundled.
 */
class InMemoryWhiteLabelConfigRepository : WhiteLabelConfigRepository {

    override suspend fun loadConfig(brandId: String): Result<WhiteLabelConfig> {
        val config = BrandCatalog.byId(brandId)
            ?: return Result.failure(UnknownBrandException(WhiteLabelConfigError.UnknownBrand(brandId)))
        return Result.success(config)
    }
}
