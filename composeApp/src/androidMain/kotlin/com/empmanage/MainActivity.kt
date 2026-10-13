package com.empmanage

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.empmanage.di.commonModules
import com.empmanage.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EmpManageApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EmpManageApplication)
            modules(commonModules + platformModule())
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}
