import SwiftUI
import shared

/// Home de Cartões (SPEC-009): fatura, limite, compras e Bottom Tab Bar.
struct HomeCartoesView: View {
    let onNavigateToConta: () -> Void
    let onBrandSwitcherTap: () -> Void
    var userName: String = "Heitor Bastos"

    var body: some View {
        HomeCartoesContent(
            userName: userName,
            onTabChange: { tab in
                if tab == .conta { onNavigateToConta() }
            },
            onBrandSwitcherTap: onBrandSwitcherTap
        )
    }
}

struct HomeCartoesContent: View {
    let userName: String
    let onTabChange: (HomeTab) -> Void
    let onBrandSwitcherTap: () -> Void

    @Environment(\.brandTheme) private var brandTheme
    @State private var topSafeArea: CGFloat = 0

    private var cornerRadius: CGFloat { brandTheme.cornerRadius }

    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(spacing: 0) {
                    headerBlock
                    actionButtons
                    limitSection
                    purchasesSection
                    Spacer().frame(height: 16)
                }
            }
            // Conteúdo sobe sob a status bar; o header (com padding do inset) rola junto.
            .ignoresSafeArea(edges: .top)
            .background(brandTheme.background)

            HomeTabBar(
                currentTab: .cartoes,
                onTabChange: onTabChange,
                onBrandSwitcher: onBrandSwitcherTap
            )
        }
        .background(brandTheme.background)
        .readTopSafeArea($topSafeArea)
        .toolbar(.hidden, for: .navigationBar)
        .toolbarColorScheme(.dark, for: .navigationBar)
    }

    private var headerBlock: some View {
        VStack(spacing: 0) {
            HomeGreetingHeader(userName: userName)
            invoiceCard
                .padding(.horizontal, 16)
            Spacer().frame(height: 20)
        }
        .homeScrollingBrandHeader(
            color: brandTheme.primary,
            topSafeArea: topSafeArea
        )
    }

    private var invoiceCard: some View {
        HStack(alignment: .bottom) {
            VStack(alignment: .leading, spacing: 0) {
                Text("Fatura aberta")
                    .font(brandTheme.font(size: 12))
                    .tracking(0.3)
                    .foregroundStyle(Color.white.opacity(0.70))

                Text("R$ 487,40")
                    .font(brandTheme.font(size: 28, weight: .bold))
                    .tracking(-0.56)
                    .foregroundStyle(.white)
                    .padding(.top, 8)

                (Text("Vencimento ") + Text("25 JUL").bold())
                    .font(brandTheme.font(size: 13))
                    .foregroundStyle(Color.white.opacity(0.80))
                    .padding(.top, 4)

                (Text("Melhor dia de compra ") + Text("20 JUL").bold())
                    .font(brandTheme.font(size: 13))
                    .foregroundStyle(Color.white.opacity(0.80))
                    .padding(.top, 2)
            }

            Spacer(minLength: 8)

            Image(systemName: "chevron.right")
                .resizable()
                .scaledToFit()
                .frame(width: 16, height: 16)
                .foregroundStyle(Color.white.opacity(0.60))
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.white.opacity(0.10))
        .clipShape(RoundedRectangle(cornerRadius: cornerRadius, style: .continuous))
    }

    private var actionButtons: some View {
        HStack(spacing: 12) {
            Button(action: {}) {
                HStack(spacing: 8) {
                    Image(systemName: "creditcard.fill")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 18, height: 18)
                    Text("Pagar fatura")
                        .font(brandTheme.font(size: 14, weight: .semibold))
                }
                .foregroundStyle(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 44)
                .background(brandTheme.primary)
                .clipShape(RoundedRectangle(cornerRadius: cornerRadius, style: .continuous))
            }
            .buttonStyle(.plain)

            Button(action: {}) {
                HStack(spacing: 8) {
                    Image(systemName: "rectangle.stack.fill")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 18, height: 18)
                    Text("Meus cartões")
                        .font(brandTheme.font(size: 14, weight: .semibold))
                }
                .foregroundStyle(brandTheme.primary)
                .frame(maxWidth: .infinity)
                .frame(height: 44)
                .overlay(
                    RoundedRectangle(cornerRadius: cornerRadius, style: .continuous)
                        .stroke(brandTheme.primary, lineWidth: 1)
                )
            }
            .buttonStyle(.plain)
        }
        .padding(.horizontal, 16)
        .padding(.top, 16)
    }

    private var limitSection: some View {
        let used: CGFloat = 750
        let total: CGFloat = 2750
        let progress = used / total

        return VStack(alignment: .leading, spacing: 0) {
            HStack {
                Text("Meu limite")
                    .font(brandTheme.font(size: 15, weight: .semibold))
                    .foregroundStyle(brandTheme.onBackground)
                Spacer()
                Image(systemName: "chevron.right")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 16, height: 16)
                    .foregroundStyle(brandTheme.onSurface)
            }

            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Utilizado")
                        .font(brandTheme.font(size: 12))
                        .foregroundStyle(brandTheme.onSurface)
                    Text("R$ 750,00")
                        .font(brandTheme.font(size: 14, weight: .bold))
                        .foregroundStyle(brandTheme.onBackground)
                }
                Spacer()
                VStack(alignment: .trailing, spacing: 2) {
                    Text("Disponível")
                        .font(brandTheme.font(size: 12))
                        .foregroundStyle(brandTheme.onSurface)
                    Text("R$ 2.000,00")
                        .font(brandTheme.font(size: 14, weight: .bold))
                        .foregroundStyle(brandTheme.onBackground)
                }
            }
            .padding(.top, 12)

            GeometryReader { geo in
                ZStack(alignment: .leading) {
                    Capsule()
                        .fill(brandTheme.onSurface.opacity(0.20))
                        .frame(height: 4)
                    Capsule()
                        .fill(brandTheme.primary)
                        .frame(width: geo.size.width * progress, height: 4)
                }
            }
            .frame(height: 4)
            .padding(.top, 8)

            Text("Limite total: R$ 2.750,00")
                .font(brandTheme.font(size: 12))
                .foregroundStyle(brandTheme.onSurface)
                .frame(maxWidth: .infinity, alignment: .trailing)
                .padding(.top, 6)
        }
        .padding(16)
        .background(brandTheme.surface)
        .clipShape(RoundedRectangle(cornerRadius: cornerRadius, style: .continuous))
        .padding(.horizontal, 16)
        .padding(.top, 16)
    }

    private var purchasesSection: some View {
        VStack(spacing: 0) {
            HStack {
                Text("Minhas compras")
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
                ForEach(Array(Self.mockPurchases.enumerated()), id: \.element.id) { index, item in
                    PurchaseRowView(item: item, brandTheme: brandTheme)
                    if index < Self.mockPurchases.count - 1 {
                        Divider()
                            .background(brandTheme.background)
                    }
                }
            }
            .background(brandTheme.surface)
            .clipShape(RoundedRectangle(cornerRadius: cornerRadius, style: .continuous))
            .padding(.horizontal, 16)
            .padding(.top, 12)
        }
    }

    private static let mockPurchases: [PurchaseItem] = [
        PurchaseItem(id: "1", name: "Amazon", subtitle: "15 Jul · Compras online", amount: "R$ 189,90", systemImage: "bag.fill", badge: "1/3"),
        PurchaseItem(id: "2", name: "iFood", subtitle: "14 Jul · Alimentação", amount: "R$ 67,50", systemImage: "fork.knife", badge: nil),
        PurchaseItem(id: "3", name: "Posto Shell", subtitle: "13 Jul · Transporte", amount: "R$ 150,00", systemImage: "fuelpump.fill", badge: nil),
        PurchaseItem(id: "4", name: "Cinemark", subtitle: "12 Jul · Entretenimento", amount: "R$ 80,00", systemImage: "film", badge: nil)
    ]
}

