import SwiftUI
import shared

struct ReceiptView: View {
    let onClose: () -> Void
    @ObservedObject var viewModel: PixViewModel

    var body: some View {
        ReceiptContent(
            uiState: viewModel.uiState,
            onShare: { shareReceipt(viewModel.uiState) },
            onClose: {
                viewModel.onReset()
                onClose()
            }
        )
    }

    private func shareReceipt(_ state: PixUiState) {
        guard let receipt = state.receipt else { return }
        let text = """
        Comprovante de Transferência PIX
        Para: \(receipt.recipientName)
        Valor: \(receipt.amountFormatted)
        Data: \(receipt.dateTimeFormatted)
        ID E2E: \(receipt.e2eId)
        Autenticação: \(receipt.authenticationCode)
        """
        let av = UIActivityViewController(activityItems: [text], applicationActivities: nil)
        UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }?
            .rootViewController?
            .present(av, animated: true)
    }
}

struct ReceiptContent: View {
    let uiState: PixUiState
    let onShare: () -> Void
    let onClose: () -> Void
    @Environment(\.whiteLabelConfig) private var config

    var body: some View {
        guard let receipt = uiState.receipt else {
            return AnyView(ProgressView())
        }
        return AnyView(
            VStack(spacing: 0) {
                ScrollView {
                    VStack(spacing: 24) {
                        Image(systemName: "checkmark.circle.fill")
                            .resizable()
                            .frame(width: 80, height: 80)
                            .foregroundStyle(config.primaryColor)
                            .padding(.top, 32)

                        Text("Transferência realizada!")
                            .font(.title2).bold()

                        Text(receipt.amountFormatted)
                            .font(.system(size: 44, weight: .bold))
                            .foregroundStyle(config.primaryColor)

                        VStack(spacing: 0) {
                            ReceiptRow(label: "Para", value: receipt.recipientName)
                            Divider().padding(.horizontal)
                            ReceiptRow(label: "Data/hora", value: receipt.dateTimeFormatted)
                            Divider().padding(.horizontal)
                            ReceiptRow(label: "ID E2E", value: receipt.e2eId)
                                .lineLimit(1)
                                .minimumScaleFactor(0.5)
                            Divider().padding(.horizontal)
                            ReceiptRow(label: "Autenticação", value: receipt.authenticationCode)
                        }
                        .background(Color(.secondarySystemBackground))
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .padding(.horizontal, 24)
                    }
                }

                VStack(spacing: 12) {
                    Button(action: onShare) {
                        HStack {
                            Image(systemName: "square.and.arrow.up")
                            Text("Compartilhar comprovante")
                                .fontWeight(.medium)
                        }
                        .foregroundStyle(config.primaryColor)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(config.primaryColor, lineWidth: 1.5)
                        )
                    }

                    Button(action: onClose) {
                        Text("Concluir")
                            .fontWeight(.semibold)
                            .foregroundStyle(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(config.primaryColor)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                }
                .padding(.horizontal, 24)
                .padding(.bottom, 32)
            }
            .navigationTitle("Comprovante")
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarBackButtonHidden(true)
            .toolbarBackground(config.primaryColor, for: .navigationBar)
            .toolbarColorScheme(.dark, for: .navigationBar)
        )
    }
}

private struct ReceiptRow: View {
    let label: String
    let value: String

    var body: some View {
        HStack {
            Text(label)
                .font(.subheadline)
                .foregroundStyle(.secondary)
            Spacer()
            Text(value)
                .font(.subheadline)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }
}

#Preview {
    NavigationStack {
        ReceiptContent(
            uiState: {
                var s = PixUiState()
                s.receipt = PixReceiptDisplay(
                    e2eId: "E0000000202607181200000123456789",
                    recipientName: "João da Silva",
                    amountFormatted: "R$ 150,00",
                    dateTimeFormatted: "18/07/2026 às 12:00",
                    authenticationCode: "ABC123"
                )
                return s
            }(),
            onShare: {},
            onClose: {}
        )
    }
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
}
