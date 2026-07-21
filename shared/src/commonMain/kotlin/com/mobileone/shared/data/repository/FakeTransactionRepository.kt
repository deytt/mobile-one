package com.mobileone.shared.data.repository

import com.mobileone.shared.domain.entity.Transaction
import com.mobileone.shared.domain.entity.TransactionCategory
import com.mobileone.shared.domain.entity.TransactionPage
import com.mobileone.shared.domain.repository.TransactionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementação em memória de [TransactionRepository] para validar paginação da SPEC-002.
 */
class FakeTransactionRepository : TransactionRepository {

    override suspend fun getTransactions(
        accountId: String,
        page: Int,
        pageSize: Int
    ): Result<TransactionPage> {
        delay(600)
        val all = ALL_TRANSACTIONS
        val start = page * pageSize
        if (start >= all.size) {
            return Result.success(TransactionPage(items = emptyList(), hasMore = false, page = page))
        }
        val end = minOf(start + pageSize, all.size)
        val items = all.subList(start, end)
        return Result.success(
            TransactionPage(items = items, hasMore = end < all.size, page = page)
        )
    }

    override fun observeRecentTransactions(accountId: String, limit: Int): Flow<List<Transaction>> =
        flowOf(ALL_TRANSACTIONS.take(limit))

    companion object {
        // Base fixa para manter dados previsíveis em testes e previews.
        private const val TODAY_EPOCH_DAY = 20000

        val ALL_TRANSACTIONS: List<Transaction> = listOf(
            Transaction("tx01", "acc_001", 5000L, true, "iFood", TransactionCategory.PURCHASE, TODAY_EPOCH_DAY),
            Transaction("tx02", "acc_001", 200_00L, false, "Salário", TransactionCategory.DEPOSIT, TODAY_EPOCH_DAY),
            Transaction("tx03", "acc_001", 12_00L, true, "Spotify", TransactionCategory.PURCHASE, TODAY_EPOCH_DAY - 1),
            Transaction("tx04", "acc_001", 150_00L, true, "PIX enviado - João", TransactionCategory.PIX, TODAY_EPOCH_DAY - 1),
            Transaction("tx05", "acc_001", 75_50L, true, "Boleto Energia", TransactionCategory.BOLETO, TODAY_EPOCH_DAY - 2),
            Transaction("tx06", "acc_001", 30_00L, false, "PIX recebido - Maria", TransactionCategory.PIX, TODAY_EPOCH_DAY - 2),
            Transaction("tx07", "acc_001", 89_90L, true, "Amazon", TransactionCategory.PURCHASE, TODAY_EPOCH_DAY - 3),
            Transaction("tx08", "acc_001", 400_00L, true, "TED enviado", TransactionCategory.TED, TODAY_EPOCH_DAY - 3),
            Transaction("tx09", "acc_001", 25_00L, true, "Netflix", TransactionCategory.PURCHASE, TODAY_EPOCH_DAY - 4),
            Transaction("tx10", "acc_001", 19_90L, true, "Uber", TransactionCategory.PURCHASE, TODAY_EPOCH_DAY - 4),
            Transaction("tx11", "acc_001", 1_200_00L, false, "Freela", TransactionCategory.DEPOSIT, TODAY_EPOCH_DAY - 5),
            Transaction("tx12", "acc_001", 9_90L, true, "Apple Music", TransactionCategory.PURCHASE, TODAY_EPOCH_DAY - 5),
            Transaction("tx13", "acc_001", 60_00L, true, "Farmácia", TransactionCategory.PURCHASE, TODAY_EPOCH_DAY - 6),
            Transaction("tx14", "acc_001", 320_00L, true, "Cartão Crédito", TransactionCategory.CARD, TODAY_EPOCH_DAY - 6),
            Transaction("tx15", "acc_001", 50_00L, false, "Cashback", TransactionCategory.DEPOSIT, TODAY_EPOCH_DAY - 7),
            Transaction("tx16", "acc_001", 15_00L, true, "Estacionamento", TransactionCategory.PURCHASE, TODAY_EPOCH_DAY - 7),
            Transaction("tx17", "acc_001", 800_00L, false, "Reembolso", TransactionCategory.DEPOSIT, TODAY_EPOCH_DAY - 8),
            Transaction("tx18", "acc_001", 45_00L, true, "Lanche", TransactionCategory.PURCHASE, TODAY_EPOCH_DAY - 8),
            Transaction("tx19", "acc_001", 3_50L, true, "Taxa manutenção", TransactionCategory.FEE, TODAY_EPOCH_DAY - 9),
            Transaction("tx20", "acc_001", 250_00L, true, "Seguro Auto", TransactionCategory.BOLETO, TODAY_EPOCH_DAY - 9),
            Transaction("tx21", "acc_001", 100_00L, true, "Supermercado", TransactionCategory.PURCHASE, TODAY_EPOCH_DAY - 10),
            Transaction("tx22", "acc_001", 500_00L, false, "Transferência recebida", TransactionCategory.TED, TODAY_EPOCH_DAY - 10),
            Transaction("tx23", "acc_001", 22_00L, true, "Combustível", TransactionCategory.PURCHASE, TODAY_EPOCH_DAY - 11),
            Transaction("tx24", "acc_001", 130_00L, true, "Academia", TransactionCategory.PURCHASE, TODAY_EPOCH_DAY - 11),
            Transaction("tx25", "acc_001", 40_00L, true, "Restaurante", TransactionCategory.PURCHASE, TODAY_EPOCH_DAY - 12),
        )
    }
}
