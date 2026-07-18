import SwiftUI
import shared

/// Observa o [AppStateRepository.currentConfig] via [IOSDependencyProvider.watchCurrentConfig]
/// e re-publica as mudanças para que `MobileOneRootView` possa re-aplicar o tema white-label
/// em runtime quando o Brand Switcher é ativado (SPEC-004).
@MainActor
final class AppConfigObserver: ObservableObject {
    @Published private(set) var config: WhiteLabelConfig = BrandCatalog.shared.bancoPrincipal()
    private var canceller: FlowCanceller?

    init() {
        canceller = IOSDependencyProvider.shared.watchCurrentConfig { [weak self] newConfig in
            self?.config = newConfig
        }
    }

    deinit { canceller?.cancel() }
}

@main
struct iosAppApp: App {
    @StateObject private var configObserver = AppConfigObserver()

    init() {
        IOSDependencyProvider.shared.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            MobileOneRootView()
                .environment(\.whiteLabelConfig, configObserver.config)
                .environment(\.brandTheme, BrandTheme(config: configObserver.config))
                .environmentObject(configObserver)
        }
    }
}
