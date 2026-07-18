import SwiftUI
import shared

/// Brand Switcher (SPEC-011 layout / SPEC-004 comportamento).
/// Após aplicar, [onApplied] reinicia a Home com o novo tema.
struct BrandSwitcherView: View {
    let onBack: () -> Void
    let onApplied: (WhiteLabelConfig) -> Void
    @StateObject private var viewModel = BrandSwitcherViewModel()

    var body: some View {
        BrandSwitcherContent(
            uiState: viewModel.uiState,
            onSelectBrand: viewModel.onSelectBrand,
            onApply: viewModel.onApply
        )
        .navigationBarHidden(true)
        .toolbarColorScheme(.dark, for: .navigationBar)
        .onChange(of: viewModel.uiState.applied) { _, applied in
            guard applied else { return }
            let selected = viewModel.uiState.brands.first { $0.brandId == viewModel.uiState.selectedBrandId }
            if let cfg = selected { onApplied(cfg) }
        }
    }
}

struct BrandSwitcherContent: View {
    let uiState: BrandSwitcherUiState
    let onSelectBrand: (String) -> Void
    let onApply: () -> Void

    private var selectedBrand: WhiteLabelConfig {
        uiState.brands.first { $0.brandId == uiState.selectedBrandId }
            ?? uiState.brands.first
            ?? BrandCatalog.shared.bancoPrincipal()
    }

    private var selectedPrimary: Color {
        Color(hex: selectedBrand.theme.colorPrimary)
    }

    var body: some View {
        VStack(spacing: 0) {
            BrandSwitcherHeader()

            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Selecionar marca".uppercased())
                        .font(BrandFonts.font(familyName: "Inter", size: 11, weight: .semibold))
                        .foregroundStyle(Color(hex: "6B7280"))
                        .tracking(0.77)
                        .padding(.top, 20)

                    ForEach(uiState.brands, id: \.brandId) { brand in
                        BrandOptionCard(
                            brand: brand,
                            isSelected: brand.brandId == uiState.selectedBrandId,
                            onSelect: { onSelectBrand(brand.brandId) }
                        )
                    }
                }
                .padding(.horizontal, 16)
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color(hex: "F4F4F6"))

            VStack(spacing: 0) {
                Button(action: onApply) {
                    HStack(spacing: 8) {
                        if uiState.isApplying {
                            ProgressView()
                                .tint(.white)
                        }
                        Text("Aplicar marca")
                            .font(BrandFonts.font(familyName: "Inter", size: 15, weight: .bold))
                            .foregroundStyle(.white)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
                }
                .background(selectedPrimary)
                .clipShape(RoundedRectangle(cornerRadius: 12))
                .disabled(uiState.isApplying)

                Text("As mudanças são aplicadas instantaneamente em todo o app")
                    .font(BrandFonts.font(familyName: "Inter", size: 11, weight: .regular))
                    .foregroundStyle(Color(hex: "9CA3AF"))
                    .multilineTextAlignment(.center)
                    .frame(maxWidth: .infinity)
                    .padding(.top, 12)
            }
            .padding(.horizontal, 16)
            .padding(.top, 8)
            .padding(.bottom, 32)
            .background(Color(hex: "F4F4F6"))
        }
        .background(Color(hex: "F4F4F6").ignoresSafeArea(edges: .bottom))
    }
}

