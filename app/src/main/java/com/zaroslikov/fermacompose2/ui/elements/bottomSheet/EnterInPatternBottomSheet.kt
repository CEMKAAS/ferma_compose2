package com.zaroslikov.fermacompose2.ui.elements.bottomSheet

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.CloseButton
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData2
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.сompositions.card.InfoPatternCard
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddEntryState2

@Composable
fun EnterInPatternBottomSheet(
    colors: List<Color>,
    addEntryState2: AddEntryState2,
    onDismissRequest: () -> Unit,
    onTitleChange: (String) -> Unit,
    onTitleAndSuffix: (Pair<String, Suffix>) -> Unit,
    onCountChange: (String) -> Unit,
    onSuffixChange: (Suffix) -> Unit,
    onCategoryChange: (String) -> Unit,
    onAnimalChange: (Pair<Long, String>) -> Unit,
    onAnimalClearChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onInsertClick: () -> Unit
) {
    BaseBottomSheet(
        title = addEntryState2.nameTemplate,
        colors = colors,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val template = addEntryState2.templateEntryState
            InfoPatternCard(
                title = if (template.isTitle) null else addEntryState2.title,
                count = if (template.isCount) null else addEntryState2.count,
                countSuffix = if (template.isSuffix) null else addEntryState2.countSuffix,
                category = if (template.isCategory) null else addEntryState2.category,
                note = if (template.isNote) null else addEntryState2.note
            )
            Field(
                state = addEntryState2,
                onTitleChange = onTitleChange,
                onTitleAndSuffix = onTitleAndSuffix,
                onCountChange = onCountChange,
                onSuffixChange = onSuffixChange,
                onCategoryChange = onCategoryChange,
                onAnimalChange = onAnimalChange,
                onAnimalClearChange = onAnimalClearChange,
                onNoteChange = onNoteChange
            )
            ButtonPanel(
                colors = colors, enabled = addEntryState2.hasAnyError,
                onDismissRequest = onDismissRequest
            ) { onInsertClick() }
        }
    }
}


@Composable
private fun Field(
    state: AddEntryState2,
    onTitleChange: (String) -> Unit,
    onTitleAndSuffix: (Pair<String, Suffix>) -> Unit,
    onCountChange: (String) -> Unit,
    onSuffixChange: (Suffix) -> Unit,
    onCategoryChange: (String) -> Unit,
    onAnimalChange: (Pair<Long, String>) -> Unit,
    onAnimalClearChange: (String) -> Unit,
    onNoteChange: (String) -> Unit
) {
    val template = state.templateEntryState
    if (template.isTitle)
        OutlinedTextTitleAddNew(
            value = state.title,
            onValueChange = onTitleChange,
            onValueChangeSuffix = onTitleAndSuffix,
            titleList = state.pickList.titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash,
            drawableRes = R.drawable.icon_add_product
        )
    if (template.isCount || template.isSuffix)
        OutlinedTextCountNew(
            value = if (template.isCount) state.count else null,
            onValueChange = onCountChange,
            suffix = if (template.isSuffix) state.countSuffix else null,
            onSuffixChange = onSuffixChange,
            isError = state.error.isErrorCount,
            intResSup = R.string.support_text_count_product,
        )
    if (template.isCategory)
        OutlinedTextCategoryNew(
            value = state.category,
            onValueChange = onCategoryChange,
            titleList = state.pickList.categoryList,
        )
    if (template.isAnimal && state.pickList.animalList.isNotEmpty())
        OutlinedTextAnimalNew(
            value = state.animal,
            onValueChange = onAnimalChange,
            selectedAnimalIndex = state.selectedAnimalIndex,
            onClickClear = onAnimalClearChange,
            animalList = state.pickList.animalList,
        )
    if (template.isNote)
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = onNoteChange,
        )
}

@Composable
private fun ButtonPanel(
    colors: List<Color>,
    enabled: Boolean,
    onDismissRequest: () -> Unit,
    onInsertClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 16.dp),
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
