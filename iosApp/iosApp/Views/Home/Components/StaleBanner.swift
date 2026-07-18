import SwiftUI

struct StaleBanner: View {
    let lastUpdatedAt: Int64

    private var timeString: String {
        let date = Date(timeIntervalSince1970: Double(lastUpdatedAt) / 1000.0)
        let cal = Calendar.current
        let hour = cal.component(.hour, from: date)
        let min = cal.component(.minute, from: date)
        return String(format: "%02d:%02d", hour, min)
    }

    var body: some View {
        HStack {
            Image(systemName: "exclamationmark.triangle")
                .font(.caption)
            Text("Dados desatualizados · Última atualização: \(timeString)")
                .font(.caption)
        }
        .foregroundStyle(Color(hex: "#92400E"))
        .padding(.horizontal, 16)
        .padding(.vertical, 6)
        .frame(maxWidth: .infinity)
        .background(Color(hex: "#FEF3C7"))
    }
}

#Preview {
    StaleBanner(lastUpdatedAt: 1_700_000_000_000)
}
