import Foundation
import shared

struct HomeUiState {
    var isLoading = true
    var isRefreshing = false
    var account: AccountDisplay?
    var isBalanceHidden = false
    var transactions: [TransactionDisplay] = []
    var hasMoreTransactions = true
    var isLoadingMoreTransactions = false
    var isStale = false
    var errorMessage: String?
}

struct AccountDisplay {
    let holderName: String
    let maskedNumber: String
    let balanceFormatted: String
    let availableLimitFormatted: String
}

struct TransactionDisplay: Identifiable {
    let id: String
    let description: String
    let amountFormatted: String
    let isDebit: Bool
    let dateFormatted: String
    let category: TransactionCategory
}

struct TransactionSection: Identifiable {
    var id: String { date }
    let date: String
    let transactions: [TransactionDisplay]
}

@MainActor
final class HomeViewModel: ObservableObject {
    @Published private(set) var uiState = HomeUiState()

    private let accountId = FakeAccountRepository.companion.DEMO_ACCOUNT_ID
    private var accountCanceller: FlowCanceller?
    private var balanceCanceller: FlowCanceller?
    private var currentPage = 0
    // Account_ = mapeamento Kotlin/Native da data class Account (underscore evita conflito)
    private var latestAccount: Account_?
    private var isHidden = false

    init() {
        startObservations()
        loadNextPage()
    }

    deinit {
        accountCanceller?.cancel()
        balanceCanceller?.cancel()
    }

    private func startObservations() {
        // Parâmetros de callback Kotlin → Swift chegam como KotlinBoolean (boxed); usar .boolValue
        balanceCanceller = IOSDependencyProvider.shared.watchBalanceHidden { [weak self] hidden in
            guard let self else { return }
            self.isHidden = hidden.boolValue
            self.uiState.isBalanceHidden = hidden.boolValue
            if let account = self.latestAccount {
                self.uiState.account = account.toDisplay(isHidden: hidden.boolValue)
            }
        }

        accountCanceller = IOSDependencyProvider.shared.watchAccount { [weak self] account in
            guard let self else { return }
            self.latestAccount = account
            self.uiState.isStale = false  // updatedAt = 0 no Fake → sempre Fresh
            self.uiState.account = account.toDisplay(isHidden: self.isHidden)
            self.uiState.isLoading = false
        }
    }

    func onRefresh() {
        uiState.isRefreshing = true
        currentPage = 0
        Task {
            // suspend fun → async throws em Kotlin/Native; retorno Boolean vira KotlinBoolean
            let raw = try? await IOSDependencyProvider.shared.refreshAccountData(accountId: accountId)
            if raw?.boolValue == true {
                uiState.transactions = []
                uiState.hasMoreTransactions = true
                uiState.isRefreshing = false
                await loadNextPageAsync()
            } else {
                uiState.isRefreshing = false
                uiState.errorMessage = "Falha ao atualizar. Tente novamente."
            }
        }
    }

    func onLoadMore() {
        guard !uiState.isLoadingMoreTransactions, uiState.hasMoreTransactions else { return }
        loadNextPage()
    }

    func onToggleBalance() {
        Task { try? await IOSDependencyProvider.shared.toggleBalanceVisibility() }
    }

    func onDismissError() {
        uiState.errorMessage = nil
    }

    private func loadNextPage() {
        Task { await loadNextPageAsync() }
    }

    private func loadNextPageAsync() async {
        uiState.isLoadingMoreTransactions = true
        let todayEpochDay = Int32(Date().timeIntervalSince1970 / 86400)
        let outcome = try? await IOSDependencyProvider.shared.getTransactions(
            accountId: accountId,
            page: Int32(currentPage),
            pageSize: 20
        )
        guard let outcome else {
            uiState.isLoadingMoreTransactions = false
            uiState.errorMessage = "Erro ao carregar transações."
            return
        }
        if let page = outcome.page {
            // Propriedades de data class (isDebit, hasMore) chegam como Bool nativo — sem .boolValue
            let newItems = page.items.map { tx in
                TransactionDisplay(
                    id: tx.id,
                    description: tx.description_,
                    // Default params Kotlin não são propagados ao Swift — passar hidden: explicitamente
                    amountFormatted: CurrencyFormatter.shared.format(
                        cents: tx.amountCents,
                        isDebit: tx.isDebit,
                        hidden: false
                    ),
                    isDebit: tx.isDebit,
                    dateFormatted: CurrencyFormatter.shared.formatEpochDay(
                        epochDay: tx.epochDay,
                        todayEpochDay: todayEpochDay
                    ),
                    category: tx.category
                )
            }
            uiState.transactions += newItems
            uiState.hasMoreTransactions = page.hasMore
            uiState.isLoadingMoreTransactions = false
            if page.hasMore { currentPage += 1 }
        } else {
            uiState.isLoadingMoreTransactions = false
            uiState.errorMessage = outcome.errorMessage
        }
    }

    var transactionSections: [TransactionSection] {
        let grouped = Dictionary(grouping: uiState.transactions, by: \.dateFormatted)
        let orderedKeys = uiState.transactions.map(\.dateFormatted).reduce(into: [String]()) {
            if !$0.contains($1) { $0.append($1) }
        }
        return orderedKeys.compactMap { date in
            guard let txs = grouped[date] else { return nil }
            return TransactionSection(date: date, transactions: txs)
        }
    }
}

// Account_ = mapeamento Kotlin/Native da data class Account (underscore evita conflito de nome)
private extension Account_ {
    func toDisplay(isHidden: Bool) -> AccountDisplay {
        AccountDisplay(
            holderName: holderName,
            maskedNumber: maskedNumber,
            // Default params Kotlin não propagados → hidden: obrigatório
            balanceFormatted: CurrencyFormatter.shared.formatBalance(cents: balanceCents, hidden: isHidden),
            availableLimitFormatted: CurrencyFormatter.shared.formatBalance(cents: availableLimitCents, hidden: isHidden)
        )
    }
}
