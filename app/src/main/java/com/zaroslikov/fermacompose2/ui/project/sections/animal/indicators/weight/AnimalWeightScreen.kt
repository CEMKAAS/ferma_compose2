@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.weight

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.list.suffixWeightList
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_2
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_4
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.EntryIndicationBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.AnimalIndicatorsCardNew
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.AnimalIndicatorsDeleteCard
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.InventoryAnimalBody


object AnimalWeightDestination : NavigationDestination {
    override val route = "animalWeight"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

@Composable
fun AnimalWeightScreen(
    navigateBack: () -> Unit,
    viewModel: AnimalWeightViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(blue_4, blue_2)
    val icon = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24
    val title = R.string.weight_screen_title

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = title,
                scrollBehavior = scrollBehavior,
                onNavigateBackClick = navigateBack
            )
        },
        floatingActionButton = {
            if (!state.isArchive)
                NeonGlowFab(colors = colors) {
                    viewModel.onIntent(AnimalWeightIntent.OpenDialogClicked(true))
                }
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            AnimalWeightContainer2(
                modifier = Modifier.modifierScreenLazy(innerPadding),
                icon = icon,
                colors = colors,
                itemList = state.weightList,
                isArchive = state.isArchive,
                onInsertClick = {
                    viewModel.onIntent(AnimalWeightIntent.OpenDialogClicked(true))
                },
                onEditClick = {
                    viewModel.onIntent(
                        AnimalWeightIntent.OpenDialogClicked(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(AnimalWeightIntent.OpenBottomSheetDelete(it)) },
                titleRes = title
            )
        if (state.isOpenEntryBottomSheet)
            WeightBottomSheet(
                state = state.currentProduct,
                onIntent = viewModel::onIntent,
                colors = colors,
                icon = icon,
                titleRes = title,
            )
        if (state.isOpenBottomSheetDelete)
            WarningDeleteWeightBottomSheet(
                onDismissRequest = {
                    viewModel.onIntent(AnimalWeightIntent.OpenBottomSheetDelete(null))
                },
                onDeleteClick = { viewModel.onIntent(AnimalWeightIntent.DeletePressed) },
                color = colors.first(),
                state = state.deleteWeight
            )
    }
}

@Composable
private fun AnimalWeightContainer2(
    modifier: Modifier,
    @DrawableRes icon: Int,
    @StringRes titleRes: Int,
    colors: List<Color>,
    itemList: List<AnimalWeightUi>,
    onInsertClick: () -> Unit,
    onEditClick: (AnimalWeightUi) -> Unit,
    onDeleteClick: (Long) -> Unit,
    isArchive: Boolean,
) {
    InventoryAnimalBody(
        modifier = modifier,
        itemList = itemList,
        titleRes2 = titleRes,
        titleRes = R.string.message_no_date_title_weight,
        messageRes = R.string.message_no_date_message_weight,
        iconRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
        iconColor = blue_1,
        backgroundColor = blue_3,
        isArchive = isArchive,
        detailCard = { item ->
            AnimalIndicatorsCardNew(
                icon = icon,
                colors = colors,
                value = item.weight.toFormatNumber(),
                suffix = item.suffix,
                date = item.date,
                note = item.note,
                totalValues = item.totalValue,
                indicationStatus = item.indicationStatus,
                isArchive = isArchive,
                onEditClick = { onEditClick(item) },
                onDeleteClick = { onDeleteClick(item.id) },
            )
        }
    )
}

@Composable
private fun WarningDeleteWeightBottomSheet(
    color: Color,
    state: AnimalWeightUi?,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit
) {
    WarningDeleteBottomSheet(
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick,
        titleRes = R.string.base_section_delete_weight,
        supportRes = R.string.base_section_delete_support_weight,
        textRes = R.string.base_section_warning_weight,
        textButtonRes = R.string.base_section_button_delete_weight
    ) {
        state?.let {
            AnimalIndicatorsDeleteCard(
                value = state.weight.toFormatNumber(),
                suffix = state.suffix,
                date = state.date,
                note = state.note,
                color = color
            )
        }
    }
}

@Composable
private fun WeightBottomSheet(
    state: CurrentAnimalWeight,
    colors: List<Color>,
    @DrawableRes icon: Int,
    @StringRes titleRes: Int,
    onIntent: (AnimalWeightIntent) -> Unit
) {
    EntryIndicationBottomSheet(
        isEntry = state.isEntry,
        enabledButton = state.hasAnyError,
        colors = colors,
        onDismissRequest = {
            onIntent(
                AnimalWeightIntent.OpenDialogClicked(
                    false,
                    isSaveStateForBottomSheet = state.isEntry
                )
            )
        },
        onSecondDismissRequest = {
            onIntent(AnimalWeightIntent.OpenDialogClicked(false))
        },
        onInsertClick = { onIntent(AnimalWeightIntent.InsertPressed) },
        onUpdateClick = { onIntent(AnimalWeightIntent.UpdatePressed) },
        iconRes = icon,
        titleRes = titleRes
    ) {
        OutlinedTextCountNew(
            value = state.weight,
            onValueChange = {
                onIntent(AnimalWeightIntent.WeightChanged(it))
            },
            suffix = state.suffix,
            onSuffixChange = {
                onIntent(AnimalWeightIntent.SuffixClicked(it))
            },
            isError = state.error.isErrorWeight,
            drawableRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
            intRes = R.string.weight_screen_title,
            intResSup = R.string.support_text_weight_animal,
            intResError = R.string.error_no_weight_animal,
            keyboardOptions = keyboardOptionsNextNumber(),
            suffixList = suffixWeightList
        )
        OutlinedTextDateNew(
            value = state.date,
            onValueChange = { onIntent(AnimalWeightIntent.DateClicked(it)) },
        )
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = { onIntent(AnimalWeightIntent.NoteChanged(it)) },
        )
    }
}