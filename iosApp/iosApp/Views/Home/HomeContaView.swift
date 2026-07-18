import SwiftUI
import shared

/// Home de Conta (SPEC-010): saldo, quick actions, feature cards, transações e Bottom Tab Bar.
struct HomeContaView: View {
    var onNavigateToCartoes: () -> Void = {}
    let onBrandSwitcherTap: () -> Void
    var onPixTap: () -> Void = {}
    var onLogoutTap: () -> Void = {}
    @StateObject private var viewModel = HomeViewModel()

    var body: some View {
        HomeContaContent(
            uiState: viewModel.uiState,
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

struct HomeContaContent: View {
    let uiState: HomeUiState
    let onRefresh: () -> Void
    let onLoadMore: () -> Void
    let onToggleBalance: () -> Void
    let onDismissError: () -> Void
    var onNavigateToCartoes: () -> Void = {}
    let onBrandSwitcherTap: () -> Void
    var onPixTap: () -> Void = {}
    var onLogoutTap: () -> Void = {}

    @Environment(\.brandTheme) private var brandTheme

    private let creditGreen = Color(hex: "#22C55E")
    private var cornerRadius: CGFloat { brandTheme.cornerRadius }

    var body: some View {
        VStack(spacing: 0) {
            Group {
                if uiState.isLoading && uiState.account == nil {
                    loadingContent
                } else {
                    mainScroll
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
        .background(brandTheme.background)
        .toolbar(.hidden, for: .navigationBar)
        .toolbarColorScheme(.dark, for: .navigationBar)
        .alert(
            "Erro",
            isPresented: Binding(
                get: { uiState.errorMessage != nil },
                set: { _ in onDismissError() }
            )
        ) {
            Button("OK", action: onDismissError)
        } message: {
            Text(uiState.errorMessage ?? "")
        }
    }

    private var mainScroll: some View {
        ScrollView {
            VStack(spacing: 0) {
                heroBlock

                if uiState.isStale {
                    StaleBanner(lastUpdatedAt: 0)
                        .padding(.horizontal, 16)
                        .padding(.top, 8)
                }

                FeaturePromoCardView(
                    title: "PIX",
                    subtitle: "Transferências e pagamentos instantâneos, disponíveis 24h por dia",
                    systemImage: "bolt.fill",
                    theme: brandTheme,
                    action: onPixTap
                )
                .padding(.horizontal, 16)
                .padding(.top, 16)

                FeaturePromoCardView(
                    title: "Open Finance",
                    subtitle: "Conecte suas contas de outros bancos e tenha uma visão completa",
                    systemImage: "building.columns.fill",
                    theme: brandTheme,
                    action: {}
                )
                .padding(.horizontal, 16)
                .padding(.top, 16)

                transactionsSection

                Button(role: .destructive, action: onLogoutTap) {
                    Text("Sair")
                        .frame(maxWidth: .infinity)
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 16)
            }
        }
        .refreshable { onRefresh() }
        .background(brandTheme.background)
    }

    private var heroBlock: some View {
        VStack(spacing: 0) {
            HomeGreetingHeader(userName: uiState.account?.holderName ?? "Heitor Bastos")
            heroBalance
        }
        .background(brandTheme.primary.ignoresSafeArea(edges: .top))
        .clipShape(
            UnevenRoundedRectangle(
                topLeadingRadius: 0,
                bottomLeadingRadius: cornerRadius,
                bottomTrailingRadius: cornerRadius,
                topTrailingRadius: 0,
                style: .continuous
            )
        )
    }

    private var heroBalance: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("Saldo disponível")
                .font(brandTheme.font(size: 13))
                .foregroundStyle(Color.white.opacity(0.70))

            HStack(spacing: 8) {
                Text(uiState.account?.balanceFormatted ?? "R$ ••••••")
                    .font(brandTheme.font(size: 28, weight: .bold))
                    .tracking(-0.56)
                    .foregroundStyle(.white)

                Button(action: onToggleBalance) {
                    Image(systemName: uiState.isBalanceHidden ? "eye" : "eye.slash")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 20, height: 20)
                        .foregroundStyle(Color.white.opacity(0.80))
                }
                .buttonStyle(.plain)
                .accessibilityLabel(uiState.isBalanceHidden ? "Mostrar saldo" : "Ocultar saldo")
            }
            .padding(.top, 4)

            HStack(spacing: 24) {
                ContaQuickActionView(label: "Pagar", systemImage: "arrow.up.right", action: {})
                ContaQuickActionView(label: "Extrato", systemImage: "doc.text", action: {})
                ContaQuickActionView(label: "PIX", systemImage: "qrcode", action: onPixTap)
            }
            .frame(maxWidth: .infinity)
            .padding(.top, 20)
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 20)
    }

    private var transactionsSection: some View {
        VStack(spacing: 0) {
            HStack {
                Text("Transações recentes")
                    .font(brandTheme.font(size: 15, weight: .semibold))
                    .foregroundStyle(brandTheme.onBackground)
                Spacer()
                Button("Ver todas") {}
                    .font(brandTheme.font(size: 13, weight: .medium))
                    .foregroundStyle(brandTheme.primary)
                    .buttonStyle(.plain)
            }
            .padding(.horizontal, 16)
            .padding(.top, 20)

            VStack(spacing: 0) {
                ForEach(Array(uiState.transactions.enumerated()), id: \.element.id) { index, tx in
                    RecentTransactionRowView(
                        transaction: tx,
                        creditGreen: creditGreen,
                        brandTheme: brandTheme
                    )
                    .onAppear {
                        if tx.id == uiState.transactions.last?.id {
                            onLoadMore()
                        }
                    }
                    if index < uiState.transactions.count - 1 {
                        Divider().background(brandTheme.background)
                    }
                }

                if uiState.isLoadingMoreTransactions {
                    ProgressView()
                        .frame(maxWidth: .infinity)
                        .padding()
                }
            }
            .background(brandTheme.surface)
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
            .padding(.horizontal, 16)
            .padding(.top, 8)
        }
    }

    private var loadingContent: some View {
        ScrollView {
            VStack(spacing: 16) {
                BalanceSkeleton()
                BalanceSkeleton()
            }
            .padding()
        }
    }
}

private struct ContaQuickActionView: View {
    let label: String
    let systemImage: String
    let action: () -> Void
    @Environment(\.brandTheme) private var brandTheme

    var body: some View {
        Button(action: action) {
            VStack(spacing: 6) {
                ZStack {
                    Circle()
                        .fill(Color.white.opacity(0.15))
                        .frame(width: 48, height: 48)
                    Image(systemName: systemImage)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 20, height: 20)
                        .foregroundStyle(.white)
                }
                Text(label)
                    .font(brandTheme.font(size: 12, weight: .medium))
                    .foregroundStyle(Color.white.opacity(0.90))
            }
        }
        .buttonStyle(.plain)
    }
}

private struct FeaturePromoCardView: View {
    let title: String
    let subtitle: String
    let systemImage: String
    let theme: BrandTheme
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                ZStack {
                    Circle()
                        .fill(theme.background)
                        .frame(width: 40, height: 40)
                    Image(systemName: systemImage)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 20, height: 20)
                        .foregroundStyle(theme.primary)
                }

                VStack(alignment: .leading, spacing: 2) {
                    Text(title)
                        .font(theme.font(size: 15, weight: .semibold))
                        .foregroundStyle(theme.onBackground)
                    Text(subtitle)
                        .font(theme.font(size: 12))
                        .foregroundStyle(theme.onSurface)
                        .lineLimit(2)
                        .multilineTextAlignment(.leading)
                }

                Spacer(minLength: 8)

                Image(systemName: "chevron.right")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 16, height: 16)
                    .foregroundStyle(theme.onSurface)
            }
            .padding(16)
            .background(theme.surface)
            .clipShape(RoundedRectangle(cornerRadius: theme.cornerRadius, style: .continuous))
            .shadow(color: .black.opacity(0.08), radius: 3, x: 0, y: 1)
        }
        .buttonStyle(.plain)
    }
}

