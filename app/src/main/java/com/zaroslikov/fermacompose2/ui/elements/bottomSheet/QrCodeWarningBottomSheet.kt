package com.zaroslikov.fermacompose2.ui.elements.bottomSheet

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.icon.IconBorder
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16

enum class QrCodeWarningType {
    GLOBAL, LOCAL
}

@Composable
fun QrCodeWarningBottomSheet(
    backupData: DomainTemplateTable? = null,
    qrCodeWarningType: QrCodeWarningType,
    onDismissRequest: () -> Unit,
    onScannerClick: () -> Unit,
    onRecoverClick: (DomainTemplateTable) -> Unit = {}
) {
    BaseBottomSheet(
        title = stringResource(R.string.base_section_warning),
        onDismissRequest = onDismissRequest,
        contentBottom = {
            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.button_close),
                onClick = onDismissRequest
            )
        }
    ) {
        when (qrCodeWarningType) {
            QrCodeWarningType.GLOBAL -> WarningCard(
                titleRes = R.string.warning_qr_code_bottom_sheet_no_search_project,
                supportTitleRes = R.string.warning_qr_code_bottom_sheet_no_search_project_support
            ) { onScannerClick() }

            QrCodeWarningType.LOCAL -> {
                val (supportTitleRes, onRecoverClick) = if (backupData == null)
                    R.string.warning_qr_code_bottom_sheet_no_search_template_support_no_backup_data to
                            null
                else
                    R.string.warning_qr_code_bottom_sheet_no_search_template_support to
                            { onRecoverClick(backupData) }
                WarningCard(
                    titleRes = R.string.warning_qr_code_bottom_sheet_no_search_template,
                    supportTitleRes = supportTitleRes,
                    onRecoverClick = onRecoverClick,
                    onScannerClick = onScannerClick
                )
            }
        }
    }
}

@Composable
private fun WarningCard(
    @StringRes titleRes: Int,
    @StringRes supportTitleRes: Int,
    onRecoverClick: (() -> Unit)? = null,
    onScannerClick: () -> Unit
) {
    BorderCard(
        containerColor = ghostly_white,
        borderColor = grey_2
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconBorder(
                iconRes = R.drawable.outline_qr_code_24,
                iconColor = grey,
                iconSize = 32.dp,
                borderColor = grey_2,
                containerColor = gray_6,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(titleRes),
                    style = text_16,
                    color = black_1,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    stringResource(supportTitleRes),
                    style = text_14,
                    color = gray_7,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BorderButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    text = stringResource(
                        if (onRecoverClick != null) R.string.button_scanner_again
                        else R.string.button_try_again
                    ),
                    onClick = onScannerClick
                )
                if (onRecoverClick != null)
                    GradientButton(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = listOf(grey, grey),
                        text = stringResource(R.string.button_recover),
                        onClick = onRecoverClick
                    )
            }
        }
    }
}