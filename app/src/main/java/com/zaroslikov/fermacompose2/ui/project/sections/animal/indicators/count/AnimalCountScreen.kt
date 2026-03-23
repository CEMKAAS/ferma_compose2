@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.green_15
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toIndicationStatus
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogGroupToSolo
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.FabMenu2
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.AnimalCountCardNew
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.InventoryAnimalBody

object AnimalCountDestination : NavigationDestination {
    override val route = "animalCount"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

@Composable
fun AnimalCountScreen(
    navigateBack: () -> Unit,
    viewModel: AnimalCountViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val title = R.string.count_screen_title

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
            FabMenu2(
                isListEmpty = state.countList.isNotEmpty(),
                onClick = { viewModel.onIntent(AnimalCountIntent.DialogClicked(true, it)) }
            )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            AnimalCountContainer2(
                modifier = Modifier.modifierScreenLazy(innerPadding),
                titleRes = title,
                itemList = state.countList,
                onEditClick = {
                    viewModel.onIntent(
                        AnimalCountIntent.DialogClicked(true, item = it)
                    )
                },
                onDeleteClick = {
                    viewModel.onIntent(AnimalCountIntent.DeleteCountPressed(it))
                }
            )

        if (state.isOpenEntryBottomSheet) {
            when (state.currentProduct.version) {
                AnimalCountVersion.SALE ->
                    BottomSheetSaleAnimal(
                        state = state.currentProduct,
                        onIntent = viewModel::onIntent,
                        countAllAnimal = state.countAnimal,
                    )

                AnimalCountVersion.EXPENSES ->
                    BottomSheetExpensesAnimal(
                        state = state.currentProduct,
                        countAllAnimal = state.countAnimal,
                        countSuffix = state.countAnimalSuffix,
                        onIntent = viewModel::onIntent,
                    )

                AnimalCountVersion.KILL ->
                    BottomSheetKillAnimal(
                        state = state.currentProduct,
                        onIntent = viewModel::onIntent,
                        countAnimalAll = state.countAnimal,
                        countSuffix = state.countAnimalSuffix
                    )

                AnimalCountVersion.WRITE_OFF ->
                    BottomSheetWriteOffAnimal(
                        state = state.currentProduct,
                        countAllAnimal = state.countAnimal,
                        countSuffix = state.countAnimalSuffix,
                        onIntent = viewModel::onIntent,
                    )

                AnimalCountVersion.ADD ->
                    BottomSheetAddAnimal(
                        state = state.currentProduct,
                        countAllAnimal = state.countAnimal,
                        onIntent = viewModel::onIntent
                    )

                AnimalCountVersion.INCUBATOR -> Unit
            }
        }
        if (state.openSoloDialog)
            AlertDialogGroupToSolo(
                sex = state.animal.sex,
                onUpdateSex = { viewModel.onIntent(AnimalCountIntent.SexClicked(it)) },
                onDismissRequest = {
                    viewModel.onIntent(
                        AnimalCountIntent.OpenSoloDialogClicked(
                            false
                        )
                    )
                },
                onSave = { viewModel.onIntent(AnimalCountIntent.SaveGroupPressed) }
            )
        if (state.openWarningDialog)
            AlertDialogWarningAnimal(
                textWarning = state.textWarning,
                onConfirmationClick = {
                    viewModel.onIntent(
                        AnimalCountIntent.WarningEndDialogClicked(true)
                    )
                },
                onDismissRequest = {
                    viewModel.onIntent(
                        AnimalCountIntent.WarningEndDialogClicked(false)
                    )
                }
            )
    }
}

@Composable
private fun AnimalCountContainer2(
    modifier: Modifier,
    @StringRes titleRes: Int,
    itemList: List<DomainAnimalCountPriceUi>,
    onEditClick: (DomainAnimalCountPriceUi) -> Unit,
    onDeleteClick: (Long) -> Unit,
) {
    InventoryAnimalBody(
        modifier = modifier,
        itemList = itemList,
        titleRes2 = titleRes,
        titleRes = R.string.message_no_date_title_count,
        messageRes = R.string.message_no_date_message_count,
        iconRes = R.drawable.baseline_spoke_24,
        iconColor = animal_1,
        backgroundColor = green_15,
        detailCard = { item ->
            AnimalCountCardNew(
                icon = item.version?.toDrawRes() ?: AnimalCountVersion.ADD.toDrawRes(),
                colors = item.version?.toColorList() ?: AnimalCountVersion.ADD.toColorList(),
                value = item.count,
                suffix = item.suffix,
                price = item.priceAll ?: item.price,
                date = item.date,
                note = item.note,
                onEditClick = { onEditClick(item) },
                onDeleteClick = { onDeleteClick(item.id) },
                indicationStatus = item.version?.toIndicationStatus()
                    ?: AnimalCountVersion.ADD.toIndicationStatus(),
                productKill = item.productKill
            )
        }
    )
}