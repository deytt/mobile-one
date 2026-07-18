import SwiftUI
import shared

/// Bottom bar da Home (SPEC-009): pílula Cartões/Conta + botão grade (Brand Switcher).
struct HomeTabBar: View {
    let currentTab: HomeTab
    let onTabChange: (HomeTab) -> Void
    let onBrandSwitcher: () -> Void
    @Environment(\.brandTheme) private var brandTheme

    var body: some View {
        HStack(spacing: 16) {
            tabSwitcher
            Spacer(minLength: 0)
            brandSwitcherButton
        }
        .padding(.horizontal, 16)
        .padding(.top, 12)
        .padding(.bottom, 8)
        .background(brandTheme.surface.ignoresSafeArea(edges: .bottom))
        .shadow(color: .black.opacity(0.08), radius: 8, x: 0, y: -1)
    }

    private var tabSwitcher: some View {
        HStack(spacing: 0) {
            ForEach(HomeTab.allCases) { tab in
                let selected = tab == currentTab
                Button {
                    withAnimation(.easeInOut(duration: 0.2)) {
                        onTabChange(tab)
                    }
                } label: {
                    Text(tab.title)
                        .font(brandTheme.font(size: 14, weight: selected ? .semibold : .medium))
                        .foregroundStyle(selected ? Color.white : brandTheme.onSurface)
                        .padding(.horizontal, 20)
                        .padding(.vertical, 8)
                        .background(selected ? brandTheme.primary : Color.clear)
                        .clipShape(Capsule())
                }
                .buttonStyle(.plain)
            }
        }
        .padding(4)
        .background(brandTheme.background)
        .clipShape(Capsule())
    }

    private var brandSwitcherButton: some View {
        Button(action: onBrandSwitcher) {
            Image(systemName: "square.grid.2x2.fill")
                .resizable()
                .scaledToFit()
                .frame(width: 20, height: 20)
                .foregroundStyle(.white)
                .frame(width: 44, height: 44)
                .background(brandTheme.primary)
                .clipShape(Circle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel("Brand Switcher")
    }
}

#Preview {
    VStack {
        Spacer()
        HomeTabBar(
            currentTab: .cartoes,
            onTabChange: { _ in },
            onBrandSwitcher: {}
        )
    }
    .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.bancoPrincipal()))
}
