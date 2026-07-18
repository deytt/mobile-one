import SwiftUI
import shared

struct ProcessingView: View {
    @Environment(\.whiteLabelConfig) private var config

    var body: some View {
        VStack(spacing: 24) {
            ProgressView()
                .scaleEffect(2.0)
                .tint(config.primaryColor)

            Text("Processando transferência...")
                .font(.headline)

            Text("Aguarde, isso pode levar alguns segundos.")
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
        }
        .padding(40)
        .navigationTitle("")
        .navigationBarBackButtonHidden(true)
    }
}

#Preview {
    ProcessingView()
        .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
}
