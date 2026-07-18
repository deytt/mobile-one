package com.mobileone.shared.domain.entity

/**
 * Tipos de chave PIX suportados pelo Banco Central (SPEC-003).
 * Definido no domínio para ser referenciado por entidades e feature state.
 */
sealed class PixKeyType {
    object CPF : PixKeyType()
    object CNPJ : PixKeyType()
    object Phone : PixKeyType()
    object Email : PixKeyType()
    object RandomKey : PixKeyType()
    object QRCode : PixKeyType()
}
