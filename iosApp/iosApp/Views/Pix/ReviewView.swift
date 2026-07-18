import SwiftUI
import shared

struct ReviewView: View {
    @ObservedObject var viewModel: PixViewModel

    var body: some View {
        ReviewContent(
            uiState: viewModel.uiState,
            onConfirm: viewModel.onConfirmTransfer
        )
    }
}

struct ReviewContent: View {
    let uiState: PixUiState
    let onConfirm: () -> Void
    @Environment(\.whiteLabelConfig) private var config

    var body: some View {
        guard let recipient = uiState.recipient else {
            return AnyView(ProgressView())
        }
        return AnyView(
            VStack(spacing: 0) {
                ScrollView {
                    VStack(alignment: .leading, spacing: 20) {
                        Text("Confirme os dados")
                            .font(.title2).bold()
                            .padding(.top, 8)

                        VStack(spacing: 0) {
                            ReviewRow(label: "Para", value: recipient.name)
                            Divider().padding(.horizontal)
                            ReviewRow(label: "Banco", value: recipient.institution)
                            Divider().padding(.horizontal)
                            ReviewRow(label: "Chave", value: recipient.maskedKey)
                            Divider().padding(.horizontal)
                            ReviewRow(
                                label: "Valor",
                                value: uiState.amountFormatted,
                                valueFont: .title3,
                                valueColor: config.primaryColor
                            )
                            if !uiState.description.isEmpty {
                                Divider().padding(.horizontal)
                                ReviewRow(label: "Descrição", value: uiState.description)
                            }
                        }
                        .background(Color(.secondarySystemBackground))
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                    }
                    .padding(.horizontal, 24)
                }

                Button(action: onConfirm) {
                    HStack {
                        Spacer()
                        Image(systemName: "touchid")
                        Text("Confirmar com biometria")
                            .fontWeight(.semibold)
                        Spacer()
                    }
                    .foregroundStyle(.white)
                    .padding()
                    .background(config.primaryColor)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                }
                .disabled(uiState.isLoading)
                .padding(.horizontal, 24)
                .padding(.bottom, 32)
            }
            .navigationTitle("Revisão")
            .navigationBarTitleDisplayMode(.inline)
            .toolbarBackground(config.primaryColor, for: .navigationBar)
            .toolbarColorScheme(.dark, for: .navigationBar)
        )
    }
}

private struct ReviewRow: View {
    let label: String
    let value: String
    var valueFont: Font = .body
    var valueColor: Color = .primary

    var body: some View {
        HStack {
            Text(label)
                .font(.subheadline)
                .foregroundStyle(.secondary)
            Spacer()
            Text(value)
                .font(valueFont)
                .foregroundStyle(valueColor)
                .fontWeight(valueFont == .title3 ? .bold : .regular)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }
}

#Preview {
    NavigationStack {
        ReviewContent(
            uiState: {
                var s = PixUiState()
                s.recipient = PixRecipientDisplay(name: "João da Silva", institution: "Nubank", maskedKey: "•••.456.•••-••")
                s.amountCents = 15_000
                s.amountFormatted = "R$ 150,00"
                s.description = "Almoço"
                return s
            }(),
            onConfirm: {}
        )
    }
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
}
