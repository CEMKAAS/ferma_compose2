@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.vaccination

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
import com.zaroslikov.domain.models.dto.animal.AnimalVaccinationExpensesDomain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DateFactoryCardNew
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextVaccinationNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.project.sections.EntryIndicationBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.AnimalIndicatorsDeleteCard
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.AnimalVaccinationCardNew
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.InventoryAnimalBody
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_2
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.violet_4


object AnimalVaccinationDestination : NavigationDestination {
    override val route = "animalVaccination"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

@Composable
fun AnimalVaccinationScreen(
    navigateBack: () -> Unit,
    viewModel: AnimalVaccinationViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(violet_4, violet_2)
    val icon = R.drawable.vaccines_24dp_000000_fill0_wght400_grad0_opsz24
    val title = R.string.vaccination_screen_title

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
                    viewModel.onIntent(
                        AnimalVaccinationIntent.OpenEntryBottomSheetByItem(true)
                    )
                }
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            AnimalVaccinationContainer2(
                modifier = Modifier.modifierScreenLazy(innerPadding),
                icon = icon,
                titleRes = title,
                colors = colors,
                itemList = state.vaccinationList,
                isArchive = state.isArchive,
                onEditClick = {
                    viewModel.onIntent(AnimalVaccinationIntent.OpenEntryBottomSheetByItem(true, it))
                },
                onDeleteClick = {
                    viewModel.onIntent(AnimalVaccinationIntent.OpenBottomSheetDelete(it))
                }
            )
        if (state.isOpenDialog)
            VaccinationBottomSheet(
                state = state.currentProduct,
                onIntent = viewModel::onIntent,
                colors = colors,
                icon = icon,
                titleRes = title,
                titleList = state.titleVaccinationList,
            )
        if (state.isOpenBottomSheetDelete)
            WarningDeleteVaccinationBottomSheet(
                color = colors.first(),
                state = state.deleteVaccination,
                onDismissRequest = {
                    viewModel.onIntent(AnimalVaccinationIntent.OpenBottomSheetDelete(null))
                },
                onDeleteClick = { viewModel.onIntent(AnimalVaccinationIntent.DeletePressed) }
            )
    }
}

@Composable
private fun AnimalVaccinationContainer2(
    modifier: Modifier,
    @DrawableRes icon: Int,
    @StringRes titleRes: Int,
    colors: List<Color>,
    isArchive: Boolean,
    itemList: List<AnimalVaccinationExpensesDomain>,
    onEditClick: (AnimalVaccinationExpensesDomain) -> Unit,
    onDeleteClick: (Long) -> Unit,
) {
    InventoryAnimalBody(
        modifier = modifier,
        isVaccination = true,
        itemList = itemList,
        titleRes2 = titleRes,
        titleRes = R.string.message_no_date_title_vaccination,
        messageRes = R.string.message_no_date_message_vaccination,
        iconRes = R.drawable.vaccines_24dp_000000_fill0_wght400_grad0_opsz24,
        iconColor = violet_1,
        backgroundColor = violet_3,
        isArchive = isArchive,
        detailCard = { item ->
            AnimalVaccinationCardNew(
                icon = icon,
                colors = colors,
                value = item.vaccination,
                count = item.countVaccination.formatNumber(),
                suffix = Suffix.RUBLE,
                date = item.date,
                note = item.note,
                onEditClick = { onEditClick(item) },
                onDeleteClick = { onDeleteClick(item.id) },
                nextDate = item.nextDate,
                isArchive = isArchive,
                price = item.priceAll ?: item.price,
            )
        }
    )
}


