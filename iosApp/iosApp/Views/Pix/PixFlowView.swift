import SwiftUI
import shared

/**
 * Orquestrador do fluxo PIX (SPEC-003). Cria uma instância compartilhada de [PixViewModel]
 * e renderiza a sub-view correspondente ao step atual — análogo ao PixFlowScreen Android.
 */
struct PixFlowView: View {
    let onClose: () -> Void
    @StateObject private var viewModel = PixViewModel()

    var body: some View {
        Group {
            switch viewModel.uiState.step {
            case .enterKey:
                EnterKeyView(onBack: {
                    viewModel.onReset()
                    onClose()
                }, viewModel: viewModel)

            case .confirmRecipient:
                ConfirmRecipientView(
                    onBack: viewModel.onRejectRecipient,
                    viewModel: viewModel
                )

            case .enterAmount:
                EnterAmountView(viewModel: viewModel)

            case .review:
                ReviewView(viewModel: viewModel)

            case .processing:
                ProcessingView()

            case .receipt:
                ReceiptView(onClose: onClose, viewModel: viewModel)
            }
        }
        .alert(
            "Erro",
            isPresented: Binding(
                get: { viewModel.uiState.errorMessage != nil },
                set: { _ in viewModel.onDismissError() }
            )
        ) {
            Button("OK", action: viewModel.onDismissError)
        } message: {
            Text(viewModel.uiState.errorMessage ?? "")
        }
    }
}
