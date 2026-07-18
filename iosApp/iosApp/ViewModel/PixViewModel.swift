import Foundation
import shared

// MARK: - UI State structs

struct PixUiState {
    var step: PixFlowStep = .enterKey
    var keyInput: String = ""
    var detectedKeyType: String? = nil       // "CPF","CNPJ","Email","Phone","RandomKey","QRCode"
    var keyValidationState: KeyValidationState = .idle
    var recipient: PixRecipientDisplay? = nil
    var amountCents: Int64 = 0
    var amountFormatted: String = "R$ 0,00"
    var description: String = ""
    var isLoading: Bool = false
    var receipt: PixReceiptDisplay? = nil
    var errorMessage: String? = nil
}

enum PixFlowStep {
    case enterKey
    case confirmRecipient
    case enterAmount
    case review
    case processing
    case receipt
}

enum KeyValidationState {
    case idle
    case valid
    case invalid(String)
}

struct PixRecipientDisplay {
    let name: String
    let institution: String
    let maskedKey: String
}

struct PixReceiptDisplay {
    let e2eId: String
    let recipientName: String
    let amountFormatted: String
    let dateTimeFormatted: String
    let authenticationCode: String
}

// MARK: - ViewModel

@MainActor
final class PixViewModel: ObservableObject {
    @Published private(set) var uiState = PixUiState()

    // MARK: Tela 1: Inserir chave

    func onKeyChanged(_ input: String) {
        uiState.keyInput = input
        let detected = IOSDependencyProvider.shared.detectPixKeyType(input: input)
        uiState.detectedKeyType = detected

        guard let type = detected, !input.isEmpty else {
            uiState.keyValidationState = .idle
            return
        }
        // Enquanto digita, mostra "valid" se já passou na validação — sem mostrar erro prematuro
        let errorReason = IOSDependencyProvider.shared.validatePixKey(key: input, typeString: type)
        uiState.keyValidationState = errorReason == nil ? .valid : .idle
    }

    func onContinueFromKey() {
        guard let type = uiState.detectedKeyType else { return }
        let errorReason = IOSDependencyProvider.shared.validatePixKey(key: uiState.keyInput, typeString: type)
        if let reason = errorReason {
            uiState.keyValidationState = .invalid(reason)
            return
        }
        lookupRecipient(pixKey: uiState.keyInput)
    }

    func onScanQRCode() {
        Task {
            guard let payload = try? await IOSDependencyProvider.shared.scanPixQRCode() else { return }
            let outcome = IOSDependencyProvider.shared.parsePixQRCode(payload: payload)
            if outcome.isSuccess, let key = outcome.pixKey, let type = outcome.keyType {
                uiState.keyInput = key
                uiState.detectedKeyType = type
                uiState.keyValidationState = .valid
                if outcome.amountCents > 0 {
                    uiState.amountCents = outcome.amountCents
                    uiState.amountFormatted = formatCents(outcome.amountCents)
                }
                lookupRecipient(pixKey: key)
            } else {
                uiState.errorMessage = outcome.errorMessage ?? "QR Code inválido"
            }
        }
    }

    // MARK: Tela 2: Confirmar destinatário

    func onConfirmRecipient() {
        uiState.step = .enterAmount
    }

    func onRejectRecipient() {
        uiState.recipient = nil
        uiState.keyValidationState = .idle
        uiState.step = .enterKey
    }

    // MARK: Tela 3: Inserir valor

    func onAmountChanged(cents: Int64) {
        uiState.amountCents = cents
        uiState.amountFormatted = formatCents(cents)
    }

    func onDescriptionChanged(_ description: String) {
        uiState.description = description
    }

    func onContinueFromAmount() {
        guard uiState.amountCents > 0 else { return }
        uiState.step = .review
    }

    // MARK: Tela 4: Revisão e confirmação biométrica

    func onConfirmTransfer() {
        guard let recipient = uiState.recipient,
              let keyType = uiState.detectedKeyType else { return }

        Task {
            uiState.step = .processing
            uiState.isLoading = true

            do {
                let outcome = try await IOSDependencyProvider.shared.executePixTransfer(
                    pixKey: uiState.keyInput,
                    typeString: keyType,
                    amountCents: uiState.amountCents,
                    description: uiState.description,
                    recipientName: recipient.name,
                    recipientInstitution: recipient.institution
                )

                if outcome.isSuccess, let e2eId = outcome.e2eId {
                    let receipt = PixReceiptDisplay(
                        e2eId: e2eId,
                        recipientName: recipient.name,
                        amountFormatted: uiState.amountFormatted,
                        dateTimeFormatted: buildDateTimeFormatted(),
                        authenticationCode: String(e2eId.suffix(6)).uppercased()
                    )
                    uiState.receipt = receipt
                    uiState.step = .receipt
                } else {
                    uiState.errorMessage = outcome.errorMessage ?? "Falha na transferência"
                    uiState.step = .review
                }
            } catch {
                uiState.errorMessage = error.localizedDescription
                uiState.step = .review
            }
            uiState.isLoading = false
        }
    }

    // MARK: Utilitários

    func onDismissError() {
        uiState.errorMessage = nil
    }

    func onReset() {
        uiState = PixUiState()
    }

    // MARK: Privados

    private func lookupRecipient(pixKey: String) {
        Task {
            uiState.isLoading = true
            do {
                let outcome = try await IOSDependencyProvider.shared.lookupPixRecipient(pixKey: pixKey)
                if outcome.isSuccess,
                   let name = outcome.name,
                   let institution = outcome.institution,
                   let key = outcome.pixKey,
                   let keyType = outcome.pixKeyType {
                    uiState.recipient = PixRecipientDisplay(
                        name: name,
                        institution: institution,
                        maskedKey: maskKey(key, type: keyType)
                    )
                    uiState.step = .confirmRecipient
                } else {
                    uiState.errorMessage = outcome.errorMessage ?? "Destinatário não encontrado"
                }
            } catch {
                uiState.errorMessage = error.localizedDescription
            }
            uiState.isLoading = false
        }
    }

    private func maskKey(_ key: String, type: String) -> String {
        switch type {
        case "CPF":
            let digits = key.filter { $0.isNumber }
            guard digits.count == 11 else { return "•••.•••.•••-••" }
            let mid = String(digits.dropFirst(3).prefix(3))
            return "•••.\(mid).•••-••"
        case "CNPJ":
            return "••.•••.•••/••••-••"
        case "Email":
            let parts = key.split(separator: "@")
            if parts.count == 2 {
                let local = String(parts[0].prefix(2)) + "•••"
                return "\(local)@\(parts[1])"
            }
            return "•••@•••"
        case "Phone":
            return "+55 (••) •••••-\(key.suffix(4))"
        case "RandomKey":
            return "\(key.prefix(8))-••••-••••-••••-••••••••••••"
        default:
            return key
        }
    }

    private func formatCents(_ cents: Int64) -> String {
        let reais = cents / 100
        let centavos = cents % 100
        return String(format: "R$ %lld,%02lld", reais, centavos)
    }

    private func buildDateTimeFormatted() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd/MM/yyyy 'às' HH:mm"
        formatter.locale = Locale(identifier: "pt_BR")
        return formatter.string(from: Date())
    }
}
