import SwiftUI
import shared

/// Tela inicial usada para validar o consumo do `shared` e do tema white-label (SPEC-004).
struct ContentView: View {
    @Environment(\.whiteLabelConfig) private var theme
    private let message = PlatformKt.greet()

    var body: some View {
        VStack(spacing: 12) {
            Text(theme.brandName)
                .font(.title2)
                .fontWeight(.bold)
                .foregroundStyle(theme.primaryColor)
            Text(message)
                .font(.subheadline)
                .multilineTextAlignment(.center)
        }
        .padding(24)
        .background(theme.backgroundColor)
        .clipShape(RoundedRectangle(cornerRadius: theme.cornerRadius))
    }
}

#Preview {
    ContentView()
}
