@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.size

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
import com.zaroslikov.domain.models.list.suffixHeightList
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.orang_14
import com.zaroslikov.fermacompose2.orang_2
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


object AnimalSizeDestination : NavigationDestination {
    override val route = "animalSize"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

@Composable
fun AnimalSizeScreen(
    navigateBack: () -> Unit,
    viewModel: AnimalSizeViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(orang_1, orang_2)
    val icon = R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24
    val title = R.string.height_screen_title

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
                    viewModel.onIntent(AnimalSizeIntent.OpenDialogClicked(isEntry = true))
                }
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            AnimalSizeContainer2(
                modifier = Modifier.modifierScreenLazy(innerPadding),
                icon = icon,
                itemList = state.sizeList,
                colors = colors,
                isArchive = state.isArchive,
                onEditClick = {
                    viewModel.onIntent(AnimalSizeIntent.OpenDialogClicked(isEntry = true, it))
                },
                onDeleteClick = { viewModel.onIntent(AnimalSizeIntent.OpenBottomSheetDelete(it)) },
                titleRes = title
            )

        if (state.isOpenEntryBottomSheet)
            SizeBottomSheet(
                state = state.currentProduct,
                onIntent = viewModel::onIntent,
                colors = colors,
                icon = icon,
                titleRes = title,
            )
        if (state.isOpenBottomSheetDelete)
            WarningDeleteSizeBottomSheet(
                color = colors.first(),
                state = state.deleteSize,
                onDeleteClick = { viewModel.onIntent(AnimalSizeIntent.DeletePressed) },
                onDismissRequest = { viewModel.onIntent(AnimalSizeIntent.OpenBottomSheetDelete(null)) }
            )
    }
}


@Composable
private fun AnimalSizeContainer2(
    modifier: Modifier,
    @DrawableRes icon: Int,
    @StringRes titleRes: Int,
    colors: List<Color>,
    itemList: List<AnimalSizeUi>,
    onEditClick: (AnimalSizeUi) -> Unit,
    onDeleteClick: (Long) -> Unit,
    isArchive: Boolean,
) {
    InventoryAnimalBody(
        modifier = modifier,
        itemList = itemList,
        titleRes2 = titleRes,
        titleRes = R.string.message_no_date_title_height,
        messageRes = R.string.message_no_date_message_height,
        iconRes = R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24,
        iconColor = orang_2,
        backgroundColor = orang_14,
        isArchive = isArchive,
        detailCard = { item ->
            AnimalIndicatorsCardNew(
                icon = icon,
                colors = colors,
                value = item.size.toFormatNumber(),
                suffix = item.suffix,
                date = item.date,
                note = item.note,
                totalValues = item.totalValue,
                isArchive = isArchive,
                indicationStatus = item.indicationStatus,
                onEditClick = { onEditClick(item) },
                onDeleteClick = { onDeleteClick(item.id) },
            )
        },
    )
}

@Composable
private fun WarningDeleteSizeBottomSheet(
    color: Color,
    state: AnimalSizeUi?,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit
) {
    WarningDeleteBottomSheet(
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick,
        titleRes = R.string.base_section_delete_size,
        supportRes = R.string.base_section_delete_support_size,
        textRes = R.string.base_section_warning_size,
        textButtonRes = R.string.base_section_button_delete_size
    ) {
        state?.let {
            AnimalIndicatorsDeleteCard(
                color = color,
                value = state.size.toFormatNumber(),
                suffix = state.suffix,
                date = state.date,
                note = state.note,
            )
        }
    }
}

@Composable
private fun SizeBottomSheet(
    state: CurrentAnimalSize,
    colors: List<Color>,
    @DrawableRes icon: Int,
    @StringRes titleRes: Int,
    onIntent: (AnimalSizeIntent) -> Unit
) {
    EntryIndicationBottomSheet(
        isEntry = state.isEntry,
        enabledButton = state.hasAnyError,
        colors = colors,
        onDismissRequest = {
            onIntent(
                AnimalSizeIntent.OpenDialogClicked(
                    false,
                    isSaveStateForBottomSheet = state.isEntry
                )
            )
        },
        onSecondDismissRequest = {
            onIntent(AnimalSizeIntent.OpenDialogClicked(false))
        },
        onInsertClick = { onIntent(AnimalSizeIntent.InsertPressed) },
        onUpdateClick = { onIntent(AnimalSizeIntent.UpdatePressed) },
        iconRes = icon,
        titleRes = titleRes
    ) {
        OutlinedTextCountNew(
            value = state.size,
            onValueChange = {
                onIntent(AnimalSizeIntent.SizeChanged(it))
            },
            suffix = state.suffix,
            onSuffixChange = {
                onIntent(AnimalSizeIntent.SuffixClicked(it))
            },
            suffixList = suffixHeightList,
            isError = state.error.isErrorSize,
            drawableRes = R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24,
            intRes = R.string.height_screen_title,
            intResSup = R.string.support_text_height_animal,
            intResError = R.string.error_no_height_animal,
        )
        OutlinedTextDateNew(
            value = state.date,
            onValueChange = { onIntent(AnimalSizeIntent.DateClicked(it)) },
        )
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = { onIntent(AnimalSizeIntent.NoteChanged(it)) },
        )
    }
}