package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.entity.PixKeyType
import com.mobileone.shared.domain.error.PixError
import com.mobileone.shared.domain.validation.CpfValidator
import com.mobileone.shared.domain.validation.CnpjValidator
import com.mobileone.shared.domain.validation.PhoneValidator
import com.mobileone.shared.domain.validation.EmailValidator
import com.mobileone.shared.domain.validation.RandomKeyValidator

/**
 * Valida uma chave PIX de acordo com seu tipo (SPEC-003).
 * Operação pura, sem IO — delegada aos validators específicos por tipo.
 */
class ValidatePixKeyUseCase {

    operator fun invoke(key: String, type: PixKeyType): Result<Unit> {
        val isValid = when (type) {
            is PixKeyType.CPF -> CpfValidator.validate(key)
            is PixKeyType.CNPJ -> CnpjValidator.validate(key)
            is PixKeyType.Phone -> PhoneValidator.validate(key)
            is PixKeyType.Email -> EmailValidator.validate(key)
            is PixKeyType.RandomKey -> RandomKeyValidator.validate(key)
            is PixKeyType.QRCode -> key.isNotBlank()  // validação feita pelo ParsePixQRCodeUseCase
        }
        return if (isValid) {
            Result.success(Unit)
        } else {
            Result.failure(PixError.InvalidKey(invalidReason(type)))
        }
    }

    private fun invalidReason(type: PixKeyType): String = when (type) {
        is PixKeyType.CPF -> "CPF inválido"
        is PixKeyType.CNPJ -> "CNPJ inválido"
        is PixKeyType.Phone -> "Telefone inválido — use o formato +55 (DDD) número"
        is PixKeyType.Email -> "E-mail inválido"
        is PixKeyType.RandomKey -> "Chave aleatória inválida — deve ser um UUID v4"
        is PixKeyType.QRCode -> "QR Code inválido"
    }
}
