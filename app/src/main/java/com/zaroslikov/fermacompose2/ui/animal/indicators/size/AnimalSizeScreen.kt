@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.size

import androidx.annotation.StringRes
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.convertSize
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.animal.indicators.AnimalIndicatorsCardNew
import com.zaroslikov.fermacompose2.ui.animal.indicators.InventoryAnimalBody
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenu
import com.zaroslikov.fermacompose2.ui.elements.FloatButton
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCount2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.textBold_18
import com.zaroslikov.fermacompose2.ui.elements.textBuildIndicatorsAnnotated2
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanel
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlin.math.absoluteValue


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
                itemList = state.sizeList,
                onInsertClick = { viewModel.onIntent(AnimalSizeIntent.OpenDialogClicked(isEntry = true)) },
                onEditClick = {
                    viewModel.onIntent(AnimalSizeIntent.OpenDialogClicked(isEntry = false, it))
                },
                onDeleteClick = { TODO() }
            )

        if (state.isOpenDialog)
            SizeBottomSheet(
                state = state.domainAnimalSize,
                onIntent = viewModel::onIntent,
                error = state.error,
                isEntry = state.isEntry,
            )
    }
}


@Composable
private fun AnimalSizeContainer2(
    modifier: Modifier,
    itemList: List<DomainAnimalSize>,
    onInsertClick: () -> Unit,
    onEditClick: (DomainAnimalSize) -> Unit,
    onDeleteClick: (Long) -> Unit,
) {
    InventoryAnimalBody(
        modifier = modifier,
        itemList = itemList,
        onInsertClick = onInsertClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        titleRes = R.string.message_no_date_title_height,
        messageRes = R.string.message_no_date_message_height,
        supportRes = R.string.message_no_date_support_height,
        buttonRes = R.string.button_sale_message_no_height
    ) { item, previous ->
        AnimalIndicatorsCardNew(
            modifier = modifier,
            domainAnimalSize = item,
            previousDomainAnimalSize = previous,
            onEditClick = { onEditClick(item) },
            onDeleteClick = { onDeleteClick(item.id) }
        )
    }
}

@Composable
private fun AnimalSizeContainer(
    modifier: Modifier = Modifier,
    state: AnimalSizeState,
    onIntent: (AnimalSizeIntent) -> Unit
) {
    if (state.sizeList.isNotEmpty())
        VaccinationList2(
            modifier = modifier,
            indicatorsList = state.sizeList,
            onEditChange = { onIntent(AnimalSizeIntent.OpenDialogClicked(isEntry = false, it)) }
        )
    else MessageNoData(
        modifier = modifier,
        onClick = { onIntent(AnimalSizeIntent.OpenDialogClicked(isEntry = true)) },
        titleRes = R.string.message_no_date_title_height,
        messageRes = R.string.message_no_date_message_height,
        supportRes = R.string.message_no_date_support_height,
        buttonRes = R.string.button_sale_message_no_height
    )
}

@Composable
private fun VaccinationList2(
    modifier: Modifier,
    indicatorsList: List<DomainAnimalSize>,
    onEditChange: (DomainAnimalSize) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        item { HeadingIndicators(R.string.height_screen_title) }
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
                text = "${domainAnimalSize.size.toFormatNumber()} ${stringResource(domainAnimalSize.suffix.toResId())}",
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
                        painterResource(if (details) R.drawable.icon_keyboard_arrow_up else R.drawable.icon_keyboard_arrow_down),
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
    domainAnimalSize: DomainAnimalSize,
    previousDomainAnimalSize: DomainAnimalSize
) {
    val context = LocalContext.current

    val size = domainAnimalSize.size.toConvertZeroDouble()
    val previousSize = previousDomainAnimalSize.size.toConvertZeroDouble()

    val suffix = domainAnimalSize.suffix
    val suffixPrevious = previousDomainAnimalSize.suffix

    val note = if (domainAnimalSize.note != "") "\n${domainAnimalSize.note}" else ""

    val sizeConverted = size.convertSize(suffix, to = Suffix.MILLIMETERS)
    val previousCountConverted = previousSize.convertSize(suffixPrevious, to = Suffix.MILLIMETERS)

    val totalValue = (sizeConverted - previousCountConverted).convertSize(
        Suffix.MILLIMETERS,
        suffix
    ).absoluteValue.formatNumber()

    Row {
        Text(
            modifier = Modifier.weight(1f),
            text = when {
                size > previousSize ->
                    textBuildIndicatorsAnnotated2(
                        context = context,
                        intRes = R.string.animal_indicators_size_increased_s,
                        totalValue = totalValue,
                        suffix = suffix,
                        isPlus = true,
                        note = note
                    )

                size == previousSize ->
                    buildAnnotatedString {
                        append(stringResource(R.string.animal_indicators_size_not_changed_s))
                        append(note)
                    }

                else ->
                    textBuildIndicatorsAnnotated2(
                        context = context,
                        intRes = R.string.animal_indicators_size_decreased_s,
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
    state: DomainAnimalSize,
    error: AnimalSizeState.Error,
    isEntry: Boolean,
    onIntent: (AnimalSizeIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = { onIntent(AnimalSizeIntent.EndDialogClicked) },
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.modifierBottomSheet(true)) {
            OutlinedTextCount2(
                value = state.size,
                onValueChange = {
                    onIntent(AnimalSizeIntent.SizeChanged(it))
                },
                suffix = state.suffix,
                onSuffixChange = {
                    onIntent(AnimalSizeIntent.SuffixClicked(it))
                },
                isError = error.isErrorSize,
                drawableRes = R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24,
                intRes = R.string.height_screen_title,
                intResSup = R.string.support_text_height_animal,
                intResError = R.string.error_no_height_animal,
                keyboardOptions = keyboardOptionsNextNumber(),
                versionDropMenu = DropdownMenu.HEIGHT,
                isWarehouseShow = false,
                cardBorder = false
            )
            OutlinedTextDate(
                value = state.date,
                onValueChange = { onIntent(AnimalSizeIntent.DateClicked(it)) },
                isCardBorder = false
            )
            OutlinedTextNote(
                value = state.note,
                onValueChange = { onIntent(AnimalSizeIntent.NoteChanged(it)) },
                cardBorder = false
            )
            ButtonPanel(
                isEntry = isEntry,
                entryButton = R.string.button_add,
                onClickInsert = { onIntent(AnimalSizeIntent.InsertPressed) },
                onClickUpdate = { onIntent(AnimalSizeIntent.UpdatePressed) },
                onClickDelete = { onIntent(AnimalSizeIntent.DeletePressed) }
            )
        }
    }
}

@Composable
fun HeadingIndicators(
    @StringRes titleRes: Int,
    isVaccination: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(titleRes),
            style = textBold_18,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.outlined_text_date),
            style = textBold_18,
            textAlign = TextAlign.Center
        )
        if (isVaccination)
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.support_text_data_next_vaccination),
                style = textBold_18,
                textAlign = TextAlign.Center
            )
        Spacer(modifier = Modifier.weight(0.25f))
    }
}
