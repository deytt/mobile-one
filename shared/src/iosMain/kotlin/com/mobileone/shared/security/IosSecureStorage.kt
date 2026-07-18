@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@file:Suppress("CAST_NEVER_SUCCEEDS")

package com.mobileone.shared.security

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecItemNotFound
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.darwin.OSStatus

/**
 * Persistência segura via Keychain Services (`kSecClassGenericPassword`) — ADR-005. Baseado no
 * padrão consolidado da lib `russhwolf/multiplatform-settings` (`KeychainSettings`) para uso de
 * `kotlinx.cinterop` com `SecItemAdd`/`SecItemCopyMatching`/`SecItemUpdate`/`SecItemDelete`.
 */
class IosSecureStorage : SecureStorage {

    override suspend fun put(key: String, value: String) {
        val data = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding)
        if (hasItem(key)) updateItem(key, data) else addItem(key, data)
    }

    override suspend fun get(key: String): String? = cfRetain(key) { (cfKey) ->
        val result = alloc<CFTypeRefVar>()
        val status = keychainOperation(
            kSecAttrAccount to cfKey,
            kSecReturnData to kCFBooleanTrue,
            kSecMatchLimit to kSecMatchLimitOne
        ) { SecItemCopyMatching(it, result.ptr) }
        if (status == errSecItemNotFound) return@cfRetain null
        val nsData = CFBridgingRelease(result.value) as? NSData ?: return@cfRetain null
        NSString.create(nsData, NSUTF8StringEncoding)?.let { it as String }
    }

    override suspend fun delete(key: String) {
        cfRetain(key) { (cfKey) ->
            keychainOperation(kSecAttrAccount to cfKey) { SecItemDelete(it) }
        }
    }

    override suspend fun clear() {
        AuthSecureStorageKeys.ALL.forEach { delete(it) }
    }

    private fun addItem(key: String, value: NSData?) = cfRetain(key, value) { (cfKey, cfValue) ->
        keychainOperation(
            kSecAttrAccount to cfKey,
            kSecValueData to cfValue
        ) { SecItemAdd(it, null) }
    }

    private fun updateItem(key: String, value: NSData?) = cfRetain(key, value) { (cfKey, cfValue) ->
        keychainOperation(kSecAttrAccount to cfKey) {
            SecItemUpdate(it, cfDictionaryOf(kSecValueData to cfValue))
        }
    }

    private fun hasItem(key: String): Boolean = cfRetain(key) { (cfKey) ->
        val status = keychainOperation(
            kSecAttrAccount to cfKey,
            kSecMatchLimit to kSecMatchLimitOne
        ) { SecItemCopyMatching(it, null) }
        status != errSecItemNotFound
    }

    private fun MemScope.keychainOperation(
        vararg input: Pair<CFStringRef?, CFTypeRef?>,
        operation: (query: CFDictionaryRef?) -> OSStatus
    ): OSStatus {
        val defaults = mapOf(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to serviceNameRef
        )
        return operation(cfDictionaryOf(defaults + mapOf(*input)))
    }

    companion object {
        private const val SERVICE_NAME = "com.mobileone.shared.auth"
        private val serviceNameRef: CFTypeRef? = CFBridgingRetain(SERVICE_NAME)
    }
}

private fun MemScope.cfDictionaryOf(vararg items: Pair<CFStringRef?, CFTypeRef?>): CFDictionaryRef? =
    cfDictionaryOf(mapOf(*items))

private fun MemScope.cfDictionaryOf(map: Map<CFStringRef?, CFTypeRef?>): CFDictionaryRef? {
    val keys = allocArrayOf(*map.keys.toTypedArray())
    val values = allocArrayOf(*map.values.toTypedArray())
    return CFDictionaryCreate(
        kCFAllocatorDefault,
        keys.reinterpret(),
        values.reinterpret(),
        map.size.convert(),
        null,
        null
    )
}

private inline fun <T> cfRetain(vararg values: Any?, block: MemScope.(Array<CFTypeRef?>) -> T): T = memScoped {
    val cfValues = Array(values.size) { i -> CFBridgingRetain(values[i]) }
    try {
        block(cfValues)
    } finally {
        cfValues.forEach { CFBridgingRelease(it) }
    }
}
