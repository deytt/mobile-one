package com.mobileone.shared.security

import platform.Foundation.NSFileManager
import platform.Foundation.NSProcessInfo

/**
 * Checagem simples de jailbreak — sem libs externas, decisão registrada na SPEC-001/ADR-005.
 */
class IosDeviceIntegrityChecker : DeviceIntegrityChecker {

    override suspend fun check(): IntegrityStatus = IntegrityStatus(
        isRooted = hasJailbreakIndicators(),
        isEmulator = isSimulator(),
        isDebuggable = false
    )

    private fun hasJailbreakIndicators(): Boolean {
        val fileManager = NSFileManager.defaultManager
        return JAILBREAK_PATHS.any { fileManager.fileExistsAtPath(it) }
    }

    /** Detecção padrão de Simulator via variável de ambiente definida pelo Xcode. */
    private fun isSimulator(): Boolean =
        NSProcessInfo.processInfo.environment["SIMULATOR_DEVICE_NAME"] != null

    companion object {
        private val JAILBREAK_PATHS = listOf(
            "/Applications/Cydia.app",
            "/Library/MobileSubstrate/MobileSubstrate.dylib",
            "/bin/bash",
            "/usr/sbin/sshd",
            "/etc/apt"
        )
    }
}
