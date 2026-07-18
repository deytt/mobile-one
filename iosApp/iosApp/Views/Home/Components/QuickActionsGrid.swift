import SwiftUI
import shared

struct QuickActionsGrid: View {
    let features: FeatureFlags
    @Environment(\.whiteLabelConfig) private var config

    var body: some View {
        HStack(spacing: 8) {
            if features.pixEnabled {
                QuickActionButton(label: "PIX", systemImage: "qrcode")
            }
            QuickActionButton(label: "Transferir", systemImage: "arrow.left.arrow.right")
            if features.creditCardEnabled {
                QuickActionButton(label: "Pagar", systemImage: "creditcard")
            }
            QuickActionButton(label: "Recarga", systemImage: "iphone")
        }
    }
}

private struct QuickActionButton: View {
    let label: String
    let systemImage: String
    @Environment(\.whiteLabelConfig) private var config

    var body: some View {
        Button(action: {}) {
            VStack(spacing: 4) {
                Image(systemName: systemImage)
                    .font(.system(size: 20))
                Text(label)
                    .font(.caption2)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 10)
            .background(config.primaryColor.opacity(0.12))
            .foregroundStyle(config.primaryColor)
            .clipShape(RoundedRectangle(cornerRadius: config.cornerRadius / 2))
        }
    }
}

#Preview {
    QuickActionsGrid(features: BrandCatalog.shared.bancoPrincipal().features)
        .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
        .padding()
}
