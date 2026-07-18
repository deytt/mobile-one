import SwiftUI
import shared

/// Tela inicial (SPEC-006): marca centralizada sobre gradiente radial, page dots e texto
/// regulatório no rodapé. Decide o próximo destino após `minDuration` (SPEC-001).
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
    @Environment(\.whiteLabelConfig) private var config
    @Environment(\.brandTheme) private var brandTheme

    var body: some View {
        GeometryReader { geometry in
            let endRadius = max(geometry.size.width, geometry.size.height)

            ZStack {
                brandTheme.splashGradient(endRadius: endRadius)
                    .ignoresSafeArea()

                VStack(spacing: 0) {
                    Spacer()

                    VStack(spacing: 16) {
                        BrandLogoView(size: 64, config: config)
                        Text(config.brandName)
                            .font(brandNameFont)
                            .tracking(brandTheme.splashNameTracking)
                            .foregroundStyle(.white)
                            .multilineTextAlignment(.center)
                    }
                    .padding(.horizontal, 32)

                    Spacer()

                    VStack(spacing: 12) {
                        SplashPageDots()
                        Text("Seguro e regulado pelo Banco Central do Brasil")
                            .font(brandTheme.font(size: 10))
                            .foregroundStyle(.white.opacity(0.45))
                            .multilineTextAlignment(.center)
                    }
                    .padding(.bottom, 40)
                    .padding(.horizontal, 32)
                }
            }
        }
        .preferredColorScheme(.dark)
    }

    private var brandNameFont: Font {
        if config.brandId == "banco_premium" {
            return brandTheme.font(size: 22, weight: .bold, italic: true)
        }
        return brandTheme.font(size: 22, weight: .semibold)
    }
}

/// Três pontos estáticos (SPEC-006): tamanhos ~4.8 / 5.1 / 6.4 e opacidades 31% / 40% / 76%
/// sobre `rgba(255,255,255,0.4)`.
private struct SplashPageDots: View {
    private let sizes: [CGFloat] = [4.8, 5.1, 6.4]
    private let opacities: [Double] = [0.31, 0.40, 0.76]

    var body: some View {
        HStack(spacing: 8) {
            ForEach(Array(sizes.enumerated()), id: \.offset) { index, size in
                Circle()
                    .fill(Color.white.opacity(0.4))
                    .frame(width: size, height: size)
                    .opacity(opacities[index])
            }
        }
    }
}

#Preview("Banco Principal") {
    SplashContent()
        .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
        .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.bancoPrincipal()))
}

#Preview("Fintech Verde") {
    SplashContent()
        .environment(\.whiteLabelConfig, BrandCatalog.shared.fintechVerde())
        .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.fintechVerde()))
}

#Preview("Banco Premium") {
    SplashContent()
        .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPremium())
        .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.bancoPremium()))
}
