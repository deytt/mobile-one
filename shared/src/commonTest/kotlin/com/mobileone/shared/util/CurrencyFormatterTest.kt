package com.mobileone.shared.util

import kotlin.test.Test
import kotlin.test.assertEquals

class CurrencyFormatterTest {

    @Test
    fun deveFormatarCentavosPositivosComoCredito() {
        assertEquals("+ R$ 1.234,56", CurrencyFormatter.format(cents = 123456L, isDebit = false))
    }

    @Test
    fun deveFormatarCentavosComoDebito() {
        assertEquals("- R$ 50,00", CurrencyFormatter.format(cents = 5000L, isDebit = true))
    }

    @Test
    fun deveFormatarSaldoPositivo() {
        assertEquals("R$ 1.234,56", CurrencyFormatter.formatBalance(cents = 123456L))
    }

    @Test
    fun deveFormatarSaldoNegativo() {
        assertEquals("- R$ 50,00", CurrencyFormatter.formatBalance(cents = -5000L))
    }

    @Test
    fun deveRetornarBulletsQuandoSaldoOculto() {
        assertEquals("R$ ••••••", CurrencyFormatter.formatBalance(cents = 123456L, hidden = true))
    }

    @Test
    fun deveRetornarBulletsParaTransacaoOculta() {
        assertEquals("R$ ••••••", CurrencyFormatter.format(cents = 5000L, isDebit = true, hidden = true))
    }

    @Test
    fun deveFormatarValorZero() {
        assertEquals("R$ 0,00", CurrencyFormatter.formatBalance(cents = 0L))
    }

    @Test
    fun deveFormatarValorSemSeparadorMilharAbaixoDeMil() {
        assertEquals("R$ 9,99", CurrencyFormatter.formatBalance(cents = 999L))
    }

    @Test
    fun deveFormatarValorComDoisSeparadoresDeMilhar() {
        assertEquals("R$ 1.000.000,00", CurrencyFormatter.formatBalance(cents = 100_000_000L))
    }

    @Test
    fun deveFormatarEpochDayComoHoje() {
        val today = 20000
        assertEquals("Hoje", CurrencyFormatter.formatEpochDay(epochDay = today, todayEpochDay = today))
    }

    @Test
    fun deveFormatarEpochDayComoOntem() {
        val today = 20000
        assertEquals("Ontem", CurrencyFormatter.formatEpochDay(epochDay = today - 1, todayEpochDay = today))
    }

    @Test
    fun deveFormatarEpochDayComoData() {
        // epochDay 20000 = 2024-10-04 (aproximadamente)
        // Vamos usar um epochDay conhecido: 19570 = 2023-07-15
        val knownDay = 19557 // 2023-07-02
        val today = 20000
        val result = CurrencyFormatter.formatEpochDay(epochDay = knownDay, todayEpochDay = today)
        // Deve retornar algo como "2 jul."
        assertEquals(true, result.contains("jul"), "Esperado mês jul, mas obteve: $result")
    }
}
