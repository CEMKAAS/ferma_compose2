@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.incubator_project.AddIncubator

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_11
import com.zaroslikov.fermacompose2.orang_12
import com.zaroslikov.fermacompose2.orang_15
import com.zaroslikov.fermacompose2.orang_8
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CardNewWithTitle
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedNumberNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDropdownMenuNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.warehouse.CurrencySettingsCard

object AddIncubatorDestination : NavigationDestination {
    override val route = "Add_incubator"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun AddIncubatorScreen(
    navigateBack: () -> Unit,
    viewModel: AddIncubatorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val eventFlow = viewModel.navigation

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
            }
        }
    }
    val titleRes = if (state.isEntry) R.string.add_incubator_screen_title else
        R.string.add_incubator_screen_title_edit

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = titleRes,
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
            AddIncubatorContainer(
                modifier = Modifier.modifierScreen(innerPadding),
                state = state,
                onIntent = viewModel::onIntent,
            )
    }
}

@Composable
private fun AddIncubatorContainer(
    modifier: Modifier,
    state: AddIncubatorState,
    onIntent: (AddIncubatorIntent) -> Unit,
) {
    Column(
        modifier = modifier.padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ImageCard()
        CurrencySettingsCard(state.currentProduct.currencySuffix) {
            onIntent(AddIncubatorIntent.CurrencyClicked(it))
        }
        EnterValue(state.currentProduct, onIntent)
        FunctionIncubatorCard(
            isAutoRotation = state.currentProduct.isAutoRotation,
            isAutoVentilation = state.currentProduct.isAutoVentilation,
            onAutoRotationCheckedChange = { onIntent(AddIncubatorIntent.AutoRotationClicked(it)) },
            onAutoVentilationCheckedChange = { onIntent(AddIncubatorIntent.AutoVentilationClicked(it)) }
        )
        SaveButton(
            isInsertProject = state.isEntry,
            enabledButton = state.currentProduct.enabledButton(),
            onInsertClick = { onIntent(AddIncubatorIntent.Insert) },
            onUpdateClick = { onIntent(AddIncubatorIntent.Update) }
        )
    }
}

@Composable
private fun ImageCard() {
    CardFieldNew(
        padding = PaddingValues(24.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconTransaction2(
                icon = R.drawable.outline_egg_24,
                sizeCard = 128.dp,
                boxColor = orang_8,
                iconColor = orang_12
            )
            Text(
                stringResource(R.string.add_incubator_screen_download_image),
                style = text_14,
                color = gray_7
            )
        }
    }
}

@Composable
private fun EnterValue(
    state: AddIncubator,
    onIntent: (AddIncubatorIntent) -> Unit
) {
    CardFieldNew(
        padding = PaddingValues(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextNew(
                value = state.title,
                onValueChange = { onIntent(AddIncubatorIntent.TitleChanged(it)) },
                isError = state.error.isErrorTitle,
                labelIntRes = R.string.add_incubator_screen_name_incubator,
                supportingText = R.string.add_incubator_screen_name_incubator_support,
                isBorderCard = false
            )
            OutlinedTextDropdownMenuNew(
                value = state.brand,
                onValueChange = { onIntent(AddIncubatorIntent.BrandChanged(it)) },
                titleList = state.brandList,
                labelIntRes = R.string.add_incubator_screen_brand,
                intResSup = R.string.add_incubator_screen_brand_support,
                isBorderCard = false
            )
            OutlinedTextDropdownMenuNew(
                value = state.model,
                onValueChange = { onIntent(AddIncubatorIntent.ModelChanged(it)) },
                titleList = state.modelList,
                labelIntRes = R.string.add_incubator_screen_model,
                intResSup = R.string.add_incubator_screen_model_support,
                isBorderCard = false
            )
            OutlinedNumberNew(
                value = state.capacity,
                onValueChange = { onIntent(AddIncubatorIntent.CapacityChanged(it)) },
                intRes = R.string.add_incubator_screen_capacity,
                intResSup = R.string.add_incubator_screen_capacity_egg,
                isError = state.error.isErrorCapacity,
                suffix = Suffix.PIECES,
                isBorderCard = false
            )
            OutlinedNumberNew(
                value = state.price,
                onValueChange = { onIntent(AddIncubatorIntent.PriceChanged(it)) },
                intRes = R.string.add_incubator_screen_incubator_sell,
                intResSup = R.string.add_incubator_screen_incubator_sell_support,
                suffix = Suffix.PIECES,
                isBorderCard = false
            )
            OutlinedTextDateNew(
                value = state.date,
                onValueChange = { onIntent(AddIncubatorIntent.DateClicked(it)) },
                intRes = R.string.add_incubator_screen_date_sale,
                isBorderCard = false
            )
            OutlinedTextNoteNew(
                value = state.note,
                onValueChange = { onIntent(AddIncubatorIntent.NoteClicked(it)) },
                labelIntRes = R.string.add_incubator_screen_note,
                supportingText = R.string.add_incubator_screen_note_support,
                isBorderCard = false
            )
        }
    }
}


@Composable
fun FunctionIncubatorCard(
    isAutoRotation: Boolean,
    isAutoVentilation: Boolean,
    onAutoRotationCheckedChange: (Boolean) -> Unit,
    onAutoVentilationCheckedChange: (Boolean) -> Unit
) {
    CardNewWithTitle(
        titleRes = R.string.add_incubator_screen_function_incubator
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FunctionIncubatorSecondCard(
                checked = isAutoRotation,
                onCheckedChange = onAutoRotationCheckedChange,
                titleRes = R.string.add_incubator_screen_automatic_egg_rotation,
                supportRes = R.string.add_incubator_screen_automatic_egg_rotation_support,
                borderColor = blue_9,
                containerColor = blue_3
            )
            FunctionIncubatorSecondCard(
                checked = isAutoVentilation,
                onCheckedChange = onAutoVentilationCheckedChange,
                titleRes = R.string.add_incubator_screen_auto_ventilation,
                supportRes = R.string.add_incubator_screen_auto_ventilation_support,
                borderColor = green_11,
                containerColor = price_green_2
            )
        }
    }
}

@Composable
private fun FunctionIncubatorSecondCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    @StringRes titleRes: Int,
    @StringRes supportRes: Int,
    borderColor: Color,
    containerColor: Color
) {
    BorderCard(
        borderColor = borderColor,
        containerColor = containerColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(titleRes), style = text_16, color = black_2)
                Text(stringResource(supportRes), style = text_12, color = gray_7)
            }
            Switch(
                checked = checked,
                onCheckedChange = { onCheckedChange(it) }
            )
        }
    }
}


@Composable
private fun SaveButton(
    isInsertProject: Boolean,
    enabledButton: Boolean,
    onInsertClick: () -> Unit,
    onUpdateClick: () -> Unit
) {
    GradientButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(if (isInsertProject) R.string.add_incubator_screen_save else R.string.button_text_edit_title),
        onClick = {
            if (isInsertProject) onInsertClick()
            else onUpdateClick()
        },
        colors = listOf(orang_9, orang_15),
        enabled = enabledButton,
        paddingValues = PaddingValues(vertical = 14.dp)
    )
}