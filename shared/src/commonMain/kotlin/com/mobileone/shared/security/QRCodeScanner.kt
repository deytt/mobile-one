package com.mobileone.shared.security

/**
 * Contrato para leitura de QR Code via câmera nativa (SPEC-003).
 * Definido como interface (em vez de expect/actual) para permitir fakes em testes —
 * mesmo padrão de [BiometricAuthenticator]. Implementações nativas:
 * - Android: `AndroidQRCodeScanner` (ML Kit Barcode / ZXing)
 * - iOS: `IosQRCodeScanner` (AVFoundation + Vision)
 */
interface QRCodeScanner {
    /**
     * Abre a câmera nativa e retorna o payload do primeiro QR Code escaneado.
     * @return Success com o payload bruto (string) ou Failure com mensagem de erro.
     */
    suspend fun scan(): Result<String>
}
