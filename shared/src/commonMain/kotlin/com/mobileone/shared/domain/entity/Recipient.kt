package com.mobileone.shared.domain.entity

import com.mobileone.shared.domain.entity.PixKeyType

/**
 * Destinatário retornado pelo DICT do Banco Central (SPEC-003).
 * Dados suficientes para exibir a tela de confirmação e executar a transferência.
 */
data class Recipient(
    val name: String,
    val taxId: String,           // CPF/CNPJ do titular (não exibido diretamente — usar maskedKey)
    val institution: String,
    val pixKey: String,
    val pixKeyType: PixKeyType
)