private struct PurchaseItem: Identifiable {
    let id: String
    let name: String
    let subtitle: String
    let amount: String
    let systemImage: String
    let badge: String?
}

private struct PurchaseRowView: View {
    let item: PurchaseItem
    let brandTheme: BrandTheme

    var body: some View {
        HStack(spacing: 12) {
            ZStack {
                Circle()
                    .fill(brandTheme.background)
                    .frame(width: 40, height: 40)
                Image(systemName: item.systemImage)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 20, height: 20)
                    .foregroundStyle(brandTheme.onSurface)
            }

            VStack(alignment: .leading, spacing: 2) {
                Text(item.name)
                    .font(brandTheme.font(size: 14, weight: .semibold))
                    .foregroundStyle(brandTheme.onBackground)
                Text(item.subtitle)
                    .font(brandTheme.font(size: 12))
                    .foregroundStyle(brandTheme.onSurface)
                if let badge = item.badge {
                    Text(badge)
                        .font(brandTheme.font(size: 10, weight: .bold))
                        .foregroundStyle(.white)
                        .padding(.horizontal, 6)
                        .frame(height: 14)
                        .background(brandTheme.secondary)
                        .clipShape(RoundedRectangle(cornerRadius: 4, style: .continuous))
                        .padding(.top, 2)
                }
            }

            Spacer(minLength: 8)

            Text(item.amount)
                .font(brandTheme.font(size: 14, weight: .semibold))
                .foregroundStyle(brandTheme.onBackground)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }
}

#Preview {
    HomeCartoesContent(
        userName: "Heitor Bastos",
        onTabChange: { _ in },
        onBrandSwitcherTap: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
    .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.bancoPrincipal()))
}
