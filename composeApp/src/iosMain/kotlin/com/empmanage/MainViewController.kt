package com.empmanage

import androidx.compose.ui.window.ComposeUIViewController
import com.empmanage.di.commonModules
import com.empmanage.di.platformModule
import org.koin.core.context.startKoin

private var koinStarted = false

fun MainViewController() = ComposeUIViewController {
    if (!koinStarted) {
        startKoin {
            modules(commonModules + platformModule())
        }
        koinStarted = true
    }
    App()
}
