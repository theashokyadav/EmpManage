package com.empmanage.platform

actual fun themeDataStorePath(): String = "${documentsDirectory()}/theme_prefs.preferences_pb"
