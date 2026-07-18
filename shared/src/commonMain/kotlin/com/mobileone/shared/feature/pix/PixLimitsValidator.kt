package com.mobileone.shared.feature.pix

import com.mobileone.shared.domain.error.PixError

/**
 * Regra de limite de transferência PIX (SPEC-003).
 * Limites definidos pelo Banco Central: R$ 20.000 diurno / R$ 1.000 noturno (21h–6h).
 */
class PixLimitsValidator {

    fun validate(amountCents: Long, hourOfDay: Int): Result<Unit> {
        val period = if (hourOfDay in 6..20) PixPeriod.DAY else PixPeriod.NIGHT
        val limit = when (period) {
            PixPeriod.DAY -> 20_000_00L    // R$ 20.000,00
            PixPeriod.NIGHT -> 1_000_00L   // R$  1.000,00
        }
        return if (amountCents > limit) {
            Result.failure(PixError.LimitExceeded(limit))
        } else {
            Result.success(Unit)
        }
    }
}

enum class PixPeriod { DAY, NIGHT }
