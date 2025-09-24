@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.weight

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.animal.indicators.size.HeadingIndicators
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DropdownMenu
import com.zaroslikov.fermacompose2.ui.elements.FloatButton
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCount2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.textBuildIndicatorsAnnotated2
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanel
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlin.math.absoluteValue


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
    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.weight_screen_title,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatButton {
                viewModel.onIntent(
                    AnimalWeightIntent.OpenDialogClicked(
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
            AnimalWeightContainer(
                modifier = Modifier.modifierScreenLazy(innerPadding),
                state = state.value,
                onIntent = viewModel::onIntent
            )

        if (state.value.isOpenDialog)
            SizeBottomSheet(
                state = state.value.domainAnimalWeight,
                onIntent = viewModel::onIntent,
                error = state.value.error,
                isEntry = state.value.isEntry,
            )
    }
}

@Composable
private fun AnimalWeightContainer(
    modifier: Modifier = Modifier,
    state: AnimalWeightState,
    onIntent: (AnimalWeightIntent) -> Unit
) {
    if (state.weightList.isNotEmpty())
        VaccinationList2(
            modifier = modifier,
            indicatorsList = state.weightList,
            onEditChange = { onIntent(AnimalWeightIntent.OpenDialogClicked(isEntry = false, it)) }
        )
    else MessageNoData(
        modifier = modifier,
        onClick = { onIntent(AnimalWeightIntent.OpenDialogClicked(isEntry = true)) },
        titleRes = R.string.message_no_date_title_weight,
        messageRes = R.string.message_no_date_message_weight,
        supportRes = R.string.message_no_date_support_weight,
        buttonRes = R.string.button_sale_message_no_weight
    )
}

@Composable
private fun VaccinationList2(
    modifier: Modifier,
    indicatorsList: List<DomainAnimalWeight>,
    onEditChange: (DomainAnimalWeight) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        item { HeadingIndicators(R.string.weight_screen_title) }
        itemsIndexed(items = indicatorsList, key = { index, _ -> index }) { index, item ->
            val previousItem =
                if (index < indicatorsList.size - 1) indicatorsList[index + 1] else null
            SizeCard(
                modifier = Modifier.clickable { onEditChange(item) },
                domainAnimalSize = item,
                previousDomainAnimalSize = previousItem
            )
        }
    }
}

@Composable
private fun SizeCard(
    modifier: Modifier = Modifier,
    domainAnimalSize: DomainAnimalWeight,
    previousDomainAnimalSize: DomainAnimalWeight?
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
                text = "${domainAnimalSize.weight.toFormatNumber()} ${
                    stringResource(
                        domainAnimalSize.suffix.toResId()
                    )
                }",
                style = textBold_16,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.weight(1f),
                text = domainAnimalSize.date,
                style = textBold_16,
                textAlign = TextAlign.Center
            )
            if (previousDomainAnimalSize != null)
                IconButton(
                    modifier = Modifier.weight(0.25f),
                    onClick = { details = !details }) {
                    Icon(
                        imageVector = if (details) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            else Spacer(
                modifier = Modifier
                    .weight(0.25f)
                    .size(44.dp)
            )
        }
        if (details && previousDomainAnimalSize != null)
            DetailsCount(
                domainAnimalSize,
                previousDomainAnimalSize
            )
    }
}

@Composable
private fun DetailsCount(
    domainAnimalWeight: DomainAnimalWeight,
    previousDomainAnimalWeight: DomainAnimalWeight
) {
    val context = LocalContext.current

    val weight = domainAnimalWeight.weight.toConvertZeroDouble()
    val previousWeight = previousDomainAnimalWeight.weight.toConvertZeroDouble()

    val suffix = domainAnimalWeight.suffix
    val suffixPrevious = previousDomainAnimalWeight.suffix

    val note = if (domainAnimalWeight.note != "") "\n${domainAnimalWeight.note}" else ""

    val weightConverted = weight.convertWeight(suffix, to = Suffix.GRAM)
    val previousConverted = previousWeight.convertWeight(suffixPrevious, to = Suffix.GRAM)

    val totalValue = (weightConverted - previousConverted).convertWeight(
        Suffix.GRAM,
        suffix
    ).absoluteValue.formatNumber()

    Row {
        Text(
            modifier = Modifier.weight(1f),
            text = when {
                weight > previousWeight ->
                    textBuildIndicatorsAnnotated2(
                        context = context,
                        intRes = R.string.animal_indicators_weight_increased_s,
                        totalValue = totalValue,
                        suffix = suffix,
                        isPlus = true,
                        note = note
                    )

                weight == previousWeight ->
                    buildAnnotatedString {
                        append(stringResource(R.string.animal_indicators_weight_not_changed_s))
                        append(note)
                    }

                else ->
                    textBuildIndicatorsAnnotated2(
                        context = context,
                        intRes = R.string.animal_indicators_weight_decreased_s,
                        totalValue = totalValue,
                        suffix = suffix,
                        isPlus = false,
                        note = note
                    )
            },
            style = text_16,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun SizeBottomSheet(
    state: DomainAnimalWeight,
    error: AnimalWeightState.Error,
    isEntry: Boolean,
    onIntent: (AnimalWeightIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = { onIntent(AnimalWeightIntent.EndDialogClicked) },
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.modifierBottomSheet()) {
            OutlinedTextCount2(
                value = state.weight,
                onValueChange = {
                    onIntent(AnimalWeightIntent.WeightChanged(it))
                },
                suffix = state.suffix,
                onSuffixChange = {
                    onIntent(AnimalWeightIntent.SuffixClicked(it))
                },
                isError = error.isErrorWeight,
                drawableRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
                intRes = R.string.weight_screen_title,
                intResSup = R.string.support_text_weight_animal,
                intResError = R.string.error_no_weight_animal,
                keyboardOptions = keyboardOptionsNextNumber(),
                versionDropMenu = DropdownMenu.WEIGHT,
                isWarehouseShow = false,
                cardBorder = false
            )
            OutlinedTextDate(
                value = state.date,
                onValueChange = { onIntent(AnimalWeightIntent.DateClicked(it)) },
                isCardBorder = false
            )
            OutlinedTextNote(
                value = state.note,
                onValueChange = { onIntent(AnimalWeightIntent.NoteChanged(it)) },
                cardBorder = false
            )
            ButtonPanel(
                isEntry = isEntry,
                entryButton = R.string.button_add,
                onClickInsert = { onIntent(AnimalWeightIntent.InsertPressed) },
                onClickUpdate = { onIntent(AnimalWeightIntent.UpdatePressed) },
                onClickDelete = { onIntent(AnimalWeightIntent.DeletePressed) }
            )
        }
    }
}

