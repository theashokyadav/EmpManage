package com.empmanage.platform

import androidx.compose.runtime.Composable

/** iOS has no hardware/system back button; in-app navigation relies on UI controls instead. */
@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
}
