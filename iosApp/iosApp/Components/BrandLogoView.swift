import SwiftUI
import shared

/// Identidade visual da marca (SPEC-005 / SPEC-006).
///
/// - Banco Principal → rounded rect (`cornerRadius`, tipicamente 12), fundo 18% branco
/// - Fintech Verde → círculo, fundo 18% branco
/// - Banco Premium → losango 45°, radius 4, fundo 15% branco
struct BrandLogoView: View {
    let size: CGFloat
    let config: WhiteLabelConfig

    private var theme: BrandTheme { BrandTheme(config: config) }
    private var isPremium: Bool { config.brandId == "banco_premium" }
    private var isFintech: Bool { config.brandId == "fintech_verde" }

    var body: some View {
        ZStack {
            logoShape
            Text(brandInitials(config.brandName))
                .font(initialsFont)
                .tracking(initialsTracking)
                .foregroundStyle(.white)
        }
        .frame(width: size, height: size)
    }

    @ViewBuilder
    private var logoShape: some View {
        if isPremium {
            RoundedRectangle(cornerRadius: 4)
                .fill(Color.white.opacity(0.15))
                .frame(width: size * 51 / 64, height: size * 51 / 64)
                .rotationEffect(.degrees(45))
        } else if isFintech {
            Circle()
                .fill(Color.white.opacity(0.18))
                .frame(width: size, height: size)
        } else {
            RoundedRectangle(cornerRadius: theme.cornerRadius)
                .fill(Color.white.opacity(0.18))
                .frame(width: size, height: size)
        }
    }

    private var initialsFont: Font {
        let fontSize = size * 24 / 64
        if isPremium {
            return theme.font(size: fontSize, weight: .bold, italic: true)
        }
        if isFintech {
            return theme.font(size: fontSize, weight: .semibold)
        }
        return theme.font(size: fontSize, weight: .bold)
    }

    private var initialsTracking: CGFloat {
        if isFintech { return -0.24 }
        if isPremium { return 0 }
        return -0.48
    }
}

#Preview("Banco Principal") {
    BrandLogoView(size: 64, config: BrandCatalog.shared.bancoPrincipal())
        .padding()
        .background(Color(hex: "#003B6F"))
}

#Preview("Fintech Verde") {
    BrandLogoView(size: 64, config: BrandCatalog.shared.fintechVerde())
        .padding()
        .background(Color(hex: "#00A86B"))
}

#Preview("Banco Premium") {
    BrandLogoView(size: 64, config: BrandCatalog.shared.bancoPremium())
        .padding()
        .background(Color(hex: "#7B2D00"))
}
