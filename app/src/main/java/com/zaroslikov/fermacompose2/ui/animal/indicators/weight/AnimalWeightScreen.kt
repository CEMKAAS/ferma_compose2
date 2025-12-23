@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.weight

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
import com.zaroslikov.domain.models.list.suffixWeightList
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.blue_2
import com.zaroslikov.fermacompose2.blue_4
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
            NeonGlowFab(colors = colors) {
                viewModel.onIntent(
                    AnimalWeightIntent.OpenDialogClicked(
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
            AnimalWeightContainer2(
                modifier = Modifier.modifierScreenLazy(innerPadding),
                icon = icon,
                colors = colors,
                itemList = state.weightList,
                onDetailClick = { viewModel.positeive(it.first, it.second) },
                onInsertClick = { viewModel.onIntent(AnimalWeightIntent.OpenDialogClicked(isEntry = true)) },
                onEditClick = {
                    viewModel.onIntent(AnimalWeightIntent.OpenDialogClicked(isEntry = false, it))
                },
                onDeleteClick = { viewModel.onIntent(AnimalWeightIntent.DeletePressed) },
                titleRes = title
            )

        if (state.isOpenDialog)
            WeightBottomSheet(
                state = state,
                onIntent = viewModel::onIntent,
                colors = colors,
                icon = icon,
                titleRes = title,
            )
    }
}

@Composable
private fun AnimalWeightContainer2(
    modifier: Modifier,
    @DrawableRes icon: Int,
    @StringRes titleRes: Int,
    colors: List<Color>,
    itemList: List<DomainAnimalWeight>,
    onDetailClick: (Pair<DomainAnimalWeight, DomainAnimalWeight?>) -> Pair<String, IndicationStatus>,
    onInsertClick: () -> Unit,
    onEditClick: (DomainAnimalWeight) -> Unit,
    onDeleteClick: (Long) -> Unit,
) {
    InventoryAnimalBody(
        modifier = modifier,
        itemList = itemList,
        onInsertClick = onInsertClick,
        titleRes = R.string.message_no_date_title_weight,
        messageRes = R.string.message_no_date_message_weight,
        supportRes = R.string.message_no_date_support_weight,
        buttonRes = R.string.button_sale_message_no_weight,
        titleRes2 = titleRes
    ) { item, previous ->
        AnimalIndicatorsCardNew(
            icon = icon,
            colors = colors,
            value = item.weight.toFormatNumber(),
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
private fun WeightBottomSheet(
    state: AnimalWeightState,
    colors: List<Color>,
    @DrawableRes icon: Int,
    @StringRes titleRes: Int,
    onIntent: (AnimalWeightIntent) -> Unit
) {
    EntryIndicationBottomSheet(
        isEntry = state.isEntry,
        enabledButton = state.enabledButton(),
        colors = colors,
        onDismissRequest = {
            onIntent(AnimalWeightIntent.EndDialogClicked)
        },
        onInsertClick = { onIntent(AnimalWeightIntent.InsertPressed) },
        onUpdateClick = { onIntent(AnimalWeightIntent.UpdatePressed) },
        icon = icon,
        titleRes = titleRes
    ) {
        OutlinedTextCountNew(
            value = state.domainAnimalWeight.weight,
            onValueChange = {
                onIntent(AnimalWeightIntent.WeightChanged(it))
            },
            suffix = state.domainAnimalWeight.suffix,
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
            value = state.domainAnimalWeight.date,
            onValueChange = { onIntent(AnimalWeightIntent.DateClicked(it)) },
        )
        OutlinedTextNoteNew(
            value = state.domainAnimalWeight.note,
            onValueChange = { onIntent(AnimalWeightIntent.NoteChanged(it)) },
        )
    }
}