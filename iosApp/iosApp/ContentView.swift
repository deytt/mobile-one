import SwiftUI
import shared

/// Tela placeholder da Fundação KMP — prova que o iosApp consome o `shared` via
/// `greet()`/`Platform().name` e o tema white-label (SPEC-004) via `@Environment`. As telas
/// reais (Login, Home, PIX, ...) chegam a partir do Figma por spec.
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
