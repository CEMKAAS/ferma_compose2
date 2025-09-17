@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.size

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.DomainIndicatorsVM
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
//import com.zaroslikov.fermacompose2.ui.animal.HeadingIndicators
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.FloatButton
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCount2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination


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
    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.height_screen_title,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatButton {
                viewModel.onIntent(
                    AnimalSizeIntent.AddOpenDialogClicked(
                        true
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
            AnimalSizeContainer(
                modifier = Modifier.modifierScreenLazy(innerPadding),
                state = state.value,
                onIntent = viewModel::onIntent
            )

        if (state.value.isAddOpenDialog)
            IndicatorsBottomSheet(
                state = state.value,
                onIntent = viewModel::onIntent
            )
    }
}

@Composable
fun AnimalSizeContainer(
    modifier: Modifier = Modifier,
    state: AnimalSizeState,
    onIntent: (AnimalSizeIntent) -> Unit
) {
    if (state.sizeList.isNotEmpty())
        VaccinationList2(
            modifier = modifier,
            onValueChange = {
//                onValueChange(it.first)
//                isLastCount = it.second
//                isFirstCount = it.third
            },
            onAddClick = { onIntent(AnimalSizeIntent.EditOpenDialogClicked(true)) },
            indicatorsList = state.sizeList
        )
    else MessageNoData(
        modifier = modifier,
        onClick = { onIntent(AnimalSizeIntent.AddOpenDialogClicked(true)) },
        titleRes = R.string.message_no_date_title_weight,
        messageRes = R.string.message_no_date_message_height,
        supportRes = R.string.message_no_date_support_height,
        buttonRes = R.string.button_sale_message_no_height
    )
}

@Composable
fun VaccinationList2(
    modifier: Modifier,
    onAddClick: () -> Unit,
    indicatorsList: List<DomainAnimalSize>,
    onValueChange: (Triple<DomainIndicatorsVM, Boolean, Boolean>) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
    ) {
//        item { HeadingIndicators(2) }
        itemsIndexed(items = indicatorsList, key = { index, _ -> index }) { index, item ->
            /* val previousItem =
                 if (index < cumulativeList.size - 1) cumulativeList[index + 1] else null*/
            SizeCard(
                modifier = Modifier,
                domainAnimalSize = item,
                previousDomainAnimalSize = item


                /*Modifier
                    .clickable {
                        onValueChange(
//                            Triple(item, index == (cumulativeList.size - 1), index == 0)
                        )
                    },*/
            )
        }
    }
}

@Composable
fun SizeCard(
    modifier: Modifier = Modifier,
    domainAnimalSize: DomainAnimalSize,
    previousDomainAnimalSize: DomainAnimalSize?
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
                text = "${domainAnimalSize.size.toFormatNumber()} ${domainAnimalSize.suffix}",
                style = textBold_16,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.weight(1f),
                text = domainAnimalSize.date,
                style = textBold_16,
                textAlign = TextAlign.Center
            )
            IconButton(
                modifier = Modifier.weight(0.25f),
                onClick = { details = !details }) {
                Icon(
                    imageVector = if (details) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }
        /*if (details)
            Row {
                Text(
                    modifier = Modifier.weight(1f),
                    text = lastValue(
                        version = version,
                        domainIndicatorsVM,
                        previousDomainIndicatorsVM
                    ),
                    style = text_16,
                    textAlign = TextAlign.Start
                )
            }*/
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicatorsBottomSheet(
    state: AnimalSizeState,
    onIntent: (AnimalSizeIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = { onIntent(AnimalSizeIntent.AddOpenDialogClicked(false)) },
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.modifierBottomSheet()) {
            OutlinedTextCount2(
                value = state.size,
                onValueChange = {
                    onIntent(AnimalSizeIntent.SizeChanged(it))
                },
                suffix = state.suffix,
                onSuffixChange = {
                    onIntent(AnimalSizeIntent.SuffixClicked(it))
                },
                isError = state.error.isError,
                drawableRes = R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24,
                intRes = R.string.height_screen_title,
                intResSup = R.string.support_text_height_animal,
                intResError = R.string.error_no_count_product,
                keyboardOptions = keyboardOptionsNextNumber(),
                cardBorder = false
            )
            OutlinedTextDate(
                value = state.date,
//                initialSelectedDateMilli = indicationUiState?.let { formatDateToLong(it.date) },
                onValueChange = { onIntent(AnimalSizeIntent.DateClicked(it)) },
                cardBorder = false
            )
            OutlinedTextNote(
                value = state.note,
                onValueChange = { onIntent(AnimalSizeIntent.NoteChanged(it)) },
                cardBorder = false
            )
        }
        /*      if (openDialogGroup)
                  AlertDialogGroupToSolo(
                      onConfirmation = {
                          openDialogGroup = !openDialogGroup
                          scope.launch {
                              sheetState.hide()
                              addBottomSheet.value = false
                          }
                      },
                      onUpdateClick = { onUpdateAnimalGroupClick(it) }
                  )*/
    }
}

