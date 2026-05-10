@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.edit


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
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
import com.zaroslikov.fermacompose2.ui.warehouse.MainSettingsCard


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
                intRes = if (state.currentProduct.isAnimalGroup)
                    R.string.animals_add_screen_edit_s else R.string.animals_add_screen_edit,
                onNavigateBackClick = navigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            AnimalEntryContainer(
                modifier = Modifier
                    .modifierScreen(innerPadding),
                state = state.currentProduct,
                onIntent = viewModel::onIntent
            )
    }
}

@Composable
fun AnimalEntryContainer(
    modifier: Modifier,
    state: AnimalUi,
    onIntent: (AnimalEditIntent) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MainSettingsCard(
            currentIcon = state.currentIcon,
            imagePath = state.imagePath,
            iconList = state.iconList,
            iconBoxColor = price_green_2,
            onImageSelected = { onIntent(AnimalEditIntent.ImagePathClicked(it)) },
            onIconSelected = { onIntent(AnimalEditIntent.IconClicked(it)) },
        )
        AnimalNameOutlinedTextNew(
            value = state.name,
            onValueChange = {
                onIntent(AnimalEditIntent.TitleChanged(it))
            },
            isAnimalGroup = state.isAnimalGroup,
            isErrorTitle = state.error.isErrorTitle
        )
        OutlinedTextAnimalTypeNew(
            value = state.type,
            onValueChange = {
                onIntent(AnimalEditIntent.TypeChanged(it))
            },
            intResSup = if (state.isAnimalGroup) R.string.support_text_type_animals else R.string.support_text_type_animals,
            list = state.pickList.typeList,
        )
        if (!state.isAnimalGroup)
            OutlinedTextSexNew(
                value = state.sex,
                onValueChange = {
                    onIntent(AnimalEditIntent.SexClicked(it))
                }
            )
        OutlinedTextDateNew(
            value = state.dateBorn,
            intRes = R.string.outlined_text_date_born,
            intResSup = if (!state.isAnimalGroup) R.string.outlined_text_date_born
            else R.string.support_text_date_born_s,
            onValueChange = {
                onIntent(AnimalEditIntent.DateClicked(it))
            }
        )
        DateFactoryCardNew(
            dateBoring = state.dateBorn,
            dateFactory = state.dateFactory,
            isDateFactory = state.isDateFactory,
            dateFactoryClicked = { onIntent(AnimalEditIntent.DateFactoryClicked(it)) },
            dateFactoryChanged = { onIntent(AnimalEditIntent.DateFactoryChanged(it)) },
            intTitle = if (!state.isAnimalGroup) R.string.checkbox_born else R.string.checkbox_born_s,
            intTooltip = R.string.tooltip_animals_born,
            intRes = R.string.outlined_text_date_factory,
            intResSup = if (!state.isAnimalGroup) R.string.support_text_date_factory else R.string.support_text_date_factory_s,
        )
        OutlinedTextCountNew(
            value = state.foodDay,
            onValueChange = {
                onIntent(AnimalEditIntent.FoodDayChanged(it))
            },
            suffix = state.foodDaySuffix,
            onSuffixChange = {
                onIntent(AnimalEditIntent.FoodDaySuffixClicked(it))
            },
            suffixList = suffixWeightDayList,
            isError = false,
            isNecessarily = false,
            drawableRes = R.drawable.outline_restaurant_24,
            intRes = R.string.outlined_food_day_animals,
            intResSup = if (!state.isAnimalGroup) R.string.support_text_food_day_animal else R.string.support_text_food_day_animals,
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
            enabledButton = state.hasAnyError
        ) { onIntent(AnimalEditIntent.Update) }
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
        colors = listOf(price_green, green_shamrock),
        enabled = enabledButton,
        paddingValues = PaddingValues(vertical = 14.dp)
    )
}