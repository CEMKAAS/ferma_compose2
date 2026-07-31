package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextBuyerNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleSaleNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedWriteOffStatus
import com.zaroslikov.fermacompose2.ui.elements.WarehouseCountCard
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.BaseEnterInPatternBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.сompositions.card.InfoPatternCard
import com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen.SaleListIntent
import com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen.SaleProductState

@Composable
fun WriteOffEnterInPatternBottomSheet(
    colors: List<Color>,
    state: WriteOffProductState,
    onDismissRequest: () -> Unit,
    onIntent: (WriteOffListIntent) -> Unit,
    onInsertAndScannerAgain: () -> Unit,
    onInsertClick: () -> Unit
) {
    val template = state.template.activeField
    val product = state.product
    val errors = state.errors
    val isAllFieldsHidden =
        template.isTitle && template.isCount && template.isSuffix && template.isPrice &&
                template.isPriceAll && template.isCategory && template.isNote && template.isWriteOffStatus
    val fontSize = if (isAllFieldsHidden) 16.sp else 36.sp
    BaseEnterInPatternBottomSheet(
        colors = colors,
        name = state.template.name,
        hasAnyError = errors.hasAnyError,
        onDismissRequest = onDismissRequest,
        onInsertAndScannerAgain = onInsertAndScannerAgain,
        onInsertClick = onInsertClick,
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

        if (!isAllFieldsHidden)
            InfoPatternCard(
                title = if (template.isTitle) null else product.title,
                count = if (template.isCount) null else product.count,
                countSuffix = if (template.isSuffix) null else product.countSuffix,
                price = if (template.isPrice || product.price == "0") null else product.price,
                priceAll = if (template.isPriceAll) null else product.priceAll,
                priceSuffix = product.priceSuffix,
                category = if (template.isCategory) null else product.category,
                note = if (template.isNote || product.note.isBlank()) null else product.note,
                writeOffStatus = if (template.isWriteOffStatus) null else product.status
            )
        WarehouseCountCard(
            title = product.title,
            warehouseList = state.pickList.warehouseList
        )
        if (template.isWriteOffStatus)
            OutlinedWriteOffStatus(
                value = product.status,
                onValueChange = { onIntent(WriteOffListIntent.StatusClicked(it)) },
                focusRequester = takeFocusRequester(), fontSize = fontSize
            )
        if (template.isTitle)
            OutlinedTextTitleSaleNew(
                value = product.title,
                onValueChoice = {
                    onIntent(
                        WriteOffListIntent.TitleAndSuffix(
                            it.title,
                            it.suffix,
                            it.productOrigin
                        )
                    )
                },
                productOrigin = product.productOrigin,
                titleList = state.pickList.titles,
                isErrorTitle = errors.isErrorTitle,
                isErrorSlash = errors.isErrorSlash,
                intResSup = R.string.support_text_price_write_product,
                isMore = true, readOnly = true,
                focusRequester = takeFocusRequester(), fontSize = fontSize
            )
        if (template.isCount || template.isSuffix)
            OutlinedTextCountNew(
                value = if (template.isCount) product.count else null,
                onValueChange = { onIntent(WriteOffListIntent.CountChanged(it)) },
                suffixList = state.pickList.suffixList,
                suffix = if (template.isSuffix) product.countSuffix else null,
                onSuffixChange = { onIntent(WriteOffListIntent.SuffixClicked(it)) },
                isError = errors.isErrorCount,
                intResSup = R.string.support_text_count_product_write_off,
                focusRequester = takeFocusRequester(), fontSize = fontSize
            )
        if (template.isPrice)
            OutlinedPriceInputNew(
                price = product.price,
                onPriceChange = {
                    onIntent(WriteOffListIntent.PriceChanged(it))
                },
                isAutoCalculate = product.isAutoPrice,
                onAutoCalculate = {
                    onIntent(WriteOffListIntent.AutoPriceClicked(it))
                },
                isManyCount = true,
                count = product.count,
                countSuffix = product.countSuffix,
                priceAll = product.priceAll,
                priceSuffix = product.priceSuffix,
                supportTextRes = R.string.support_text_price_write_off_all,
                supportTextResAutoCal = R.string.support_text_price_write_off_one,
                tooltipTextResAutoCal = R.string.expenses_entry_screen_auto_calculate,
                focusRequester = takeFocusRequester(), fontSize = fontSize
            )
        if (template.isCategory)
            OutlinedTextCategoryNew(
                value = product.category,
                onValueChange = { onIntent(WriteOffListIntent.CategoryChanged(it)) },
                titleList = state.pickList.categories,
                focusRequester = takeFocusRequester(), fontSize = fontSize
            )
        if (template.isDate)
            OutlinedTextDateNew(
                value = product.date,
                onValueChange = {
                    onIntent(WriteOffListIntent.DateClicked(it))
                },
                focusRequester = takeFocusRequester(), fontSize = fontSize
            )
        if (template.isNote)
            OutlinedTextNoteNew(
                value = product.note,
                onValueChange = { onIntent(WriteOffListIntent.NoteChanged(it)) },
                focusRequester = takeFocusRequester(), fontSize = fontSize
            )
    }
}