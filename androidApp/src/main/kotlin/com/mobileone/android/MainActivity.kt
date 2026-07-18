package com.mobileone.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import com.mobileone.android.ui.navigation.MobileOneNavHost
import com.mobileone.android.ui.theme.BankTheme
import com.mobileone.shared.config.AppStateRepository
import com.mobileone.shared.security.CurrentActivityHolder
import org.koin.android.ext.android.getKoin

/**
 * Estende [FragmentActivity] (não `ComponentActivity`) porque `BiometricPrompt` (ADR-005)
 * exige um `FragmentActivity` como host — ver trade-off documentado no ADR-005.
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val appStateRepository = getKoin().get<AppStateRepository>()
        setContent {
            val config by appStateRepository.currentConfig.collectAsState()
            BankTheme(config = config) {
                MobileOneNavHost()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        CurrentActivityHolder.activity = this
    }

    override fun onPause() {
        if (CurrentActivityHolder.activity === this) {
            CurrentActivityHolder.activity = null
        }
        super.onPause()
    }
}
