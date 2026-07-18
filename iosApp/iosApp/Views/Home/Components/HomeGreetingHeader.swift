import SwiftUI
import shared

/// Header da Home (SPEC-009): avatar + saudação + sino, sobre `colorPrimary`.
struct HomeGreetingHeader: View {
    let userName: String
    var onNotificationsTap: () -> Void = {}
    @Environment(\.brandTheme) private var brandTheme

    private var firstName: String {
        userName.split(separator: " ").first.map(String.init) ?? userName
    }

    var body: some View {
        HStack(spacing: 12) {
            ZStack {
                Circle()
                    .fill(Color.white)
                    .frame(width: 40, height: 40)
                    .overlay(Circle().stroke(Color.white, lineWidth: 2))
                Text(brandInitials(userName))
                    .font(brandTheme.font(size: 15, weight: .bold))
                    .foregroundStyle(brandTheme.primary)
            }

            Text("Olá, \(firstName)")
                .font(brandTheme.font(size: 18, weight: .bold))
                .foregroundStyle(.white)

            Spacer(minLength: 0)

            Button(action: onNotificationsTap) {
                Image(systemName: "bell")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 24, height: 24)
                    .foregroundStyle(.white)
            }
            .buttonStyle(.plain)
            .accessibilityLabel("Notificações")
        }
        .padding(16)
    }
}

#Preview {
    HomeGreetingHeader(userName: "Heitor Bastos")
        .background(Color(hex: "#003B6F"))
        .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.bancoPrincipal()))
}
