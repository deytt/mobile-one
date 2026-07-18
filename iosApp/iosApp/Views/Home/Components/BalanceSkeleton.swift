import SwiftUI

struct BalanceSkeleton: View {
    @State private var animating = false

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            SkeletonLine(width: 100, height: 14)
            SkeletonLine(width: 180, height: 28)
            SkeletonLine(width: 120, height: 12)
        }
        .padding(20)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(.systemGray6))
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .opacity(animating ? 0.4 : 0.9)
        .onAppear {
            withAnimation(.easeInOut(duration: 0.8).repeatForever(autoreverses: true)) {
                animating = true
            }
        }
    }
}

private struct SkeletonLine: View {
    let width: CGFloat
    let height: CGFloat

    var body: some View {
        RoundedRectangle(cornerRadius: 4)
            .fill(Color(.systemGray4))
            .frame(width: width, height: height)
    }
}

#Preview {
    BalanceSkeleton().padding()
}
