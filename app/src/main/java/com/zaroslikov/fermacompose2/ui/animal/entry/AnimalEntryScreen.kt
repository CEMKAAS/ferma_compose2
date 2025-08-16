@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.entry


import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.supportFun.calculatePriceAll
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAnimal
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsDown
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.supportFun.metricalAnimal
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.composeElement.AnimalNameOutlinedText
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.CheckboxTextIcon
import com.zaroslikov.fermacompose2.ui.composeElement.ErrorSupportTextSlash
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextSex
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd
import com.zaroslikov.fermacompose2.ui.composeElement.RadioButtonRow
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_16
import com.zaroslikov.fermacompose2.ui.composeElement.toOutlinedText


object AnimalEntryDestination : NavigationDestination {
    override val route = "animalEntry"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun AnimalEntryProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: AnimalEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val typeList by viewModel.typeUiState.collectAsState()
    var titleAppBarTry by rememberSaveable { mutableIntStateOf(R.string.animal_add_screen_title) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = titleAppBarTry,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        AnimalEntryContainer(
            modifier = Modifier
                .modifierScreen(innerPadding),
            onValueChange = viewModel::updateUiState,
            idPT = viewModel.itemId,
            typeList = typeList.list,
            saveInRoomSale = {
                viewModel.saveItem(
                    animalTable = it.first,
                    animalCountTable = it.second,
                    expensesTable = it.third
                )
                onNavigateUp()
            },
            titleAppBar = {
                titleAppBarTry =
                    if (it) R.string.animals_add_screen_title else R.string.animal_add_screen_title
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalEntryContainer(
    idPT: Long,
    modifier: Modifier,
    state: AnimalEntryState,
    onValueChange: (AnimalEntryState) -> Unit,
    typeList: List<String>,
    saveInRoomSale: (Triple<AnimalTable, AnimalCountTable, ExpensesTable?>) -> Unit,
    titleAppBar: (Boolean) -> Unit
) {

    val focusManager = LocalFocusManager.current

    var title by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("") }
    var sex by rememberSaveable { mutableStateOf("Мужской") }
    var isAnimalGroup by rememberSaveable { mutableStateOf(false) } // true group
    var count by rememberSaveable { mutableStateOf("1") }
    var note by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var dateBorn by rememberSaveable { mutableStateOf(dateToday()) }
    var dateFactory by rememberSaveable { mutableStateOf(dateBorn) }
    var foodDay by rememberSaveable { mutableStateOf("") }
    var suffixFoodDay by rememberSaveable { mutableStateOf("кг.") }
    var isDateFactory by rememberSaveable { mutableStateOf(true) }
    var isAutoCalculate by rememberSaveable { mutableStateOf(false) }

    //Error
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorType by rememberSaveable { mutableStateOf(false) }

    val formattedDateList = if (isDateFactory) dateBorn.split(".") else dateFactory.split(".")
    val category = stringResource(R.string.animal_card_screen_add_category_expenses)
    val suffixCount = stringResource(R.string.suffix_pieces)


    val finalPrice = if (isAutoCalculate && isAnimalGroup) calculatePriceAll(price, count)
    else price


    val factoryPadding by animateDpAsState(
        if (!isDateFactory) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Column(modifier = modifier) {
        CardField(
            modifier = Modifier
                .padding(bottom = 4.dp),
            row = false
        ) {
            Text(
                text = stringResource(R.string.animal_entry_screen_info_animal),
                style = textBold_16
            )
            RadioButtonRow(
                state = isAnimalGroup,
                onStateSelect = {
                    isAnimalGroup = it
                    titleAppBar(isAnimalGroup)
                },
                intResOne = R.string.ration_button_one,
                intResTwo = R.string.ration_button_many,
                imageOne = R.drawable.baseline_fiber_manual_record_24,
                imageTwo = R.drawable.baseline_spoke_24
            )
        }
        AnimalNameOutlinedText(
            value = state.title,
            onValueChange = { onValueChange(state.updateTitle(it)) },
            isAnimalGroup = state.isAnimalGroup,
            isErrorTitle = state.error.isErrorTitle
        )

        OutlinedTextTitleAdd(
            intRes = R.string.outlined_text_type,
            intResSup = if (!state.isAnimalGroup) R.string.support_text_type_animal
            else R.string.support_text_type_animals,
            intResError = R.string.error_no_type_animal,
            value = state.type,
            onValueChange = { onValueChange(state.updateType(it)) },
            drawableRes = R.drawable.baseline_pets_24,
            titleList = typeList,
            isErrorTitle = state.error.isErrorType,
            isErrorSlash = false,
        )
        if (!isAnimalGroup)
            OutlinedTextSex(
                value = state.sex,
                onValueChange = { onValueChange(state.updateSex(it)) },
            )
        else OutlinedTextCount(
            value = state.count,
            onValueChange = { onValueChange(state.updateCount(it)) },
            drawableRes = R.drawable.baseline_spoke_24,
            isError = state.error.isErrorCount,
            suffix = stringResource(R.string.suffix_pieces),
            intRes = R.string.outlined_text_field_quantity,
            intResSup = R.string.support_text_count_animals,
            isWarehouseShow = false,
            isDropMenuShow = false,
        )
        OutlinedPriceInput(
            price = state.price,
            onPriceChange = { onValueChange(state.updatePrice(it)) },
            count = state.priceAll,
            isAutoCalculate = state.isAutoPrice,
            onAutoCalculate = { onValueChange(state.updateIsAutoPrice(it)) },
            isManyCount = state.isAnimalGroup,
        )
        OutlinedTextDate(
            value = state.dateBorn,
            intRes = R.string.outlined_text_date_born,
            intResSup = if (!state.isAnimalGroup) R.string.outlined_text_date_born
            else R.string.support_text_date_born_s,
            onValueChange = { onValueChange(state.updateDate(it)) }
        )
        CardField(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .padding(bottom = factoryPadding.coerceAtLeast(0.dp)),
            row = false
        ) {
            CheckboxTextIcon(
                modifier = if (!state.isDateFactory) Modifier.toOutlinedText() else Modifier,
                checked = state.isDateFactory,
                onCheckedChange = {
                    onValueChange(state.updateIsDateFactory(it))
                },
                intTitle = if (!state.isAnimalGroup) R.string.checkbox_born else R.string.checkbox_born_s,
                isTooltipShow = true,
                intTooltip = R.string.tooltip_animals_born
            )
            if (!state.isDateFactory) {
                OutlinedTextDate(
                    value = state.dateFactory,
                    intRes = R.string.outlined_text_date_factory,
                    intResSup = if (!state.isAnimalGroup) R.string.support_text_date_factory else R.string.support_text_date_factory_s,
                    drawableRes = R.drawable.baseline_event_24,
                    onValueChange = { onValueChange(state.updateDateFactory(it)) }
                )
            }
        }
        OutlinedTextCount(
            value = state.foodDay,
            onValueChange = { onValueChange(state.updateFoodDay(it)) },
            isError = false,
            onSuffixChange = { onValueChange(state.updateFoodDaySuffix(it)) },
            intRes = R.string.outlined_food_day_animals,
            intResSup = if (!state.isAnimalGroup) R.string.support_text_food_day_animal else R.string.support_text_food_day_animals,
            suffix = state.foodDaySuffix,
            isWarehouseShow = false,
            isDropMenuShow = true,
            versionDropMenu = 0
        )
        OutlinedTextNote(
            value = state.note,
            onValueChange = { onValueChange(state.updateNote(it)) },
        )
        ButtonStandart(
            intRes = R.string.button_add,
            onClick = {
                if (isErrorAnimal(
                        title = title,
                        type = type,
                        count = count,
                        isGroupAnimal = isAnimalGroup,
                        isErrorTitle = { isErrorTitle = it },
                        isErrorCount = { isErrorCount = it },
                        isErrorType = { isErrorType = it }
                    )
                ) {
                    saveInRoomSale(
                        Triple(
                            AnimalTable(
                                name = title,
                                type = type,
                                data = dateBorn,
                                dateFactory = if (!isDateFactory) dateFactory else "",
                                groop = isAnimalGroup,
                                sex = sex,
                                note = note,
                                image = "0",
                                arhiv = false,
                                price = if (finalPrice == "") 0.0 else finalPrice.toConvertDb()
                                    .toDouble(),
                                foodDay = if (foodDay == "") 0.0 else foodDay.toConvertDb()
                                    .toDouble(),
                                suffixFoodDay = suffixFoodDay,
                                idPT = idPT.toInt()
                            ),
                            AnimalCountTable(
                                count = if (count == "") "1" else count,
                                suffix = suffixCount,
                                date = if (!isDateFactory) dateFactory else dateBorn,
                                idAnimal = idPT.toInt(),
                                note = "",//todo
                                version = 1
                            ),
                            if (finalPrice != "")
                                ExpensesTable(
                                    title = title,
                                    count = count.toConvertDb().toDouble(),
                                    day = formattedDateList[0].toInt(),
                                    mount = formattedDateList[1].toInt(),
                                    year = formattedDateList[2].toInt(),
                                    price = finalPrice.toDouble(),
                                    countSuffix = suffixCount,
                                    category = category,
                                    note = "",
                                    isShowFood = false,
                                    isShowWarehouse = false,
                                    isShowAnimals = false,
                                    isShowFoodHand = false,
                                    feedFood = 0.0,
                                    countAnimal = 0,
                                    foodDesignedDay = 0,
                                    lastDayFood = "",
                                    idPT = idPT,
                                    priceAll = 0.0,
                                    feedFoodSuffix = "",
                                    weight = 0.0,
                                    weightSuffix = "",
                                    isAutoWeight = false,
                                    isAutoPrice = false,
                                ) else null
                        )
                    )
                    metricalAnimal(title, type)
                }
            }
        )
    }
}
