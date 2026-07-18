package com.mobileone.shared.platform

import android.os.Build

actual class Platform actual constructor() {
    actual val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun currentEpochSeconds(): Long = System.currentTimeMillis() / 1000L
