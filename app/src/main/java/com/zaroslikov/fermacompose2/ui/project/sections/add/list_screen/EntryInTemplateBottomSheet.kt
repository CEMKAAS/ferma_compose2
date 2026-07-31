package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

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
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.WarehouseCountCard
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.BaseEnterInPatternBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.сompositions.card.InfoPatternCard
import kotlin.text.category

@Composable
fun EnterInPatternBottomSheet(
    colors: List<Color>,
    state: AddProductState,
    onDismissRequest: () -> Unit,
    onIntent: (AddListIntent) -> Unit,
    onInsertAndScannerAgain: () -> Unit,
    onInsertClick: () -> Unit
) {
    val template = state.template.activeField
    val product = state.product
    val errors = state.errors

    val isAllFieldsHidden =
        template.isTitle && template.isCount && template.isSuffix
                && template.isCategory && template.isNote && template.isAnimal
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
                category = if (template.isCategory) null else product.category,
                note = if (template.isNote || product.note.isBlank()) null else product.note,
                animalName = if (template.isAnimal) null else product.animalName.ifBlank { null },
            )

        WarehouseCountCard(
            title = product.title,
            warehouseList = state.pickList.warehouseList
        )
        if (template.isTitle)
            OutlinedTextTitleAddNew(
                value = product.title,
                onValueChange = { onIntent(AddListIntent.TitleChanged(it)) },
                onValueChangeSuffix = { onIntent(AddListIntent.TitleAndSuffix(it)) },
                titleList = state.pickList.titles,
                isErrorTitle = errors.isErrorTitle,
                isErrorSlash = errors.isErrorSlash,
                drawableRes = R.drawable.icon_add_product,
                focusRequester = takeFocusRequester(), fontSize = fontSize
            )
        if (template.isCount || template.isSuffix)
            OutlinedTextCountNew(
                value = if (template.isCount) product.count else null,
                onValueChange = { onIntent(AddListIntent.CountChanged(it)) },
                suffix = if (template.isSuffix) product.countSuffix else null,
                onSuffixChange = { onIntent(AddListIntent.SuffixClicked(it)) },
                isError = errors.isErrorCount,
                intResSup = R.string.support_text_count_product,
                focusRequester = takeFocusRequester(), fontSize = fontSize
            )
        if (template.isCategory)
            OutlinedTextCategoryNew(
                value = product.category,
                onValueChange = { onIntent(AddListIntent.CategoryChanged(it)) },
                titleList = state.pickList.categories,
                focusRequester = takeFocusRequester(), fontSize = fontSize
            )
        if (template.isDate)
            OutlinedTextDateNew(
                value = product.date,
                onValueChange = { onIntent(AddListIntent.Date(it)) },
                focusRequester = takeFocusRequester(),fontSize = fontSize
            )
        if (template.isAnimal && state.pickList.animals.isNotEmpty())
            OutlinedTextAnimalNew(
                value = product.animalName,
                onValueChange = { onIntent(AddListIntent.Animal(it)) },
                selectedAnimalIndex = product.selectedAnimalIndex,
                onClickClear = { onIntent(AddListIntent.AnimalClear(it)) },
                animalList = state.pickList.animals,
                focusRequester = takeFocusRequester(), fontSize = fontSize
            )
        if (template.isNote)
            OutlinedTextNoteNew(
                value = product.note,
                onValueChange = { onIntent(AddListIntent.NoteChanged(it)) },
                focusRequester = takeFocusRequester(), fontSize = fontSize
            )
    }
}