@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.entry


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.green_1
import com.zaroslikov.fermacompose2.green_g_2
import com.zaroslikov.fermacompose2.green_g_3
import com.zaroslikov.fermacompose2.green_g_4
import com.zaroslikov.fermacompose2.ui.elements.AnimalNameOutlinedText
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CheckboxTextIcon
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenu
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextSex
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextTitleAdd
import com.zaroslikov.fermacompose2.ui.elements.RadioButtonRow
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.toOutlinedText
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
                navigateUp = navigateBack,
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
    Column(modifier = modifier) {
        /*   if (state.isEntry)
             AnimalGroupCard(
                  isAnimalGroup = state.isAnimalGroup,
                  animalGroupClicked = { onIntent(AnimalEntryIntent.AnimalGroupClicked(it)) }
              )*/
        AnimalNameOutlinedText(
            value = state.title,
            onValueChange = {
                onIntent(AnimalEntryIntent.TitleChanged(it))
            },
            isAnimalGroup = state.isAnimalGroup,
            isErrorTitle = state.error.isErrorTitle
        )
        OutlinedTextTitleAdd(
            intRes = R.string.outlined_text_type,
            intResSup = if (!state.isAnimalGroup) R.string.support_text_type_animal
            else R.string.support_text_type_animals,
            intResError = R.string.error_no_type_animal,
            value = state.type,
            onValueChange = {
                onIntent(AnimalEntryIntent.TypeChanged(it))
            },
            drawableRes = R.drawable.baseline_pets_24,
            titleList = state.typeList,
            isErrorTitle = state.error.isErrorType,
            isErrorSlash = false,
        )
        if (state.isAnimalGroup && state.isEntry)
            OutlinedTextCountAnimal(
                value = state.count,
                onValueChange = {
                    onIntent(AnimalEntryIntent.CountChanged(it))
                },
                drawableRes = R.drawable.baseline_spoke_24,
                isError = state.error.isErrorCount,
                suffix = Suffix.RUBLE,
                intRes = R.string.outlined_text_field_quantity,
                intResSup = R.string.support_text_count_animals,
                isWarehouseShow = false,
                isDropMenuShow = false
            )
        if (!state.isAnimalGroup)
            OutlinedTextSex(
                value = state.sex,
                onValueChange = {
                    onIntent(AnimalEntryIntent.SexClicked(it))
                },
            )
        if (state.isEntry)
            OutlinedPriceInput(
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
            )
        OutlinedTextDate(
            value = state.dateBorn,
            intRes = R.string.outlined_text_date_born,
            intResSup = if (!state.isAnimalGroup) R.string.outlined_text_date_born
            else R.string.support_text_date_born_s,
            onValueChange = {
                onIntent(AnimalEntryIntent.DateClicked(it))
            }
        )
        DateFactoryCard(
            dateFactory = state.dateFactory,
            isAnimalGroup = state.isAnimalGroup,
            isDateFactory = state.isDateFactory,
            dateFactoryClicked = { onIntent(AnimalEntryIntent.DateFactoryClicked(it)) },
            dateFactoryChanged = { onIntent(AnimalEntryIntent.DateFactoryChanged(it)) },
        )
        OutlinedTextCountAnimal(
            value = state.foodDay,
            onValueChange = {
                onIntent(AnimalEntryIntent.FoodDayChanged(it))
            },
            isError = false,
            onSuffixChange = {
                onIntent(AnimalEntryIntent.FoodDaySuffixClicked(it))
            },
            intRes = R.string.outlined_food_day_animals,
            intResSup = if (!state.isAnimalGroup) R.string.support_text_food_day_animal else R.string.support_text_food_day_animals,
            suffix = state.foodDaySuffix,
            isWarehouseShow = false,
            isDropMenuShow = true,
            versionDropMenu = DropdownMenu.WEIGHT,
            isNecessarily = false
        )
        if (state.isEntry)
            OutlinedTextNote(
                value = state.note,
                onValueChange = {
                    onIntent(AnimalEntryIntent.NoteChanged(it))
                },
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


@Composable
private fun DateFactoryCard(
    isDateFactory: Boolean,
    isAnimalGroup: Boolean,
    dateFactory: String,
    dateFactoryClicked: (Boolean) -> Unit,
    dateFactoryChanged: (String) -> Unit
) {
    CardField(
        modifier = Modifier
            .padding(bottom = 4.dp),
        row = false
    ) {
        CheckboxTextIcon(
            modifier = if (!isDateFactory) Modifier.toOutlinedText() else Modifier,
            checked = isDateFactory,
            onCheckedChange = {
                dateFactoryClicked(it)
            },
            intTitle = if (!isAnimalGroup) R.string.checkbox_born else R.string.checkbox_born_s,
            isTooltipShow = true,
            intTooltip = R.string.tooltip_animals_born
        )
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = !isDateFactory,
        ) {
            OutlinedTextDate(
                value = dateFactory,
                intRes = R.string.outlined_text_date_factory,
                intResSup = if (!isAnimalGroup) R.string.support_text_date_factory else R.string.support_text_date_factory_s,
                drawableRes = R.drawable.baseline_event_24,
                onValueChange = { dateFactoryChanged(it) },
                isCardBorder = false
            )
        }
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