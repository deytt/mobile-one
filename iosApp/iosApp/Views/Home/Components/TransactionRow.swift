import SwiftUI
import shared

struct TransactionRow: View {
    let transaction: TransactionDisplay
    @Environment(\.whiteLabelConfig) private var config

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: transaction.category.systemImage)
                .frame(width: 24, height: 24)
                .foregroundStyle(config.primaryColor)

            VStack(alignment: .leading, spacing: 2) {
                Text(transaction.description)
                    .font(.subheadline)
                Text(transaction.dateFormatted)
                    .font(.caption)
                    .foregroundStyle(config.onSurfaceColor)
            }

            Spacer()

            Text(transaction.amountFormatted)
                .font(.subheadline)
                .foregroundStyle(transaction.isDebit ? config.errorColor : Color(hex: "#16A34A"))
        }
        .padding(.vertical, 6)
    }
}

private extension TransactionCategory {
    var systemImage: String {
        switch self {
        case .pix: return "qrcode"
        case .ted: return "arrow.2.circlepath"
        case .boleto: return "doc.text"
        case .card: return "creditcard"
        case .purchase: return "bag"
        case .deposit: return "arrow.down.circle"
        case .fee: return "arrow.up.circle"
        default: return "dollarsign.circle"
        }
    }
}

#Preview {
    TransactionRow(transaction: TransactionDisplay(
        id: "1",
        description: "PIX - João",
        amountFormatted: "- R$ 150,00",
        isDebit: true,
        dateFormatted: "Hoje",
        category: .pix
    ))
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
    .padding()
}
