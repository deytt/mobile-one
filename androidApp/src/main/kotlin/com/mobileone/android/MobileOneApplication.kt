package com.mobileone.android

import android.app.Application
import com.mobileone.android.di.androidAppModule
import com.mobileone.shared.di.initKoin
import org.koin.android.ext.koin.androidContext

class MobileOneApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MobileOneApplication)
            modules(androidAppModule)
        }
    }
}
