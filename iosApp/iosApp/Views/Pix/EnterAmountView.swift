import SwiftUI
import shared

struct EnterAmountView: View {
    @ObservedObject var viewModel: PixViewModel

    var body: some View {
        EnterAmountContent(
            uiState: viewModel.uiState,
            onAmountChanged: viewModel.onAmountChanged,
            onDescriptionChanged: viewModel.onDescriptionChanged,
            onContinue: viewModel.onContinueFromAmount
        )
    }
}

struct EnterAmountContent: View {
    let uiState: PixUiState
    let onAmountChanged: (Int64) -> Void
    let onDescriptionChanged: (String) -> Void
    let onContinue: () -> Void
    @Environment(\.whiteLabelConfig) private var config
    @State private var amountText: String = ""
    @State private var descriptionText: String = ""

    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    Text("Quanto você quer enviar?")
                        .font(.title2).bold()
                        .padding(.top, 8)

                    // Campo de valor grande
                    HStack(alignment: .center) {
                        Text("R$")
                            .font(.title2)
                            .foregroundStyle(.secondary)
                        TextField("0,00", text: $amountText)
                            .keyboardType(.decimalPad)
                            .font(.system(size: 40, weight: .bold))
                            .multilineTextAlignment(.trailing)
                            .onChange(of: amountText) { _, new in
                                let parsed = new.replacingOccurrences(of: ",", with: ".")
                                if let value = Double(parsed) {
                                    onAmountChanged(Int64(value * 100))
                                } else {
                                    onAmountChanged(0)
                                }
                            }
                    }
                    .padding()
                    .background(Color(.secondarySystemBackground))
                    .clipShape(RoundedRectangle(cornerRadius: 12))

                    Text("Limite diurno: R$ 20.000,00 · Limite noturno: R$ 1.000,00")
                        .font(.caption)
                        .foregroundStyle(.secondary)

                    // Campo de descrição opcional
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Descrição (opcional)")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                        TextField("Ex: Almoço, divisão de conta...", text: $descriptionText)
                            .padding()
                            .background(Color(.secondarySystemBackground))
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .onChange(of: descriptionText) { _, new in
                                onDescriptionChanged(new)
                            }
                    }
                }
                .padding(.horizontal, 24)
            }

            Button(action: onContinue) {
                Text("Continuar")
                    .fontWeight(.semibold)
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(uiState.amountCents > 0 ? config.primaryColor : Color.gray)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
            }
            .disabled(uiState.amountCents <= 0)
            .padding(.horizontal, 24)
            .padding(.bottom, 32)
        }
        .navigationTitle("Valor")
        .navigationBarTitleDisplayMode(.inline)
        .toolbarBackground(config.primaryColor, for: .navigationBar)
        .toolbarColorScheme(.dark, for: .navigationBar)
    }
}

#Preview {
    NavigationStack {
        EnterAmountContent(
            uiState: PixUiState(),
            onAmountChanged: { _ in },
            onDescriptionChanged: { _ in },
            onContinue: {}
        )
    }
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
}
