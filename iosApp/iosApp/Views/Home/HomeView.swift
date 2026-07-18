import SwiftUI
import shared

/**
 * Tela Home stateful (SPEC-002): consome [HomeViewModel] e delega a renderização ao
 * [HomeContent] stateless — segue `.cursor/rules/06-ios-conventions.mdc`.
 */
struct HomeView: View {
    var onNavigateToCartoes: () -> Void = {}
    let onBrandSwitcherTap: () -> Void
    var onPixTap: () -> Void = {}
    let onLogoutTap: () -> Void
    @StateObject private var viewModel = HomeViewModel()
    @Environment(\.whiteLabelConfig) private var config

    var body: some View {
        HomeContent(
            uiState: viewModel.uiState,
            sections: viewModel.transactionSections,
            config: config,
            onRefresh: viewModel.onRefresh,
            onLoadMore: viewModel.onLoadMore,
            onToggleBalance: viewModel.onToggleBalance,
            onDismissError: viewModel.onDismissError,
            onNavigateToCartoes: onNavigateToCartoes,
            onBrandSwitcherTap: onBrandSwitcherTap,
            onPixTap: onPixTap,
            onLogoutTap: onLogoutTap
        )
    }
}

struct HomeContent: View {
    let uiState: HomeUiState
    let sections: [TransactionSection]
    let config: WhiteLabelConfig
    let onRefresh: () -> Void
    let onLoadMore: () -> Void
    let onToggleBalance: () -> Void
    let onDismissError: () -> Void
    var onNavigateToCartoes: () -> Void = {}
    let onBrandSwitcherTap: () -> Void
    var onPixTap: () -> Void = {}
    let onLogoutTap: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            Group {
                if uiState.isLoading && uiState.account == nil {
                    loadingContent
                } else {
                    mainList
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            HomeTabBar(
                currentTab: .conta,
                onTabChange: { tab in
                    if tab == .cartoes { onNavigateToCartoes() }
                },
                onBrandSwitcher: onBrandSwitcherTap
            )
        }
        .navigationTitle(config.brandName)
        .navigationBarTitleDisplayMode(.inline)
        .toolbarBackground(config.primaryColor, for: .navigationBar)
        .toolbarColorScheme(.dark, for: .navigationBar)
        .alert(
            "Erro",
            isPresented: Binding(get: { uiState.errorMessage != nil }, set: { _ in onDismissError() })
        ) {
            Button("OK", action: onDismissError)
        } message: {
            Text(uiState.errorMessage ?? "")
        }
    }

    private var mainList: some View {
        List {
            // Stale banner
            if uiState.isStale {
                Section {
                    StaleBanner(lastUpdatedAt: 0)
                        .listRowInsets(EdgeInsets())
                }
                .listRowBackground(Color.clear)
            }

            // Card de conta
            Section {
                if let account = uiState.account {
                    AccountCard(
                        account: account,
                        isBalanceHidden: uiState.isBalanceHidden,
                        onToggleBalance: onToggleBalance
                    )
                    .listRowInsets(EdgeInsets())
                    .listRowBackground(Color.clear)
                } else {
                    BalanceSkeleton()
                        .listRowBackground(Color.clear)
                }
            }

            // Ações rápidas
            Section {
                QuickActionsGrid(features: config.features, onPixTap: onPixTap)
                    .listRowBackground(Color.clear)
                    .listRowInsets(EdgeInsets())
            }

            // Extrato
            Section(header: Text("Extrato").font(.headline).foregroundStyle(.primary)) {
                ForEach(sections) { section in
                    Section(header: Text(section.date).font(.caption).foregroundStyle(.secondary)) {
                        ForEach(section.transactions) { tx in
                            TransactionRow(transaction: tx)
                                .onAppear {
                                    if tx.id == sections.last?.transactions.last?.id {
                                        onLoadMore()
                                    }
                                }
                        }
                    }
                }

                if uiState.isLoadingMoreTransactions {
                    HStack {
                        Spacer()
                        ProgressView()
                        Spacer()
                    }
                    .padding()
                }
            }

            // Logout (dev)
            Section {
                Button(role: .destructive, action: onLogoutTap) {
                    HStack {
                        Spacer()
                        Text("Sair")
                        Spacer()
                    }
                }
            }
        }
        .listStyle(.insetGrouped)
        .refreshable { onRefresh() }
        .background(config.backgroundColor)
    }

    private var loadingContent: some View {
        ScrollView {
            VStack(spacing: 16) {
                BalanceSkeleton()
                BalanceSkeleton()
                BalanceSkeleton()
            }
            .padding()
        }
    }
}

#Preview {
    HomeContent(
        uiState: HomeUiState(
            isLoading: false,
            account: AccountDisplay(
                holderName: "Heitor Bastos",
                maskedNumber: "•••• 4521",
                balanceFormatted: "R$ 1.234,56",
                availableLimitFormatted: "R$ 5.000,00"
            ),
            transactions: [
                TransactionDisplay(id: "1", description: "PIX - João", amountFormatted: "- R$ 150,00", isDebit: true, dateFormatted: "Hoje", category: .pix),
                TransactionDisplay(id: "2", description: "Salário", amountFormatted: "+ R$ 2.000,00", isDebit: false, dateFormatted: "Hoje", category: .deposit)
            ]
        ),
        sections: [
            TransactionSection(date: "Hoje", transactions: [
                TransactionDisplay(id: "1", description: "PIX - João", amountFormatted: "- R$ 150,00", isDebit: true, dateFormatted: "Hoje", category: .pix),
                TransactionDisplay(id: "2", description: "Salário", amountFormatted: "+ R$ 2.000,00", isDebit: false, dateFormatted: "Hoje", category: .deposit)
            ])
        ],
        config: BrandCatalog.shared.bancoPrincipal(),
        onRefresh: {},
        onLoadMore: {},
        onToggleBalance: {},
        onDismissError: {},
        onBrandSwitcherTap: {},
        onLogoutTap: {}
    )
        .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
        .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.bancoPrincipal()))
}
