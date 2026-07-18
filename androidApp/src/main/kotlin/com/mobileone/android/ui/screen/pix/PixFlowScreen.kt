package com.mobileone.android.ui.screen.pix

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobileone.android.viewmodel.PixViewModel
import com.mobileone.shared.feature.pix.PixStep
import org.koin.androidx.compose.koinViewModel

/**
 * Tela stateful do fluxo PIX (SPEC-003). Observa o [PixViewModel] e delega
 * a renderização para a sub-tela correspondente ao step atual.
 */
@Composable
fun PixFlowScreen(
    onClose: () -> Unit,
    viewModel: PixViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (uiState.step) {
        PixStep.EnterKey -> EnterKeyScreen(
            uiState = uiState,
            onKeyChanged = viewModel::onKeyChanged,
            onContinue = viewModel::onContinueFromKey,
            onScanQRCode = viewModel::onScanQRCode,
            onBack = {
                viewModel.onReset()
                onClose()
            },
            modifier = modifier
        )
        PixStep.ConfirmRecipient -> ConfirmRecipientScreen(
            uiState = uiState,
            onConfirm = viewModel::onConfirmRecipient,
            onReject = viewModel::onRejectRecipient,
            onBack = viewModel::onRejectRecipient,
            modifier = modifier
        )
        PixStep.EnterAmount -> EnterAmountScreen(
            uiState = uiState,
            onAmountChanged = viewModel::onAmountChanged,
            onDescriptionChanged = viewModel::onDescriptionChanged,
            onContinue = viewModel::onContinueFromAmount,
            onBack = { viewModel.onAmountChanged(0L) },
            modifier = modifier
        )
        PixStep.Review -> ReviewScreen(
            uiState = uiState,
            onConfirm = viewModel::onConfirmTransfer,
            onBack = { /* volta para EnterAmount — manter estado */ },
            modifier = modifier
        )
        PixStep.Processing -> ProcessingScreen(modifier = modifier)
        PixStep.Receipt -> ReceiptScreen(
            uiState = uiState,
            onShare = {
                val e2eId = uiState.receipt?.e2eId ?: return@ReceiptScreen
                val text = "Comprovante PIX\n${uiState.receipt?.amountFormatted}\nE2E: $e2eId"
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                context.startActivity(Intent.createChooser(intent, "Compartilhar comprovante"))
            },
            onClose = {
                viewModel.onReset()
                onClose()
            },
            modifier = modifier
        )
    }
}
