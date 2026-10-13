package com.empmanage.platform

import android.content.Context

/**
 * Captured once in [com.empmanage.EmpManageApplication.onCreate], before
 * Koin starts, so platform actuals (DB builder, future camera/file
 * pickers) can reach an application Context without it being threaded
 * through every constructor.
 */
object AndroidAppContext {
    lateinit var instance: Context
}