@Composable
private fun WarningDeleteVaccinationBottomSheet(
    color: Color,
    state: AnimalVaccinationExpensesDomain?,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit
) {
    WarningDeleteBottomSheet(
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick,
        titleRes = R.string.base_section_delete_vaccination,
        supportRes = R.string.base_section_delete_support_vaccination,
        textRes = R.string.base_section_warning_vaccination,
        textButtonRes = R.string.base_section_button_delete_vaccination
    ) {
        state?.let {
            AnimalIndicatorsDeleteCard(
                value = state.vaccination,
                suffix = null,
                date = state.date,
                note = state.note,
                color = color,
            )
        }
    }
}


@Composable
private fun VaccinationBottomSheet(
    state: Vaccination,
    colors: List<Color>,
    @DrawableRes icon: Int,
    @StringRes titleRes: Int,
    titleList: List<String>,
    onIntent: (AnimalVaccinationIntent) -> Unit
) {
    EntryIndicationBottomSheet(
        titleRes = titleRes,
        iconRes = icon,
        colors = colors,
        isEntry = state.isEntry,
        enabledButton = state.hasAnyError,
        onDismissRequest = {
            onIntent(
                AnimalVaccinationIntent.OpenEntryBottomSheetByItem(
                    false,
                    isSaveStateForBottomSheet = state.isEntry
                )
            )
        },
        onSecondDismissRequest = {
            onIntent(AnimalVaccinationIntent.OpenEntryBottomSheetByItem(false))
        },
        onInsertClick = { onIntent(AnimalVaccinationIntent.InsertPressed) },
        onUpdateClick = { onIntent(AnimalVaccinationIntent.UpdatePressed) }
    ) {
        OutlinedTextVaccinationNew(
            value = state.vaccination,
            onValueChange = { onIntent(AnimalVaccinationIntent.VaccinationChanged(it)) },
            titleList = titleList,
            isError = state.error.isErrorVaccination,
        )
        OutlinedTextCountAnimalNew(
            value = state.countVaccination,
            onValueChange = {
                onIntent(AnimalVaccinationIntent.CountChanged(it))
            },
            suffix = Suffix.PIECES,
            isError = state.error.isErrorCount,
            intRes = R.string.animal_vaccination_count_vaccination,
            intResSup = R.string.animal_vaccination_support_count_vaccination,
            countAnimal = state.countAnimalAll,
            isErrorCountZero = state.error.isErrorCount,
        )
        OutlinedPriceInputNew(
            price = state.price,
            onPriceChange = {
                onIntent(AnimalVaccinationIntent.PriceChanged(it))
            },
            priceAll = state.priceAll,
            isAutoCalculate = state.isAutoCalculate,
            onAutoCalculate = {
                onIntent(AnimalVaccinationIntent.AutoPriceClicked(it))
            },
            tooltipTextResAutoCal = R.string.animal_vaccination_auto_calculate_tool,
            isManyCount = true,
            supportTextRes = if (state.isAutoCalculate) R.string.animal_vaccination_price_all else R.string.animal_vaccination_price,
            supportTextResAutoCal = R.string.animal_vaccination_price,
            count = state.countVaccination,
            countSuffix = Suffix.PIECES,
            priceSuffix = Suffix.RUBLE
        )
        OutlinedTextDateNew(
            value = state.date,
            onValueChange = { onIntent(AnimalVaccinationIntent.DateClicked(it)) },
            intResSup = R.string.support_text_data_vaccination,
            isNecessarily = true
        )
        DateFactoryCardNew(
            dateBoring = state.date,
            dateFactory = state.nextDate,
            isDateFactory = state.isDateFactory,
            dateFactoryClicked = { onIntent(AnimalVaccinationIntent.DateFactoryClicked(it)) },
            dateFactoryChanged = { onIntent(AnimalVaccinationIntent.DateNextClicked(it)) },
            intTitle = R.string.animal_vaccination_next_vaccination,
            intTooltip = R.string.error_no_product,
            intRes = R.string.outlined_text_date_next,
            intResSup = R.string.support_text_data_next,
        )
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = { onIntent(AnimalVaccinationIntent.NoteChanged(it)) },
        )
    }
}

