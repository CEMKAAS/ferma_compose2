@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.vaccination

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.animal.indicators.size.HeadingIndicators
import com.zaroslikov.fermacompose2.ui.animal.indicators.vaccination.AnimalVaccinationState.Vaccination
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.FloatButton
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextTitleAdd
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanel
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination


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
    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.vaccination_screen_title,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatButton {
                viewModel.onIntent(
                    AnimalVaccinationIntent.OpenDialogClicked(
                        isEntry = true
                    )
                )
            }
        }
    ) { innerPadding ->
        if (state.value.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            AnimalVaccinationContainer(
                modifier = Modifier.modifierScreenLazy(innerPadding),
                state = state.value,
                onIntent = viewModel::onIntent
            )

        if (state.value.isOpenDialog)
            VaccinationBottomSheet(
                state = state.value.vaccination,
                onIntent = viewModel::onIntent,
                isEntry = state.value.isEntry,
                isAnimalGroup = state.value.isAnimalGroup,
                titleVaccinationList = state.value.titleVaccinationList,
                countAnimalAll = state.value.countAnimalAll,
                suffixAnimal = state.value.suffixAnimal,
            )
    }
}

@Composable
private fun AnimalVaccinationContainer(
    modifier: Modifier = Modifier,
    state: AnimalVaccinationState,
    onIntent: (AnimalVaccinationIntent) -> Unit
) {
    if (state.vaccinationList.isNotEmpty())
        VaccinationList2(
            modifier = modifier,
            indicatorsList = state.vaccinationList,
            onEditChange = {
                onIntent(
                    AnimalVaccinationIntent.OpenDialogClicked(
                        isEntry = false,
                        it
                    )
                )
            }
        )
    else MessageNoData(
        modifier = modifier,
        onClick = { onIntent(AnimalVaccinationIntent.OpenDialogClicked(isEntry = true)) },
        titleRes = R.string.message_no_date_title_vaccination,
        messageRes = R.string.message_no_date_message_vaccination,
        supportRes = R.string.message_no_date_support_vaccination,
        buttonRes = R.string.button_sale_message_no_vaccination
    )
}

@Composable
private fun VaccinationList2(
    modifier: Modifier,
    indicatorsList: List<DomainAnimalVaccination>,
    onEditChange: (DomainAnimalVaccination) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        item { HeadingIndicators(R.string.vaccination_screen_title, isVaccination = true) }
        itemsIndexed(items = indicatorsList, key = { index, _ -> index }) { index, item ->
            VaccinationCard(
                modifier = Modifier.clickable { onEditChange(item) },
                domainAnimalVaccination = item
            )
        }
    }
}

@Composable
private fun VaccinationCard(
    modifier: Modifier = Modifier,
    domainAnimalVaccination: DomainAnimalVaccination
) {
    var details by rememberSaveable { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        if (details) 0.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    CardField(
        modifier = modifier.padding(bottom = extraPadding.coerceAtLeast(0.dp)),
        row = false
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = domainAnimalVaccination.vaccination,
                style = textBold_16,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.weight(1f),
                text = domainAnimalVaccination.date,
                style = textBold_16,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.weight(1f),
                text = domainAnimalVaccination.nextVaccination,
                style = textBold_16,
                textAlign = TextAlign.Center
            )
            IconButton(
                modifier = Modifier.weight(0.25f),
                onClick = { details = !details }) {
                Icon(
                    painterResource(if (details) R.drawable.icon_keyboard_arrow_up else R.drawable.icon_keyboard_arrow_down),
                    contentDescription = null
                )
            }
        }
        if (details)
            DetailsCount(domainAnimalVaccination)
    }
}

@Composable
private fun DetailsCount(
    domainAnimalVaccination: DomainAnimalVaccination
) {
    Row {
        Text(
            modifier = Modifier.weight(1f),
            text = buildAnnotatedString {
                append(
                    if (domainAnimalVaccination.note != "")
                        domainAnimalVaccination.note else stringResource(R.string.animal_indicators_screen_no_note)
                )
            },
            style = text_16,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun VaccinationBottomSheet(
    state: Vaccination,
    isEntry: Boolean,
    isAnimalGroup: Boolean,
    titleVaccinationList: List<String>,
    countAnimalAll: String,
    suffixAnimal: Suffix,
    onIntent: (AnimalVaccinationIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = { onIntent(AnimalVaccinationIntent.EndDialogClicked) },
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.modifierBottomSheet()) {
            OutlinedTextTitleAdd(
                value = state.domainAnimalVaccination.vaccination,
                onValueChange = { onIntent(AnimalVaccinationIntent.VaccinationChanged(it)) },
                intRes = R.string.animal_vaccination_title,
                intResSup = R.string.support_text_vaccination_animals,
                intResError = R.string.error_no_vaccination,
                drawableRes = R.drawable.vaccines_24dp_000000_fill0_wght400_grad0_opsz24,
                titleList = titleVaccinationList,
                isErrorTitle = state.error.isErrorVaccination,
                isErrorSlash = false
            )
            if (isAnimalGroup)
                OutlinedTextCountAnimal2(
                    value = state.countAnimal,
                    onValueChange = {
                        onIntent(AnimalVaccinationIntent.CountChanged(it))
                    },
                    countAnimalAll = countAnimalAll,
                    suffix = suffixAnimal,
                    isError = state.error.isErrorCount,
                    intRes = R.string.animal_vaccination_count_vaccination,
                    isErrorCountZero = state.error.isErrorCountZero
                )
            OutlinedPriceInput(
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
                isManyCount = isAnimalGroup,
                supportTextRes = if (isAnimalGroup) R.string.animal_vaccination_price_all else R.string.animal_vaccination_price,
                supportTextResAutoCal = R.string.animal_vaccination_price
            )
            OutlinedTextDate(
                value = state.domainAnimalVaccination.date,
                onValueChange = { onIntent(AnimalVaccinationIntent.DateClicked(it)) },
                intResSup = R.string.support_text_data_vaccination,
                isNecessarily = true
            )
            OutlinedTextDate(
                value = state.domainAnimalVaccination.nextVaccination,
                onValueChange = { onIntent(AnimalVaccinationIntent.DateNextClicked(it)) },
                intRes = R.string.outlined_text_date_next,
                intResSup = R.string.support_text_data_next,
                isLimit = false,
                minDate = state.domainAnimalVaccination.date,
                isNecessarily = true
            )
            OutlinedTextNote(
                value = state.domainAnimalVaccination.note,
                onValueChange = { onIntent(AnimalVaccinationIntent.NoteChanged(it)) },
            )
            ButtonPanel(
                isEntry = isEntry,
                entryButton = R.string.button_add,
                onClickInsert = { onIntent(AnimalVaccinationIntent.InsertPressed) },
                onClickUpdate = { onIntent(AnimalVaccinationIntent.UpdatePressed) },
                onClickDelete = { onIntent(AnimalVaccinationIntent.DeletePressed) }
            )
        }
    }
}

