@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAnimal
import com.zaroslikov.fermacompose2.supportFun.isErrorSlash
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsDown
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.supportFun.metricalAnimal
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.CheckboxTextIcon
import com.zaroslikov.fermacompose2.ui.composeElement.ErrorSupportTextSlash
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextPrice
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextSex
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd
import com.zaroslikov.fermacompose2.ui.composeElement.RadioButtonRow
import com.zaroslikov.fermacompose2.ui.composeElement.RadioButtonWriteOff
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_16
import com.zaroslikov.fermacompose2.ui.composeElement.toOutlinedText
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

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
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarBack(intRes = R.string.animals_add_screen_title, navigateUp = navigateBack)
        }
    ) { innerPadding ->
        AnimalEntryContainer(
            modifier = Modifier
                .modifierScreen(innerPadding)
                .verticalScroll(rememberScrollState()),
            idPT = viewModel.itemId,
            typeList = typeList.list,
            saveInRoomSale = {
                coroutineScope.launch {
                    viewModel.saveItem(
                        animalTable = it.animalTable,
                        animalCountTable = it.animalCountTable,
                        animalWeightTable = it.animalWeightTable,
                        animalSizeTable = it.animalSizeTable
                    )
                    onNavigateUp()
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalEntryContainer(
    idPT: Int,
    modifier: Modifier,
    typeList: List<String>,
    saveInRoomSale: (AnimalEntryRoom) -> Unit
) {
    val sexList = arrayListOf("Мужской", "Женский")

    var title by remember { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("") }
    var sex by rememberSaveable { mutableStateOf(sexList[0]) }
    var isGroupAnimal by remember { mutableStateOf(false)} // true group
    var count by remember { mutableStateOf("1") }
    var note by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var dateBorn by remember { mutableStateOf(dateToday()) }
    var dateFactory by remember { mutableStateOf(dateBorn) }
    var foodDay by remember { mutableStateOf("") }
    var isDateFactory by remember { mutableStateOf(true) }
    var selectedItemIndex by remember { mutableIntStateOf(0) }

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorType by rememberSaveable { mutableStateOf(false) }


    val focusManager = LocalFocusManager.current

    val factoryPadding by animateDpAsState(
        if (!isDateFactory) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    //Дата
    var openDialog by remember { mutableStateOf(false) }
    val state = rememberDatePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    if (openDialog) {
        DatePickerDialogSample(state, dateBorn) {
            dateBorn = it
            openDialog = false
        }
    }

    var openDialogFactory by remember { mutableStateOf(false) }
    val stateFactory = rememberDatePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    if (openDialog) {
        DatePickerDialogSample(stateFactory, dateFactory) {
            dateFactory = it
            openDialogFactory = false
        }
    }

    Column(modifier = modifier) {
        CardField(
            modifier = Modifier
                .padding(bottom = 4.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.animal_entry_screen_info_animal),
                    style = textBold_16
                )
                RadioButtonRow(
                    state = isGroupAnimal,
                    onStateSelect = { isGroupAnimal = it },
                    intResOne = R.string.ration_button_one,
                    intResTwo = R.string.ration_button_many,
                    imageOne = R.drawable.baseline_fiber_manual_record_24,
                    imageTwo = R.drawable.baseline_spoke_24
                )
            }
        }
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                isErrorTitle = it.isError()
            },
            label = { Text(text = stringResource(if (!isGroupAnimal) R.string.outlined_text_name_animal else R.string.outlined_text_name_animals)) },
            supportingText = {
                ErrorSupportTextSlash(
                    isError = isErrorTitle,
                    intRes = if (!isGroupAnimal) R.string.support_text_name_animal else R.string.support_text_names_animals,
                    intResError = if (!isGroupAnimal) R.string.error_no_name_animal else R.string.error_no_name_animals,
                )
            },
            modifier = Modifier.toOutlinedText(),
            isError = isErrorTitle,
            keyboardOptions = keyboardOptionsNext(),
            keyboardActions = keyboardActionsDown(focusManager)
        )
        OutlinedTextTitleAdd(
            intRes = R.string.outlined_text_type,
            intResSup = if (!isGroupAnimal) R.string.support_text_type_animal else R.string.support_text_type_animals,
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
        if (!isGroupAnimal)
            OutlinedTextSex(
                value = sex,
                selectedItemIndex = selectedItemIndex,
                onValueChange = {
                    selectedItemIndex = it
                    sex = sexList[selectedItemIndex]
                },
                list = sexList,
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
        OutlinedTextPrice(
            value = price,
            onValueChange = {
                price = it.toConvertDb()
            },
            intSupportText = if (!isGroupAnimal) R.string.support_text_price_animal else R.string.support_text_price_animals,
            focusManager = focusManager
        )
        OutlinedTextDate(
            value = dateBorn,
            intRes = R.string.outlined_text_date_born,
            intResSup = if (!isGroupAnimal) R.string.outlined_text_date_born else R.string.support_text_date_born_s,
            onValueChange = { openDialog = !openDialog }
        )
        CardField(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .padding(bottom = factoryPadding.coerceAtLeast(0.dp))
        ) {
            Column {
                CheckboxTextIcon(
                    modifier = if (!isDateFactory) Modifier.toOutlinedText() else Modifier,
                    checked = isDateFactory,
                    onCheckedChange = {
                        isDateFactory = it
                    },
                    intTitle = if (!isGroupAnimal) R.string.checkbox_born else R.string.checkbox_born_s,
                    isTooltipShow = true,
                    intTooltip = R.string.tooltip_animals_born
                )
                if (!isDateFactory) {
                    OutlinedTextDate(
                        value = dateFactory,
                        intRes = R.string.outlined_text_date_factory,
                        intResSup = if (!isGroupAnimal) R.string.support_text_date_factory else R.string.support_text_date_factory_s,
                        onValueChange = { openDialogFactory = !openDialogFactory }
                    )
                }
            }
        }
        OutlinedTextCount(
            value = foodDay,
            onValueChange = {
                foodDay = it.toConvertDb()
            },
            isError = false,
            intRes = R.string.outlined_food_day_animals,
            intResSup = if (!isGroupAnimal) R.string.support_text_food_day_animal else R.string.support_text_food_day_animals,
            suffix = stringResource(R.string.suffix_pieces),
            isWarehouseShow = false,
            isDropMenuShow = true,
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
                        state = isGroupAnimal,
                        isErrorTitle = { isErrorTitle = it },
                        isErrorCount = { isErrorCount = it },
                        isErrorType = { isErrorType = it }
                    )
                ) {
                    saveInRoomSale(
                        AnimalEntryRoom(
                            AnimalTable(
                                id = 0,
                                name = title,
                                type = type,
                                data = dateBorn,
                                dateFactory = dateFactory,
                                groop = isGroupAnimal,
                                sex = sex,
                                note = note,
                                image = "0",
                                arhiv = false,
                                price = if (price == "") 0.0 else price.toConvertDb()
                                    .toDouble(),
                                foodDay = if (foodDay == "") 0.0 else foodDay.toConvertDb()
                                    .toDouble(),
                                idPT = idPT
                            ),
                            AnimalCountTable(
                                id = 0,
                                count = if (count == "") "0" else count,
                                suffix = "",//TODO
                                date = dateBorn,
                                idAnimal = idPT
                            ),
                            AnimalWeightTable(
                                id = 0,
                                weight = "0",
                                suffix = "",//TODO
                                date = dateBorn,
                                idAnimal = idPT
                            ),
                            AnimalSizeTable(
                                id = 0,
                                size = "0",
                                suffix = "",//TODO
                                date = dateBorn,
                                idAnimal = idPT
                            )
                        )
                    )
                    metricalAnimal(title, type)
                }
            }
        )
    }
}

data class AnimalEntryRoom(
    val animalTable: AnimalTable,
    val animalCountTable: AnimalCountTable,
    val animalWeightTable: AnimalWeightTable,
    val animalSizeTable: AnimalSizeTable
)