import SwiftUI
import shared

/// Tela inicial (SPEC-001, node `28:19512`): exibe a marca ativa por um curto período enquanto
/// o `AuthViewModel` verifica se já existe biometria habilitada, decidindo entre Login e
/// Boas-vindas com biometria.
struct SplashView: View {
    let onFinished: () -> Void
    var minDuration: Duration = .milliseconds(900)

    var body: some View {
        SplashContent()
            .task {
                try? await Task.sleep(for: minDuration)
                onFinished()
            }
    }
}

struct SplashContent: View {
    @Environment(\.whiteLabelConfig) private var theme

    var body: some View {
        VStack {
            Spacer()
            VStack(spacing: 16) {
                ZStack {
                    RoundedRectangle(cornerRadius: 16)
                        .fill(Color.white.opacity(0.18))
                        .frame(width: 64, height: 64)
                    Text(brandInitials(theme.brandName))
                        .font(.system(size: 24, weight: .bold))
                        .foregroundStyle(.white)
                }
                Text(theme.brandName)
                    .font(.system(size: 22, weight: .semibold))
                    .foregroundStyle(.white)
            }
            Spacer()
            VStack(spacing: 12) {
                SplashLoadingDots()
                Text("Seguro e regulado pelo Banco Central do Brasil")
                    .font(.system(size: 10))
                    .foregroundStyle(.white.opacity(0.45))
            }
            .padding(.bottom, 40)
        }
        .padding(.horizontal, 32)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(theme.primaryColor)
        .ignoresSafeArea()
    }
}

private struct SplashLoadingDots: View {
    @State private var animate = false

    var body: some View {
        HStack(spacing: 8) {
            ForEach(0..<3, id: \.self) { index in
                Circle()
                    .fill(Color.white)
                    .frame(width: 6, height: 6)
                    .opacity(animate ? 1 : 0.3)
                    .animation(
                        .easeInOut(duration: 0.6)
                            .repeatForever(autoreverses: true)
                            .delay(Double(index) * 0.15),
                        value: animate
                    )
            }
        }
        .onAppear { animate = true }
    }
}

#Preview {
    SplashContent()
        .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
}

#Preview("Fintech Verde") {
    SplashContent()
        .environment(\.whiteLabelConfig, BrandCatalog.shared.fintechVerde())
}
