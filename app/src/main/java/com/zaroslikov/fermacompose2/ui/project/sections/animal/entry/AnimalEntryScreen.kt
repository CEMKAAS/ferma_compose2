@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.entry


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.suffixWeightDayList
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.DateFactoryCardNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.AnimalNameOutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextAnimalTypeNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextSexNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanel
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent


object AnimalEntryDestination : NavigationDestination {
    override val route = "animalEntry"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

@Composable
fun AnimalEntryProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: AnimalEntryViewModel = hiltViewModel()
) {
    val eventFlow = viewModel.navigation
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = gerAppBarTitle(state.isAnimalGroup, state.isEntry),
                onNavigateBackClick = navigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        AnimalEntryContainer(
            modifier = Modifier
                .modifierScreen(innerPadding),
            state = state,
            onIntent = viewModel::onIntent
        )
    }
}

@Composable
fun AnimalEntryContainer(
    modifier: Modifier,
    state: AnimalEntryState,
    onIntent: (AnimalEntryIntent) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimalNameOutlinedTextNew(
            value = state.title,
            onValueChange = {
                onIntent(AnimalEntryIntent.TitleChanged(it))
            },
            isAnimalGroup = state.isAnimalGroup,
            isErrorTitle = state.error.isErrorTitle
        )
        OutlinedTextAnimalTypeNew(
            value = state.type,
            onValueChange = {
                onIntent(AnimalEntryIntent.TypeChanged(it))
            },
            list = state.typeList,
        )
        if (!state.isAnimalGroup)
            OutlinedTextSexNew(
                value = state.sex,
                onValueChange = {
                    onIntent(AnimalEntryIntent.SexClicked(it))
                },
            )
        OutlinedPriceInputNew(
            price = state.price,
            onPriceChange = {
                onIntent(AnimalEntryIntent.PriceChanged(it))
            },
            priceAll = state.priceAll,
            isAutoCalculate = state.isAutoPrice,
            onAutoCalculate = {
                onIntent(AnimalEntryIntent.AutoPriceClicked(it))
            },
            isManyCount = state.isAnimalGroup,
            count = state.count,
            countSuffix = state.countSuffix,
            priceSuffix = Suffix.RUBLE,
        )
        OutlinedTextDateNew(
            value = state.dateBorn,
            intRes = R.string.outlined_text_date_born,
            intResSup = if (!state.isAnimalGroup) R.string.outlined_text_date_born
            else R.string.support_text_date_born_s,
            onValueChange = {
                onIntent(AnimalEntryIntent.DateClicked(it))
            }
        )
        DateFactoryCardNew(
            dateBoring = state.dateBorn,
            dateFactory = state.dateFactory,
            isDateFactory = state.isDateFactory,
            dateFactoryClicked = { onIntent(AnimalEntryIntent.DateFactoryClicked(it)) },
            dateFactoryChanged = { onIntent(AnimalEntryIntent.DateFactoryChanged(it)) },
            intTitle = if (!state.isAnimalGroup) R.string.checkbox_born else R.string.checkbox_born_s,
            intTooltip = R.string.tooltip_animals_born,
            intRes = R.string.outlined_text_date_factory,
            intResSup = if (!state.isAnimalGroup) R.string.support_text_date_factory else R.string.support_text_date_factory_s,
        )
        OutlinedTextCountNew(
            value = state.foodDay,
            onValueChange = {
                onIntent(AnimalEntryIntent.FoodDayChanged(it))
            },
            isError = false,
            onSuffixChange = {
                onIntent(AnimalEntryIntent.FoodDaySuffixClicked(it))
            },
            suffixList = suffixWeightDayList,
            intRes = R.string.outlined_food_day_animals,
            intResSup = if (!state.isAnimalGroup) R.string.support_text_food_day_animal else R.string.support_text_food_day_animals,
            suffix = state.foodDaySuffix,
        )
        ButtonPanel(
            entryButton = if (state.price.isBlank()) R.string.button_add else R.string.button_expenses,
            isEntry = state.isEntry,
            onClickInsert = { onIntent(AnimalEntryIntent.Insert) },
            onClickUpdate = { onIntent(AnimalEntryIntent.Update) },
            onClickDelete = { onIntent(AnimalEntryIntent.Delete) }
        )
    }
}

private fun gerAppBarTitle(isAnimalGroup: Boolean, isEntry: Boolean): Int {
    return when {
        isAnimalGroup && isEntry -> R.string.animals_add_screen_title
        !isAnimalGroup && isEntry -> R.string.animal_add_screen_title
        isAnimalGroup && !isEntry -> R.string.animals_add_screen_edit_s
        else -> R.string.animals_add_screen_edit
    }
}