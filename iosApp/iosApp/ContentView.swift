import Shared
import SwiftUI
import UIKit

private struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ZStack {
            Color(red: 3 / 255, green: 10 / 255, blue: 18 / 255)
                .ignoresSafeArea()
            ComposeView()
                .ignoresSafeArea(.keyboard)
        }
        .preferredColorScheme(.dark)
    }
}
