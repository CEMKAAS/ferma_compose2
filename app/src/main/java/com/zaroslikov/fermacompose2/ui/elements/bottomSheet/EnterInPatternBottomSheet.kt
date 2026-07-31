package com.zaroslikov.fermacompose2.ui.elements.bottomSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.state.BaseProductState
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.CloseButton
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextBuyerNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.WarehouseCountCard
import com.zaroslikov.fermacompose2.ui.elements.сompositions.card.InfoPatternCard
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateFieldsState
import io.appmetrica.analytics.AppMetrica

@Composable
fun BaseEnterInPatternBottomSheet(
    colors: List<Color>,
    name: String?,
    hasAnyError: Boolean,
    onDismissRequest: () -> Unit,
    onInsertAndScannerAgain: () -> Unit,
    onInsertClick: () -> Unit,
    fields: @Composable () -> Unit
) {
    BaseBottomSheet(
        title = name,
        colors = colors,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            fields()
            ButtonPanel(
                colors = colors, enabled = hasAnyError,
                onInsertAndScannerAgain = {
                    onInsertAndScannerAgain()
                    AppMetrica.reportEvent("Открытие камеры и сканирование следующий QR-код")
                },
                onDismissRequest = onDismissRequest
            ) { onInsertClick() }
        }
    }
}

@Composable
private fun ButtonPanel(
    colors: List<Color>,
    enabled: Boolean,
    onDismissRequest: () -> Unit,
    onInsertAndScannerAgain: () -> Unit,
    onInsertClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GradientButton(
            text = stringResource(R.string.button_add_and_scanner_again),
            enabled = enabled,
            prefixIconRes = R.drawable.outline_qr_code_scanner_24,
            onClick = onInsertAndScannerAgain,
            modifier = Modifier.fillMaxWidth(),
            colors = colors
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CloseButton(
                text = R.string.button_cancel,
                onClick = onDismissRequest,
                modifier = Modifier.weight(1f)
            )
            GradientButton(
                text = stringResource(R.string.button_add),
                enabled = enabled,
                onClick = onInsertClick,
                modifier = Modifier.weight(1f),
                colors = colors
            )
        }
    }
}
