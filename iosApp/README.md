# iosApp

Swift sources (`iOSApp.swift`, `ContentView.swift`) are written and ready, but there is
no `.xcodeproj` in this folder yet — generating one reliably requires Xcode (or
`xcodegen`) running on macOS, which isn't available in this dev environment.

When Mac/Xcode access is available (Phase 7 of the plan):

1. Open Xcode, create a new iOS App project named `iosApp` in this directory
   (SwiftUI lifecycle, Swift language).
2. Replace the generated `iOSApp.swift` / `ContentView.swift` with the files already
   present here.
3. Add a "Run Script" build phase that invokes
   `../composeApp/build.gradle.kts`'s `embedAndSignAppleFrameworkForXcode` task
   (standard Kotlin Multiplatform + Xcode integration), or link the
   `ComposeApp.framework` produced by `:composeApp:linkDebugFrameworkIos<Arch>`.
4. Build/run on a simulator or device.

Until then, `composeApp`'s `iosMain` source set is compiled as part of the KMP
module's metadata/klib compilation but not linked into a real iOS app binary.
