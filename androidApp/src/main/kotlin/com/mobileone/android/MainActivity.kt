package com.mobileone.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mobileone.android.ui.screen.HelloScreen
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.shared.config.AppStateRepository
import com.mobileone.shared.platform.Platform
import org.koin.android.ext.android.getKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appStateRepository = getKoin().get<AppStateRepository>()
        setContent {
            val config by appStateRepository.currentConfig.collectAsState()
            BankTheme(config = config) {
                HelloScreen(platformName = Platform().name)
            }
        }
    }
}
