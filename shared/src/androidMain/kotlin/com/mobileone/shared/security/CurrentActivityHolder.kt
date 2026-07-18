package com.mobileone.shared.security

import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

/**
 * Referência fraca para a `FragmentActivity` em primeiro plano — necessária porque
 * `BiometricPrompt` (ADR-005) exige uma Activity real, enquanto o [AndroidBiometricAuthenticator]
 * é resolvido pelo Koin com escopo de aplicação (sem Activity). Atualizada pelo `MainActivity`
 * do androidApp em `onResume`/`onPause`; trade-off já documentado no ADR-005
 * ("expect/actual com dependência de contexto Android requer injeção cuidadosa no Koin").
 */
object CurrentActivityHolder {
    private var ref: WeakReference<FragmentActivity>? = null

    var activity: FragmentActivity?
        get() = ref?.get()
        set(value) {
            ref = value?.let { WeakReference(it) }
        }
}
