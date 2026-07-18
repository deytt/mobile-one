package com.mobileone.shared.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIDevice
import platform.posix.time

actual class Platform actual constructor() {
    actual val name: String =
        "${UIDevice.currentDevice.systemName()} ${UIDevice.currentDevice.systemVersion}"
}

@OptIn(ExperimentalForeignApi::class)
actual fun currentEpochSeconds(): Long = time(null)
