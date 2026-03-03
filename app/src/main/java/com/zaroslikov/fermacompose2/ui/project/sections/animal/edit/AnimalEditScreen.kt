@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.edit


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.list.suffixWeightDayList
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.blue_14
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.DateFactoryCardNew
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.TextField.AnimalNameOutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextAnimalTypeNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextSexNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard


object AnimalEditDestination : NavigationDestination {
    override val route = "animal_edit"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

@Composable
fun AnimalEditProduct(
    navigateBack: () -> Unit,
    viewModel: AnimalEditViewModel = hiltViewModel()
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
                intRes = if (state.animalUi.isAnimalGroup) R.string.animals_add_screen_edit_s
                else R.string.animals_add_screen_edit,
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
    state: AnimalEditState,
    onIntent: (AnimalEntryIntent) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimalNameOutlinedTextNew(
            value = state.animalUi.name,
            onValueChange = {
                onIntent(AnimalEntryIntent.TitleChanged(it))
            },
            isAnimalGroup = state.animalUi.isAnimalGroup,
            isErrorTitle = state.error.isErrorTitle
        )
        OutlinedTextAnimalTypeNew(
            value = state.animalUi.type,
            onValueChange = {
                onIntent(AnimalEntryIntent.TypeChanged(it))
            },
            list = state.typeList,
        )
        if (!state.animalUi.isAnimalGroup)
            OutlinedTextSexNew(
                value = state.animalUi.sex,
                onValueChange = {
                    onIntent(AnimalEntryIntent.SexClicked(it))
                },
            )
        OutlinedTextDateNew(
            value = state.animalUi.dateBorn,
            intRes = R.string.outlined_text_date_born,
            intResSup = if (!state.animalUi.isAnimalGroup) R.string.outlined_text_date_born
            else R.string.support_text_date_born_s,
            onValueChange = {
                onIntent(AnimalEntryIntent.DateClicked(it))
            }
        )
        DateFactoryCardNew(
            dateBoring = state.animalUi.dateBorn,
            dateFactory = state.animalUi.dateFactory,
            isDateFactory = state.animalUi.isDateFactory,
            dateFactoryClicked = { onIntent(AnimalEntryIntent.DateFactoryClicked(it)) },
            dateFactoryChanged = { onIntent(AnimalEntryIntent.DateFactoryChanged(it)) },
            intTitle = if (!state.animalUi.isAnimalGroup) R.string.checkbox_born else R.string.checkbox_born_s,
            intTooltip = R.string.tooltip_animals_born,
            intRes = R.string.outlined_text_date_factory,
            intResSup = if (!state.animalUi.isAnimalGroup) R.string.support_text_date_factory else R.string.support_text_date_factory_s,
        )
        OutlinedTextCountNew(
            value = state.animalUi.foodDay,
            onValueChange = {
                onIntent(AnimalEntryIntent.FoodDayChanged(it))
            },
            isError = false,
            onSuffixChange = {
                onIntent(AnimalEntryIntent.FoodDaySuffixClicked(it))
            },
            suffixList = suffixWeightDayList,
            intRes = R.string.outlined_food_day_animals,
            intResSup = if (!state.animalUi.isAnimalGroup) R.string.support_text_food_day_animal else R.string.support_text_food_day_animals,
            suffix = state.animalUi.foodDaySuffix,
        )
        WarningCard(
            colorBackground = blue_3,
            colorBorder = blue_9,
            colorIcon = blue_1,
            colorIconBackground = blue_13,
            colorTitle = blue_14,
            colorText = blue_8,
            icon = R.drawable.outline_info_24,
            title = R.string.animal_edit_warning_title,
            text = R.string.animal_edit_warning_text
        )
        SaveButton(
            enabledButton = true
        ) { onIntent(AnimalEntryIntent.Update) }
    }
}

@Composable
private fun SaveButton(
    enabledButton: Boolean,
    onClick: () -> Unit
) {
    GradientButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.button_text_edit_title),
        onClick = onClick,
        colors = listOf(Color(0xFF00A63E), Color(0xFF009966)),
        enabled = enabledButton,
        paddingValues = PaddingValues(vertical = 14.dp)
    )
}