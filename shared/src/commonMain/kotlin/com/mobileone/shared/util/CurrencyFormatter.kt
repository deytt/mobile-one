package com.mobileone.shared.util

/**
 * Formatação de valores monetários em BRL sem depender de APIs de locale nativas (SPEC-002).
 * Lógica compartilhada entre Android e iOS — garante exibição idêntica em ambas as plataformas.
 *
 * Exemplos:
 * - `12345` centavos → `"R$ 123,45"`
 * - `-5000` centavos (débito) → `"- R$ 50,00"`
 * - `200000` centavos (crédito) → `"+ R$ 2.000,00"`
 * - Saldo oculto → `"R$ ••••••"`
 */
object CurrencyFormatter {

    private const val HIDDEN_VALUE = "R$ ••••••"

    /**
     * Formata centavos como valor em BRL.
     * Positivos ficam com prefixo `+ R$` (crédito), negativos com `- R$` (débito).
     * O valor absoluto é sempre usado — o sinal vem pelo prefixo.
     */
    fun format(cents: Long, isDebit: Boolean, hidden: Boolean = false): String {
        if (hidden) return HIDDEN_VALUE
        val prefix = if (isDebit) "- R$ " else "+ R$ "
        return prefix + formatAbsolute(kotlin.math.abs(cents))
    }

    /**
     * Formata centavos como saldo (sem prefixo de crédito/débito).
     * Usa [HIDDEN_VALUE] quando [hidden] é `true`.
     */
    fun formatBalance(cents: Long, hidden: Boolean = false): String {
        if (hidden) return HIDDEN_VALUE
        val prefix = if (cents < 0) "- R$ " else "R$ "
        return prefix + formatAbsolute(kotlin.math.abs(cents))
    }

    /** Formata centavos como valor absoluto sem prefixo, ex: `"1.234,56"`. */
    fun formatAbsolute(cents: Long): String {
        val wholePart = cents / 100L
        val decimalPart = cents % 100L
        val wholeFormatted = formatWithThousandsSeparator(wholePart)
        val decimalFormatted = decimalPart.toString().padStart(2, '0')
        return "$wholeFormatted,$decimalFormatted"
    }

    private fun formatWithThousandsSeparator(value: Long): String {
        val s = value.toString()
        if (s.length <= 3) return s
        val builder = StringBuilder()
        val offset = s.length % 3
        s.forEachIndexed { index, c ->
            if (index != 0 && (index - offset) % 3 == 0) builder.append('.')
            builder.append(c)
        }
        return builder.toString()
    }

    /**
     * Formata um [epochDay] (dias desde 1970-01-01) como string legível: "Hoje", "Ontem"
     * ou a data no formato "15 jul." — usando aritmética simples sem depender de
     * `java.time` ou APIs nativas de data.
     */
    fun formatEpochDay(epochDay: Int, todayEpochDay: Int): String = when (epochDay) {
        todayEpochDay -> "Hoje"
        todayEpochDay - 1 -> "Ontem"
        else -> {
            val (_, month, day) = epochDayToYmd(epochDay)
            "$day ${monthAbbr(month)}."
        }
    }

    private fun epochDayToYmd(epochDay: Int): Triple<Int, Int, Int> {
        var remaining = epochDay + 719468
        val era = (if (remaining >= 0) remaining else remaining - 146096) / 146097
        val doe = remaining - era * 146097
        val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
        val y = yoe + era * 400
        val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
        val mp = (5 * doy + 2) / 153
        val d = doy - (153 * mp + 2) / 5 + 1
        val m = if (mp < 10) mp + 3 else mp - 9
        val yr = if (m <= 2) y + 1 else y
        return Triple(yr, m, d)
    }

    private fun monthAbbr(month: Int): String = when (month) {
        1 -> "jan"; 2 -> "fev"; 3 -> "mar"; 4 -> "abr"
        5 -> "mai"; 6 -> "jun"; 7 -> "jul"; 8 -> "ago"
        9 -> "set"; 10 -> "out"; 11 -> "nov"; else -> "dez"
    }
}
