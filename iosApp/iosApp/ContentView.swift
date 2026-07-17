import SwiftUI
import shared

/// Tela placeholder da Fundação KMP — apenas prova que o iosApp consome o `shared` via
/// `greet()`/`Platform().name`. As telas reais (Login, Home, PIX, ...) chegam a partir do
/// Figma por spec.
struct ContentView: View {
    private let message = PlatformKt.greet()

    var body: some View {
        VStack(spacing: 12) {
            Text(message)
                .font(.title2)
                .fontWeight(.semibold)
                .multilineTextAlignment(.center)
        }
        .padding(24)
    }
}

#Preview {
    ContentView()
}
