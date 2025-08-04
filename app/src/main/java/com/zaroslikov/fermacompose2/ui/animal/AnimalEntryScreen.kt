@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal


import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.zaroslikov.fermacompose2.supportFun.toConvertZero
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
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
import java.time.Instant


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
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                isErrorTitle = it.isError()
            },
            label = { Text(text = stringResource(if (!isAnimalGroup) R.string.outlined_text_name_animal else R.string.outlined_text_name_animals)) },
            supportingText = {
                ErrorSupportTextSlash(
                    isError = isErrorTitle,
                    intRes = if (!isAnimalGroup) R.string.support_text_name_animal else R.string.support_text_names_animals,
                    intResError = if (!isAnimalGroup) R.string.error_no_name_animal else R.string.error_no_name_animals,
                )
            },
            modifier = Modifier.toOutlinedText(),
            isError = isErrorTitle,
            keyboardOptions = keyboardOptionsNext(),
            keyboardActions = keyboardActionsDown(focusManager)
        )
        OutlinedTextTitleAdd(
            intRes = R.string.outlined_text_type,
            intResSup = if (!isAnimalGroup) R.string.support_text_type_animal else R.string.support_text_type_animals,
            intResError = R.string.error_no_type_animal,
            value = type,
            onValueChange = {
                type = it.trim()
                isErrorType = it.isError()
            },
            drawableRes = R.drawable.baseline_pets_24,
            titleList = typeList,
            isErrorTitle = isErrorType,
            isErrorSlash = false,
            focusManager = focusManager
        )
        if (!isAnimalGroup)
            OutlinedTextSex(
                value = sex,
                onValueChange = { sex = it },
                focusManager = focusManager
            )
        else OutlinedTextCount(
            value = count,
            onValueChange = {
                count = it.toConvertDb()
                isErrorCount = it.isError()
            },
            drawableRes = R.drawable.baseline_spoke_24,
            isError = isErrorCount,
            suffix = stringResource(R.string.suffix_pieces),
            intRes = R.string.outlined_text_field_quantity,
            intResSup = R.string.support_text_count_animals,
            isWarehouseShow = false,
            isDropMenuShow = false,
            focusManager = focusManager
        )
        OutlinedPriceInput(
            price = price,
            onPriceChange = { price = it },
            count = count,
            isAutoCalculate = isAutoCalculate,
            onAutoCalculate = { isAutoCalculate = it },
            isManyCount = isAnimalGroup,
            focusManager = focusManager
        )
        OutlinedTextDate(
            value = dateBorn,
            intRes = R.string.outlined_text_date_born,
            intResSup = if (!isAnimalGroup) R.string.outlined_text_date_born else R.string.support_text_date_born_s,
            onValueChange = { dateBorn = it}
        )
        CardField(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .padding(bottom = factoryPadding.coerceAtLeast(0.dp)),
            row = false
        ) {
            CheckboxTextIcon(
                modifier = if (!isDateFactory) Modifier.toOutlinedText() else Modifier,
                checked = isDateFactory,
                onCheckedChange = {
                    isDateFactory = it
                },
                intTitle = if (!isAnimalGroup) R.string.checkbox_born else R.string.checkbox_born_s,
                isTooltipShow = true,
                intTooltip = R.string.tooltip_animals_born
            )
            if (!isDateFactory) {
                OutlinedTextDate(
                    value = dateFactory,
                    intRes = R.string.outlined_text_date_factory,
                    intResSup = if (!isAnimalGroup) R.string.support_text_date_factory else R.string.support_text_date_factory_s,
                    drawableRes = R.drawable.baseline_event_24,
                    onValueChange = { dateFactory = it }
                )
            }
        }
        OutlinedTextCount(
            value = foodDay,
            onValueChange = {
                foodDay = it.toConvertDb()
            },
            isError = false,
            onSuffixChange = { suffixFoodDay = it },
            intRes = R.string.outlined_food_day_animals,
            intResSup = if (!isAnimalGroup) R.string.support_text_food_day_animal else R.string.support_text_food_day_animals,
            suffix = suffixFoodDay,
            isWarehouseShow = false,
            isDropMenuShow = true,
            versionDropMenu = 0,
            focusManager = focusManager
        )
        OutlinedTextNote(
            value = note,
            onValueChange = { note = it },
            focusManager = focusManager
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
                                    priceAll = finalPrice.toDouble(),
                                    suffix = suffixCount,
                                    category = category,
                                    note = "",
                                    showFood = false,
                                    showWarehouse = false,
                                    showAnimals = false,
                                    dailyExpensesFoodAndCount = false,
                                    dailyExpensesFood = 0.0,
                                    countAnimal = 0,
                                    foodDesignedDay = 0,
                                    lastDayFood = "",
                                    idPT = idPT
                                ) else null
                        )
                    )
                    metricalAnimal(title, type)
                }
            }
        )
    }
}
