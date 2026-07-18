import SwiftUI
import shared

@main
struct iosAppApp: App {
    init() {
        IOSDependencyProvider.shared.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            MobileOneRootView()
        }
    }
}