/*
@Composable
private fun ButtonPanel() {
    if (indicationUiState == null) {
        ButtonStandart(
            intRes = R.string.button_insert,
            onClick = {
                if (isErrorVersion(
                        version = version,
                        title = count,
                        countAll = countAnimal,
                        isAnimalGroup = animalUiState?.group == true,
                        isErrorTitle = { isErrorCount = it },
                        isErrorCountAnimal = { isErrorCountAnimal = it }
                    )
                ) {
                    focusManager.clearFocus()
                    val (countAnimalForDb, priceForDb, noteForDb) = prepareForDb(
                        version = version,
                        price = price,
                        countAnimal = count,
                        countAnimalVaccination = countAnimal,
                        suffix = suffix,
                        note = note,
                        isAutoCalculate = isAutoCalculate,
                        isAnimalGroup = animalUiState?.group ?: false,
                        context = context,
                        animalCountUiState = animalCountUiState,
                        previousItem = previousItem,
                    )
                    println("id: ${idAnimal.toInt()}")
                    onSaveClick(
                        Triple(
                            DomainIndicatorsVM(
                                weight = count,
                                date = date,
                                suffix = if (version == 3) dateNext else suffix,
                                idAnimal = idAnimal.toInt(),
                                note = noteForDb
                            ),
                            priceForDb,
                            countAnimalForDb
                        )
                    )
                    if (version == 2 && count.toConvertZeroDouble() == 1.0) {
                        openDialogGroup = true

                    } else {
                        scope.launch {
                            sheetState.hide()
                            addBottomSheet.value = false
                        }
                    }
                }
            }
        )
    } else {
        ButtonRefresh {
            if (isErrorVersion(
                    version = version,
                    title = count,
                    countAll = countAnimal,
                    isAnimalGroup = animalUiState?.group == true,
                    isErrorTitle = { isErrorCount = it },
                    isErrorCountAnimal = { isErrorCountAnimal = it }
                )
            ) {
                focusManager.clearFocus()
                val (countAnimalForDb, priceForDb, noteForDb) = prepareForDb(
                    version = version,
                    price = price,
                    countAnimal = count,
                    countAnimalVaccination = countAnimal,
                    suffix = suffix,
                    note = note,
                    isAutoCalculate = isAutoCalculate,
                    isAnimalGroup = animalUiState?.group ?: false,
                    context = context,
                    animalCountUiState = animalCountUiState,
                    previousItem = previousItem,
                )
                onUpdateClick(
                    Triple(
                        indicationUiState.copy(
                            weight = count,
                            date = date,
                            suffix = if (version == 3) dateNext else suffix,
                            note = noteForDb
                        ),
                        priceForDb,
                        countAnimalForDb
                    )
                )
                println("version: $version")
                println("isFirstCount : $isFirstCount")
                println("count: ${count.toConvertZeroDouble()}")
                println(count.toConvertZeroDouble() == 1.0)
                println(version == 2 && isFirstCount && count.toConvertZeroDouble() == 1.0)

                if (version == 2 && isFirstCount && count.toConvertZeroDouble() == 1.0) {
                    openDialogGroup = true
                } else {
                    scope.launch {
                        sheetState.hide()
                        addBottomSheet.value = false
                    }
                }
            }
        }
        println("isLastCount: $isLastCount")
        if (!(version == 2 && isLastCount))
            ButtonDelete {
                onDeleteClick(indicationUiState)
                scope.launch {
                    sheetState.hide()
                    addBottomSheet.value = false
                }
            }
    }
}
*/
