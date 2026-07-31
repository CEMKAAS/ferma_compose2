package com.zaroslikov.fermacompose2.base.intent

import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType

import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData


sealed class QrCodeIntent : BaseIntent {
    data class QrDetected(val value: String) : QrCodeIntent()
    data class RecoverClick(val value: DomainTemplateTable) : QrCodeIntent()
    data class CreateQrCodeClick(val value: Long) : QrCodeIntent()
    data class OpenScannerQrCodeBottomSheetClick(val value: Boolean) : QrCodeIntent()
    data class OpenWarningQrCodeBottomSheetClick(
        val value: Boolean,
        val qrCodeWarningType: QrCodeWarningType = QrCodeWarningType.LOCAL,
        val backupData: DomainTemplateTable? = null
    ) : QrCodeIntent()

    data class OpenQrCodeBottomSheetClick(
        val value: Boolean,
        val qrCode: QrCodeData? = null
    ) : QrCodeIntent()
}