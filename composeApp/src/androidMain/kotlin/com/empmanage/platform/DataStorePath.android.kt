package com.empmanage.platform

actual fun themeDataStorePath(): String =
    "${AndroidAppContext.instance.filesDir.path}/theme_prefs.preferences_pb"
