package com.empmanage.platform

import androidx.compose.runtime.Composable

/**
 * Without Jetpack Navigation there's no back-stack to intercept the system
 * back button automatically — confirmed via a real device run: pressing
 * back while on the Form/Detail screen exited the whole app instead of
 * returning to the list, since a single-Activity app finishes by default
 * unless something intercepts back.
 */
@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)
