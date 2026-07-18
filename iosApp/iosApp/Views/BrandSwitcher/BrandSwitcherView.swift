import SwiftUI
import shared

/**
 * Tela Dev Mode de troca de marca (SPEC-004), acessível pelo ícone ⚙ da Home (SPEC-002).
 * Após aplicar, [onApplied] é chamado para que a navegação reinicie a Home com o novo tema.
 */
struct BrandSwitcherView: View {
    let onBack: () -> Void
    let onApplied: (WhiteLabelConfig) -> Void
    @StateObject private var viewModel = BrandSwitcherViewModel()
    @Environment(\.whiteLabelConfig) private var config

    var body: some View {
        BrandSwitcherContent(
            uiState: viewModel.uiState,
            onBack: onBack,
            onSelectBrand: viewModel.onSelectBrand,
            onApply: viewModel.onApply
        )
        .onChange(of: viewModel.uiState.applied) { _, applied in
            guard applied else { return }
            let selected = viewModel.uiState.brands.first { $0.brandId == viewModel.uiState.selectedBrandId }
            if let cfg = selected { onApplied(cfg) }
        }
    }
}

struct BrandSwitcherContent: View {
    let uiState: BrandSwitcherUiState
    let onBack: () -> Void
    let onSelectBrand: (String) -> Void
    let onApply: () -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Selecione a configuração de marca para demonstrar o white-label ao vivo.")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .padding(.horizontal)

                ForEach(uiState.brands, id: \.brandId) { brand in
                    BrandOptionCard(
                        brand: brand,
                        isSelected: brand.brandId == uiState.selectedBrandId,
                        onSelect: { onSelectBrand(brand.brandId) }
                    )
                    .padding(.horizontal)
                }

                Button(action: onApply) {
                    HStack {
                        if uiState.isApplying {
                            ProgressView()
                                .padding(.trailing, 4)
                        }
                        Text("Aplicar")
                            .frame(maxWidth: .infinity)
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .foregroundStyle(.white)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                }
                .disabled(uiState.isApplying)
                .padding(.horizontal)
                .padding(.top, 8)
            }
            .padding(.vertical)
        }
        .navigationTitle("Dev Mode · Trocar Marca")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: onBack) {
                    Image(systemName: "chevron.left")
                }
            }
        }
    }
}

private struct BrandOptionCard: View {
    let brand: WhiteLabelConfig
    let isSelected: Bool
    let onSelect: () -> Void

    private var primaryColor: Color { Color(hex: brand.theme.colorPrimary) }
    private var secondaryColor: Color { Color(hex: brand.theme.colorSecondary) }

    var body: some View {
        Button(action: onSelect) {
            HStack(spacing: 12) {
                Image(systemName: isSelected ? "largecircle.fill.circle" : "circle")
                    .foregroundStyle(isSelected ? Color.blue : Color.secondary)

                VStack(alignment: .leading, spacing: 2) {
                    Text(brand.brandName)
                        .font(.headline)
                        .foregroundStyle(.primary)
                    Text("Raio: \(brand.theme.borderRadiusDp)dp · \(brand.theme.fontFamilyName)")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }

                Spacer()

                HStack(spacing: 6) {
                    Circle().fill(primaryColor).frame(width: 18, height: 18)
                    Circle().fill(secondaryColor).frame(width: 18, height: 18)
                }
            }
            .padding()
            .background(isSelected ? Color.blue.opacity(0.08) : Color(.systemGray6))
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(isSelected ? Color.blue : Color.clear, lineWidth: 1.5)
            )
        }
    }
}

#Preview {
    BrandSwitcherContent(
        uiState: BrandSwitcherUiState(
            brands: BrandCatalog.shared.all(),
            selectedBrandId: "banco_principal"
        ),
        onBack: {},
        onSelectBrand: { _ in },
        onApply: {}
    )
}
