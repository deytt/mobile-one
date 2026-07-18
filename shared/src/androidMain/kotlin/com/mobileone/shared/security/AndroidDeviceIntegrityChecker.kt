package com.mobileone.shared.security

import android.os.Build
import android.os.Debug
import java.io.File

/**
 * Checagem simples de root/emulador — sem libs externas (ex: RootBeer), decisão registrada na
 * SPEC-001/ADR-005. Suficiente para a POC; não substitui uma solução anti-fraude completa em
 * produção.
 */
class AndroidDeviceIntegrityChecker : DeviceIntegrityChecker {

    override suspend fun check(): IntegrityStatus = IntegrityStatus(
        isRooted = hasRootIndicators(),
        isEmulator = isProbablyEmulator(),
        isDebuggable = Debug.isDebuggerConnected()
    )

    private fun hasRootIndicators(): Boolean {
        val buildTagsSuspicious = Build.TAGS?.contains("test-keys") == true
        val suBinaryPresent = SU_PATHS.any { File(it).exists() }
        return buildTagsSuspicious || suBinaryPresent
    }

    private fun isProbablyEmulator(): Boolean =
        Build.FINGERPRINT.startsWith("generic") ||
            Build.MODEL.contains("Emulator", ignoreCase = true) ||
            Build.MANUFACTURER.contains("Genymotion", ignoreCase = true) ||
            (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))

    companion object {
        private val SU_PATHS = listOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/app/Superuser.apk"
        )
    }
}
