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
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.CloseButton
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextBuyerNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.WarehouseCountCard
import com.zaroslikov.fermacompose2.ui.elements.сompositions.card.InfoPatternCard
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddProductState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateFieldsState
import io.appmetrica.analytics.AppMetrica

@Composable
fun EnterInPatternBottomSheet(
    colors: List<Color>,
    addProductState: AddProductState,
    onDismissRequest: () -> Unit,
    onTitleChange: (String) -> Unit,
    onTitleAndSuffix: (Pair<String, Suffix>) -> Unit,
    onCountChange: (String) -> Unit,
    onSuffixChange: (Suffix) -> Unit,
    onCategoryChange: (String) -> Unit,
    onBuyerChange: (String) -> Unit = {},
    onAnimalChange: (Pair<Long, String>) -> Unit = {},
    onAnimalClearChange: (String) -> Unit = {},
    onNoteChange: (String) -> Unit,
    onInsertAndScannerAgain: () -> Unit,
    onInsertClick: () -> Unit
) {
    val product = addProductState.product
    BaseBottomSheet(
        title = addProductState.template.name,
        colors = colors,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val template = addProductState.template.activeField
            InfoPatternCard(
                title = if (template.isTitle) null else product.title,
                count = if (template.isCount) null else product.count,
                countSuffix = if (template.isSuffix) null else product.countSuffix,
                category = if (template.isCategory) null else product.category,
                note = if (template.isNote) null else product.note,
                buyer = if (template.isBuyer) null else product.animalName,//todo заменить на покупателя
                animalName = if (template.isAnimal) null else product.animalName
            )
            WarehouseCountCard(
                title = product.title,
                warehouseList = addProductState.pickList.warehouseList
            )
            Field(
                state = addProductState,
                template = addProductState.template.activeField,
                onTitleChange = onTitleChange,
                onTitleAndSuffix = onTitleAndSuffix,
                onCountChange = onCountChange,
                onSuffixChange = onSuffixChange,
                onCategoryChange = onCategoryChange,
                onAnimalChange = onAnimalChange,
                onAnimalClearChange = onAnimalClearChange,
                onBuyerChange = onBuyerChange,
                onNoteChange = onNoteChange
            )
            ButtonPanel(
                colors = colors, enabled = addProductState.errors.hasAnyError,
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
private fun Field(
    state: AddProductState,
    template: TemplateFieldsState,
    onTitleChange: (String) -> Unit,
    onTitleAndSuffix: (Pair<String, Suffix>) -> Unit,
    onCountChange: (String) -> Unit,
    onSuffixChange: (Suffix) -> Unit,
    onCategoryChange: (String) -> Unit,
    onAnimalChange: (Pair<Long, String>) -> Unit,
    onAnimalClearChange: (String) -> Unit,
    onBuyerChange: (String) -> Unit,
    onNoteChange: (String) -> Unit
) {
    val focusRequester =
        remember { FocusRequester() } // ✅ нужно помнить, иначе при recomposition фокус сбрасывается
    // ✅ Важно: просим фокус, когда bottom sheet появился
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    var focusAssigned by rememberSaveable { mutableStateOf(false) }
    fun takeFocusRequester(): FocusRequester? =
        if (!focusAssigned) {
            focusAssigned = true
            focusRequester
        } else null


    val product = state.product
    if (template.isTitle)
        OutlinedTextTitleAddNew(
            value = product.title,
            onValueChange = onTitleChange,
            onValueChangeSuffix = onTitleAndSuffix,
            titleList = state.pickList.titles,
            isErrorTitle = state.errors.isErrorTitle,
            isErrorSlash = state.errors.isErrorSlash,
            drawableRes = R.drawable.icon_add_product,
            focusRequester = takeFocusRequester(), fontSize = 36.sp
        )
    if (template.isCount || template.isSuffix)
        OutlinedTextCountNew(
            value = if (template.isCount) product.count else null,
            onValueChange = onCountChange,
            suffix = if (template.isSuffix) product.countSuffix else null,
            onSuffixChange = onSuffixChange,
            isError = state.errors.isErrorCount,
            intResSup = R.string.support_text_count_product,
            focusRequester = takeFocusRequester(), fontSize = 36.sp
        )
    if (template.isCategory)
        OutlinedTextCategoryNew(
            value = product.category,
            onValueChange = onCategoryChange,
            titleList = state.pickList.categories,
            focusRequester = takeFocusRequester(), fontSize = 36.sp
        )
    if (template.isAnimal && state.pickList.animals.isNotEmpty())
        OutlinedTextAnimalNew(
            value = product.animalName,
            onValueChange = onAnimalChange,
            selectedAnimalIndex = product.selectedAnimalIndex,
            onClickClear = onAnimalClearChange,
            animalList = state.pickList.animals,
            focusRequester = takeFocusRequester(), fontSize = 36.sp
        )
    if (template.isBuyer)
        OutlinedTextBuyerNew(
            value = product.animalName,
            onValueChange = onBuyerChange,
            focusRequester = takeFocusRequester(), fontSize = 36.sp,
            list = emptyList()  // TODO
        )
    if (template.isNote)
        OutlinedTextNoteNew(
            value = product.note,
            onValueChange = onNoteChange,
            focusRequester = takeFocusRequester(), fontSize = 36.sp
        )
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
