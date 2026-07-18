import SwiftUI

/// Lê o inset superior (status bar / Dynamic Island) sem alterar o layout.
private struct TopSafeAreaPreferenceKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = max(value, nextValue())
    }
}

extension View {
    /// Captura `safeAreaInsets.top` do container (antes de `ignoresSafeArea` no scroll).
    func readTopSafeArea(_ topSafeArea: Binding<CGFloat>) -> some View {
        background {
            GeometryReader { geo in
                Color.clear.preference(
                    key: TopSafeAreaPreferenceKey.self,
                    value: geo.safeAreaInsets.top
                )
            }
        }
        .onPreferenceChange(TopSafeAreaPreferenceKey.self) { topSafeArea.wrappedValue = $0 }
    }

    /// Header da Home como um único bloco: status bar + conteúdo, que sobe no scroll.
    /// Espelha o padrão Android (`background(primary)` + `statusBarsPadding()`).
    func homeScrollingBrandHeader(
        color: Color,
        topSafeArea: CGFloat,
        bottomCornerRadius: CGFloat = 0
    ) -> some View {
        padding(.top, topSafeArea)
            .background {
                if bottomCornerRadius > 0 {
                    UnevenRoundedRectangle(
                        topLeadingRadius: 0,
                        bottomLeadingRadius: bottomCornerRadius,
                        bottomTrailingRadius: bottomCornerRadius,
                        topTrailingRadius: 0,
                        style: .continuous
                    )
                    .fill(color)
                } else {
                    color
                }
            }
    }
}