private struct RecentTransactionRowView: View {
    let transaction: TransactionDisplay
    let creditGreen: Color
    let brandTheme: BrandTheme

    var body: some View {
        HStack(spacing: 12) {
            ZStack {
                Circle()
                    .fill(brandTheme.background)
                    .frame(width: 40, height: 40)
                Image(systemName: transaction.category.contaSystemImage)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 20, height: 20)
                    .foregroundStyle(brandTheme.onSurface)
            }

            VStack(alignment: .leading, spacing: 2) {
                Text(transaction.description)
                    .font(brandTheme.font(size: 14, weight: .semibold))
                    .foregroundStyle(brandTheme.onBackground)
                    .lineLimit(1)
                Text("\(transaction.dateFormatted) · \(transaction.category.contaLabel)")
                    .font(brandTheme.font(size: 12))
                    .foregroundStyle(brandTheme.onSurface)
            }

            Spacer(minLength: 8)

            Text(transaction.displayAmount)
                .font(brandTheme.font(size: 14, weight: .semibold))
                .foregroundStyle(transaction.isDebit ? brandTheme.onBackground : creditGreen)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }
}

private extension TransactionDisplay {
    /// Débito sem prefixo "-"; crédito no formato Figma "+R$ …".
    var displayAmount: String {
        if isDebit {
            if amountFormatted.hasPrefix("- ") {
                return String(amountFormatted.dropFirst(2))
            }
            return amountFormatted
        }
        var value = amountFormatted
        if value.hasPrefix("+ ") { value = String(value.dropFirst(2)) }
        else if value.hasPrefix("+") { value = String(value.dropFirst()) }
        value = value.trimmingCharacters(in: .whitespaces)
        return value.hasPrefix("R$") ? "+\(value)" : "+R$ \(value)"
    }
}

private extension TransactionCategory {
    var contaSystemImage: String {
        switch self {
        case .pix: return "bolt.fill"
        case .ted: return "arrow.2.circlepath"
        case .boleto: return "doc.text"
        case .card: return "creditcard"
        case .purchase: return "bag"
        case .deposit: return "dollarsign"
        case .fee: return "doc.text"
        default: return "cart"
        }
    }

    var contaLabel: String {
        switch self {
        case .pix, .ted: return "Transferência"
        case .boleto: return "Pagamento"
        case .card: return "Cartão"
        case .purchase: return "Compras"
        case .deposit: return "Depósito"
        case .fee: return "Taxa"
        default: return "Outros"
        }
    }
}

#Preview("Banco Principal") {
    HomeContaContent(
        uiState: HomeUiState(
            isLoading: false,
            account: AccountDisplay(
                holderName: "Heitor Bastos",
                maskedNumber: "•••• 4521",
                balanceFormatted: "R$ 3.547,80",
                availableLimitFormatted: "R$ 5.000,00"
            ),
            transactions: [
                TransactionDisplay(id: "1", description: "Netflix", amountFormatted: "- R$ 45,90", isDebit: true, dateFormatted: "15 Jul", category: .purchase),
                TransactionDisplay(id: "2", description: "PIX — João Silva", amountFormatted: "+ R$ 500,00", isDebit: false, dateFormatted: "13 Jul", category: .pix)
            ]
        ),
        onRefresh: {},
        onLoadMore: {},
        onToggleBalance: {},
        onDismissError: {},
        onBrandSwitcherTap: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
    .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.bancoPrincipal()))
}