private struct BrandSwitcherHeader: View {
    var body: some View {
        ZStack(alignment: .topTrailing) {
            VStack(alignment: .leading, spacing: 0) {
                Text("Brand Switcher")
                    .font(BrandFonts.font(familyName: "Inter", size: 18, weight: .bold))
                    .foregroundStyle(.white)
                    .tracking(-0.36)
                    .padding(.bottom, 4)

                Text("Modo de demonstração")
                    .font(BrandFonts.font(familyName: "Inter", size: 12, weight: .regular))
                    .foregroundStyle(Color.white.opacity(0.45))
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            Text("DEV MODE")
                .font(BrandFonts.font(familyName: "Inter", size: 10, weight: .bold))
                .foregroundStyle(.white)
                .tracking(0.6)
                .padding(.horizontal, 8)
                .padding(.vertical, 2)
                .background(Color(hex: "F7941D"))
                .clipShape(RoundedRectangle(cornerRadius: 4))
        }
        .padding(.horizontal, 16)
        .padding(.top, 16)
        .padding(.bottom, 20)
        .frame(maxWidth: .infinity)
        .background(Color(hex: "1A1A2E").ignoresSafeArea(edges: .top))
    }
}

private struct BrandOptionCard: View {
    let brand: WhiteLabelConfig
    let isSelected: Bool
    let onSelect: () -> Void

    private var primaryColor: Color { Color(hex: brand.theme.colorPrimary) }
    private var secondaryColor: Color { Color(hex: brand.theme.colorSecondary) }
    private var avatarRadius: CGFloat { CGFloat(brand.theme.borderRadiusDp) }

    var body: some View {
        Button(action: onSelect) {
            HStack(alignment: .center, spacing: 12) {
                BrandRadio(selected: isSelected, color: primaryColor)

                RoundedRectangle(cornerRadius: avatarRadius)
                    .fill(primaryColor)
                    .frame(width: 24, height: 24)

                VStack(alignment: .leading, spacing: 0) {
                    Text(brand.brandName)
                        .font(BrandFonts.font(familyName: brand.theme.fontFamilyName, size: 14, weight: .bold))
                        .foregroundStyle(Color(hex: "1A1A2E"))

                    Text("\(brand.theme.fontFamilyName) · \(brand.theme.borderRadiusDp)px radius")
                        .font(BrandFonts.font(familyName: "Inter", size: 11, weight: .medium))
                        .foregroundStyle(Color(hex: "6B7280"))
                        .padding(.top, 2)

                    HStack(spacing: 6) {
                        ColorPill(hex: brand.theme.colorPrimary, color: primaryColor)
                        ColorPill(hex: brand.theme.colorSecondary, color: secondaryColor)
                    }
                    .padding(.top, 6)
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                if isSelected {
                    Image(systemName: "checkmark.circle")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 16, height: 16)
                        .foregroundStyle(primaryColor)
                }
            }
            .padding(16)
            .background(Color.white)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(isSelected ? primaryColor : Color.clear, lineWidth: 2)
            )
            .shadow(
                color: isSelected
                    ? primaryColor.opacity(0.13)
                    : Color.black.opacity(0.08),
                radius: isSelected ? 0 : 1.5,
                x: 0,
                y: isSelected ? 0 : 1
            )
        }
        .buttonStyle(.plain)
    }
}

private struct BrandRadio: View {
    let selected: Bool
    let color: Color

    var body: some View {
        ZStack {
            Circle()
                .stroke(selected ? color : Color(hex: "D1D5DB"), lineWidth: 2)
                .background(Circle().fill(selected ? color : Color.clear))
                .frame(width: 20, height: 20)

            if selected {
                Circle()
                    .fill(Color.white)
                    .frame(width: 8, height: 8)
            }
        }
        .frame(width: 20, height: 20)
    }
}

private struct ColorPill: View {
    let hex: String
    let color: Color

    var body: some View {
        HStack(spacing: 4) {
            Circle()
                .fill(color)
                .frame(width: 8, height: 8)
            Text(hex.uppercased())
                .font(BrandFonts.font(familyName: "Inter", size: 10, weight: .semibold))
                .foregroundStyle(color)
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 2)
        .frame(height: 19)
        .background(color.opacity(0.09))
        .clipShape(Capsule())
    }
}

#Preview {
    BrandSwitcherContent(
        uiState: BrandSwitcherUiState(
            brands: BrandCatalog.shared.all(),
            selectedBrandId: "banco_principal"
        ),
        onSelectBrand: { _ in },
        onApply: {}
    )
}
