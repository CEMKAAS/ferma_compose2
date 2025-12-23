@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import android.util.Log
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toIndicationStatus
import com.zaroslikov.fermacompose2.ui.animal.indicators.AnimalCountCardNew
import com.zaroslikov.fermacompose2.ui.animal.indicators.InventoryAnimalBody
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogGroupToSolo
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.FabMenu2
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination

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
                onInsertClick = { TODO() },
                onEditClick = {
                    viewModel.onIntent(
                        AnimalCountIntent.DialogClicked(false, item = it)
                    )
                },
                onDeleteClick = {
                    viewModel.onIntent(
                        AnimalCountIntent.DeleteCountPressed(
                            it.first,
                            it.second
                        )
                    )
                },
                /*  onDetailClick = { viewModel.updateDetailCardKill(it) }*/
            )

        if (state.isOpenDialog) {
            Log.i("count23", "AnimalCountScreen: ${state.currentAnimal.count} ")
            when (state.currentProduct.version) {
                AnimalCountVersion.SALE ->
                    BottomSheetSaleAnimal(
                        state = state.currentProduct,
                        onIntent = viewModel::onIntent,
                        countAllAnimal = state.currentAnimal.count,
                    )

                AnimalCountVersion.EXPENSES ->
                    BottomSheetExpensesAnimal(
                        state = state.currentProduct,
                        countAllAnimal = state.currentAnimal.count,
                        countSuffix = state.currentAnimal.suffix,
                        onIntent = viewModel::onIntent,
                    )

                AnimalCountVersion.KILL ->
                    BottomSheetKillAnimal(
                        state = state.currentProduct,
                        onIntent = viewModel::onIntent,
                        productKill = state.currentProduct.productKillList,
                        isAnimalGroup = true,
                        countAnimalAll = state.currentAnimal.count,
                        countSuffix = state.currentAnimal.suffix,
                        weight = state.weight,
                    )

                AnimalCountVersion.WRITE_OFF ->
                    BottomSheetWriteOffAnimal(
                        state = state.currentProduct,
                        countAllAnimal = state.currentAnimal.count,
                        countSuffix = state.currentAnimal.suffix,
                        onIntent = viewModel::onIntent,
                    )

                AnimalCountVersion.ADD ->
                    BottomSheetAddAnimal(
                        state = state.currentProduct,
                        countAllAnimal = state.currentAnimal.count,
                        onIntent = viewModel::onIntent
                    )

                AnimalCountVersion.INCUBATOR -> TODO()
            }
        }
        if (state.openSoloDialog)
            AlertDialogGroupToSolo(
                sex = state.animal.sex,
                onUpdateSex = { viewModel.onIntent(AnimalCountIntent.SexClicked(it)) },
                onConfirmation = { viewModel.onIntent(AnimalCountIntent.DialogSoloClicked(false)) },
                onSave = { viewModel.onIntent(AnimalCountIntent.SaveGroupPressed) }
            )
        if (state.openWarningDialog) {
            val textWarning = stringResource(
                when (state.openWarningDeleteAllDialog) {
                    WarningAnimalCount.DELETE -> R.string.animal_count_screen_warning_product_delete_all_text
                    WarningAnimalCount.DELETE_MINUS -> R.string.animal_count_screen_warning_minus_and_delete_text
                    WarningAnimalCount.UPDATE -> R.string.animal_count_screen_warning_product_update_text
                    WarningAnimalCount.UPDATE_MINUS -> R.string.animal_count_screen_warning_product_update_minus_text
                    WarningAnimalCount.MINUS -> R.string.animal_count_screen_warning_text
                }
            )
            AlertDialogWarningAnimal(
                textWarning = textWarning,
                onConfirmationClick = { viewModel.onIntent(AnimalCountIntent.WarningConrPressed) },
                onDismissClick = { viewModel.onIntent(AnimalCountIntent.WarningEndDialogClicked) }
            )
        }
    }
}

@Composable
private fun AnimalCountContainer2(
    modifier: Modifier,
    @StringRes titleRes: Int,
    itemList: List<DomainAnimalCountPriceUi>,
    onInsertClick: () -> Unit,
    onEditClick: (DomainAnimalCountPriceUi) -> Unit,
    onDeleteClick: (Pair<Long, Boolean>) -> Unit,
) {
    InventoryAnimalBody(
        modifier = modifier,
        itemList = itemList,
        onInsertClick = onInsertClick,
        titleRes2 = titleRes,
        titleRes = R.string.message_no_date_title_count,
        messageRes = R.string.message_no_date_message_count,
        supportRes = R.string.message_no_date_support_count,
        buttonRes = R.string.button_sale_message_no_count
    ) { item, previous ->
        AnimalCountCardNew(
            icon = item.version?.toDrawRes() ?: AnimalCountVersion.ADD.toDrawRes(),
            colors = item.version?.toColorList() ?: AnimalCountVersion.ADD.toColorList(),
            value = item.count,
            suffix = item.suffix,
            price = item.priceAll ?: item.price,
            date = item.date,
            note = item.note,
            onEditClick = { onEditClick(item) },
            onDeleteClick = { onDeleteClick(item.id to (item.version == AnimalCountVersion.KILL)) },
            indicationStatus = item.version?.toIndicationStatus()
                ?: AnimalCountVersion.ADD.toIndicationStatus(),
            productKill = item.productKill
        )
    }
}


/*@Composable
private fun AnimalCountContainer(
    modifier: Modifier = Modifier,
    state: AnimalCountState,
    onIntent: (AnimalCountIntent) -> Unit
) {
    if (state.countList.isNotEmpty())
        VaccinationList2(
            modifier = modifier,
            indicatorsList = state.countList,
            onEditClick = { onIntent(AnimalCountIntent.DialogClicked(false, it.first, it.second)) }
        )
    else MessageNoData(
        modifier = modifier,
        titleRes = R.string.message_no_date_title_count,
        messageRes = R.string.message_no_date_message_count,
        supportRes = R.string.message_no_date_support_count,
        buttonRes = R.string.button_sale_message_no_count
    )
}*/

/*@Composable
private fun VaccinationList2(
    modifier: Modifier,
    indicatorsList: List<DomainAnimalCountPrice>,
    onEditClick: (Pair<DomainAnimalCountPrice, Boolean>) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        item { HeadingIndicators(R.string.count_screen_title) }
        itemsIndexed(items = indicatorsList, key = { index, _ -> index }) { index, item ->
            val previousItem =
                if (index < indicatorsList.size - 1) indicatorsList[index + 1] else null
            CountCard(
                modifier = Modifier.clickable {
                    onEditClick(item to (item.version == AnimalCountVersion.KILL))
                },
                domainAnimalCount = item,
                previousDomainAnimalCount = previousItem
            )
        }
    }
}*/

