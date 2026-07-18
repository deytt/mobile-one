import SwiftUI
import shared

struct AccountCard: View {
    let account: AccountDisplay
    let isBalanceHidden: Bool
    let onToggleBalance: () -> Void
    @Environment(\.whiteLabelConfig) private var config

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 2) {
                    Text(account.holderName)
                        .font(.headline)
                        .foregroundStyle(config.onPrimaryColor)
                    Text("Conta \(account.maskedNumber)")
                        .font(.caption)
                        .foregroundStyle(config.onPrimaryColor.opacity(0.75))
                }
                Spacer()
                Button(action: onToggleBalance) {
                    Image(systemName: isBalanceHidden ? "eye" : "eye.slash")
                        .foregroundStyle(config.onPrimaryColor)
                }
            }

            Spacer().frame(height: 20)

            Text("Saldo disponível")
                .font(.caption)
                .foregroundStyle(config.onPrimaryColor.opacity(0.75))
            Text(account.balanceFormatted)
                .font(.title2)
                .bold()
                .foregroundStyle(config.onPrimaryColor)

            Spacer().frame(height: 8)

            Text("Limite: \(account.availableLimitFormatted)")
                .font(.caption)
                .foregroundStyle(config.onPrimaryColor.opacity(0.65))
        }
        .padding(20)
        .background(config.primaryColor)
        .clipShape(RoundedRectangle(cornerRadius: config.cornerRadius))
    }
}

#Preview {
    AccountCard(
        account: AccountDisplay(
            holderName: "Heitor Bastos",
            maskedNumber: "•••• 4521",
            balanceFormatted: "R$ 1.234,56",
            availableLimitFormatted: "R$ 5.000,00"
        ),
        isBalanceHidden: false,
        onToggleBalance: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
    .padding()
}
