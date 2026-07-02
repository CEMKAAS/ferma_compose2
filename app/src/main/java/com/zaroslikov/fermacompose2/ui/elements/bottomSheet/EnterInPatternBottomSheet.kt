package com.zaroslikov.fermacompose2.ui.elements.bottomSheet

import androidx.annotation.StringRes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddListIntent

@Composable
fun EnterInPatternBottomSheet(
    colors: List<Color>,
    onDismissRequest: () -> Unit,
) {
    BaseBottomSheet(
        title = stringResource(R.string.pattern_bottom_sheet_title),
        onDismissRequest = onDismissRequest,
        contentBottom = {}
    ) {
        InfoPatternCard()
        Field()
    }
}


@Composable
private fun Field() {
    /*OutlinedTextTitleAddNew(
        value = state.title,
        onValueChange = { onIntent(AddListIntent.TitleChanged(it)) },
        onValueChangeSuffix = { onIntent(AddListIntent.TitleAndSuffix(it)) },
        titleList = state.pickList.titleList,
        isErrorTitle = state.error.isErrorTitle,
        isErrorSlash = state.error.isErrorSlash,
        drawableRes = R.drawable.icon_add_product,

        isShowSwitch = true,
        checked = true,
        onCheckedChange = {},
    )
    OutlinedTextCountNew(
        value = state.count,
        onValueChange = {
            onIntent(AddListIntent.CountChanged(it))
        },
        suffix = state.countSuffix,
        onSuffixChange = { onIntent(AddListIntent.SuffixClicked(it)) },
        isError = state.error.isErrorCount,
        intResSup = R.string.support_text_count_product,

        isShowSwitchForValue = true,
        checkedForValue = true,
        onCheckedForValueChange = {},

        isShowSwitchForSuffix = true,
        checkedForSuffix = true,
        onCheckedForSuffixChange = {}
    )
    OutlinedTextCategoryNew(
        value = state.category,
        onValueChange = { onIntent(AddListIntent.CategoryChanged(it)) },
        titleList = state.pickList.categoryList,
        isShowSwitch = true,
        checked = true,
        onCheckedChange = {},
    )
    OutlinedTextAnimalNew(
        value = state.animal,
        onValueChange = { onIntent(AddListIntent.Animal(it)) },
        selectedAnimalIndex = state.selectedAnimalIndex,
        onClickClear = { onIntent(AddListIntent.AnimalClear(it)) },
        animalList = state.pickList.animalList,

        isShowSwitch = true,
        checked = true,
        onCheckedChange = {},
    )
    OutlinedTextNoteNew(
        value = state.note,
        onValueChange = { onIntent(AddListIntent.NoteChanged(it)) },

        isShowSwitch = true,
        checked = true,
        onCheckedChange = {},
    )*/
}

@Composable
private fun InfoPatternCard() {
    CardFieldNew(
        elevation = 0.dp,
        containerColor = ghostly_white
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.is_empty),
                style = text_12,
                lineHeight = 16.sp,
                color = grey
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RowV(R.string.is_empty, "2", R.string.is_empty, "2")
                RowV(R.string.is_empty, "2", R.string.is_empty, "2")
                RowV(R.string.is_empty, "2", R.string.is_empty, "2")
            }
        }
    }
}

@Composable
private fun RowV(
    @StringRes oneTitleRes: Int,
    oneValue: String,
    @StringRes secondTitleRes: Int,
    secondValue: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleWithValue(modifier = Modifier.weight(1f), titleRes = oneTitleRes, value = oneValue)
        TitleWithValue(
            modifier = Modifier.weight(1f),
            titleRes = secondTitleRes,
            value = secondValue
        )
    }
}

@Composable
private fun TitleWithValue(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    value: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(stringResource(titleRes), style = text_12, lineHeight = 16.sp, color = grey)
        Text(value, style = text_14, lineHeight = 20.sp, color = black_2)
    }
}
