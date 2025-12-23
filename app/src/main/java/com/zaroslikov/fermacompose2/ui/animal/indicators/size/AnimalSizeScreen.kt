@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.size

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
import com.zaroslikov.domain.models.enums.IndicationStatus
import com.zaroslikov.domain.models.list.suffixHeightList
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.ui.animal.indicators.AnimalIndicatorsCardNew
import com.zaroslikov.fermacompose2.ui.animal.indicators.InventoryAnimalBody
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.sections.EntryIndicationBottomSheet


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
            NeonGlowFab(colors = colors) {
                viewModel.onIntent(
                    AnimalSizeIntent.OpenDialogClicked(
                        isEntry = true
                    )
                )
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
                onInsertClick = { viewModel.onIntent(AnimalSizeIntent.OpenDialogClicked(isEntry = true)) },
                onEditClick = {
                    viewModel.onIntent(AnimalSizeIntent.OpenDialogClicked(isEntry = false, it))
                },
                onDeleteClick = { viewModel.onIntent(AnimalSizeIntent.DeletePressed) },
                onDetailClick = { viewModel.positeive(it.first, it.second) },
                titleRes = title
            )

        if (state.isOpenDialog)
            SizeBottomSheet(
                state = state,
                onIntent = viewModel::onIntent,
                colors = colors,
                icon = icon,
                titleRes = title,
            )
    }
}


@Composable
private fun AnimalSizeContainer2(
    modifier: Modifier,
    @DrawableRes icon: Int,
    @StringRes titleRes: Int,
    colors: List<Color>,
    itemList: List<DomainAnimalSize>,
    onDetailClick: (Pair<DomainAnimalSize, DomainAnimalSize?>) -> Pair<String, IndicationStatus>,
    onInsertClick: () -> Unit,
    onEditClick: (DomainAnimalSize) -> Unit,
    onDeleteClick: (Long) -> Unit,
) {
    InventoryAnimalBody(
        modifier = modifier,
        itemList = itemList,
        onInsertClick = onInsertClick,
        titleRes2 = titleRes,
        titleRes = R.string.message_no_date_title_height,
        messageRes = R.string.message_no_date_message_height,
        supportRes = R.string.message_no_date_support_height,
        buttonRes = R.string.button_sale_message_no_height
    ) { item, previous ->
        AnimalIndicatorsCardNew(
            icon = icon,
            colors = colors,
            value = item.size.toFormatNumber(),
            suffix = item.suffix,
            date = item.date,
            note = item.note,
            onEditClick = { onEditClick(item) },
            onDeleteClick = { onDeleteClick(item.id) },
            onDetailClick = { onDetailClick((item to previous)) },
        )
    }
}

@Composable
private fun SizeBottomSheet(
    state: AnimalSizeState,
    colors: List<Color>,
    @DrawableRes icon: Int,
    @StringRes titleRes: Int,
    onIntent: (AnimalSizeIntent) -> Unit
) {
    EntryIndicationBottomSheet(
        isEntry = state.isEntry,
        enabledButton = state.enabledButton(),
        colors = colors,
        onDismissRequest = {
            onIntent(AnimalSizeIntent.EndDialogClicked)
        },
        onInsertClick = { onIntent(AnimalSizeIntent.InsertPressed) },
        onUpdateClick = { onIntent(AnimalSizeIntent.UpdatePressed) },
        icon = icon,
        titleRes = titleRes
    ) {
        OutlinedTextCountNew(
            value = state.domainAnimalSize.size,
            onValueChange = {
                onIntent(AnimalSizeIntent.SizeChanged(it))
            },
            suffix = state.domainAnimalSize.suffix,
            onSuffixChange = {
                onIntent(AnimalSizeIntent.SuffixClicked(it))
            },
            isError = state.error.isErrorSize,
            drawableRes = R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24,
            intRes = R.string.height_screen_title,
            intResSup = R.string.support_text_height_animal,
            intResError = R.string.error_no_height_animal,
            keyboardOptions = keyboardOptionsNextNumber(),
            suffixList = suffixHeightList
        )
        OutlinedTextDateNew(
            value = state.domainAnimalSize.date,
            onValueChange = { onIntent(AnimalSizeIntent.DateClicked(it)) },
        )
        OutlinedTextNoteNew(
            value = state.domainAnimalSize.note,
            onValueChange = { onIntent(AnimalSizeIntent.NoteChanged(it)) },
        )
    }
}

