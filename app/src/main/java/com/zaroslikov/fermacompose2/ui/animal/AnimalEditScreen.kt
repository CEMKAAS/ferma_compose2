package com.zaroslikov.fermacompose2.ui.animal

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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsDown
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonArchive
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.CheckboxTextIcon
import com.zaroslikov.fermacompose2.ui.composeElement.ErrorSupportTextSlash
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextSex
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.composeElement.toOutlinedText
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.dateLong
import kotlinx.coroutines.launch


object AnimalEditDestination : NavigationDestination {
    override val route = "animalEdit"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@ExperimentalMaterial3Api
@Composable
fun AnimalEditProduct(
    navigateBack: () -> Unit,
    navigateEdit: () -> Unit,
    navigateDelete: (String) -> Unit,
    viewModel: AnimalEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    val animalEditUiState = viewModel.animaEditUiState
    val typeEditUiState = viewModel.typeUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarBack(intRes = R.string.animals_add_screen_edit, navigateUp = navigateBack)
        }
    ) { innerPadding ->
        AnimalEditContainer(
            animalEditUiState = animalEditUiState,
            modifier = Modifier
                .modifierScreen(innerPadding)
                .verticalScroll(rememberScrollState()),
            typeList = typeEditUiState.value.list,
            onValueChange = viewModel::updateUiState,
            saveInRoomSale = {
                if (it) {
                    coroutineScope.launch {
                        viewModel.saveItem()
                        navigateEdit()
                    }
                }
            },
            deleteInRoom = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    navigateDelete(it)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalEditContainer(
    modifier: Modifier,
    animalEditUiState: AnimalEditUiState,
    typeList: List<String>,
    onValueChange: (AnimalEditUiState) -> Unit = {},
    saveInRoomSale: (Boolean) -> Unit,
    deleteInRoom: (String) -> Unit
) {
    val sexList = arrayListOf("Мужской", "Женский")

    var expandedSex by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }

    var isGroupAnimal by remember { mutableStateOf(false) } // true group
//    var dateFactory by remember { mutableStateOf(dateBorn) }
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorType by rememberSaveable { mutableStateOf(false) }
    var isErrorPrice by rememberSaveable { mutableStateOf(false) }
    var isErrorFoodDay by rememberSaveable { mutableStateOf(false) }
    var openDialogFactory by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    var selectedItemIndex by remember { mutableStateOf(0) }

    var openDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedDateMillis = dateLong(animalEditUiState.data)
    )

    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }

    fun validateType(text: String) {
        isErrorType = text == ""
    }

    fun validatePrice(text: String) {
        isErrorPrice = text == ""
    }

    fun validateFoodDay(text: String) {
        isErrorFoodDay = text == ""
    }

    var isDateFactory by remember { mutableStateOf(true) } // TODO in room
    fun errorBoolean(): Boolean {
        isErrorTitle = animalEditUiState.name == ""
        isErrorType = animalEditUiState.type == ""
        isErrorPrice = animalEditUiState.price == ""
        isErrorFoodDay = animalEditUiState.foodDay == ""
        return !(isErrorTitle || isErrorType || isErrorPrice || isErrorFoodDay)
    }

    val factoryPadding by animateDpAsState(
        if (!isDateFactory) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            value = animalEditUiState.name,
            onValueChange = {
                onValueChange(animalEditUiState.copy(name = it))
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
            value = animalEditUiState.type,
            onValueChange = {
                onValueChange(animalEditUiState.copy(type = it.trim()))
                isErrorType = it.isError()
            },
            drawableRes = R.drawable.baseline_pets_24,
            titleList = typeList,
            isErrorTitle = isErrorType,
            isErrorSlash = false,
            focusManager = focusManager
        )
//        if (!isGroupAnimal)
//            OutlinedTextSex(
//                value = animalEditUiState.sex,
//                selectedItemIndex = selectedItemIndex,
//                onValueChange = {
//                    selectedItemIndex = it
//                    onValueChange(animalEditUiState.copy(sex = sexList[selectedItemIndex]))
//                },
//                focusManager = focusManager
//            )
//        TODO подумать, как лучше сделать, привязать к продаже
//        OutlinedTextPrice(
//            value = price,
//            onValueChange = {
//                price = it.toConvertDb()
//            },
//            intSupportText = if (!isGroupAnimal) R.string.support_text_price_animal else R.string.support_text_price_animals,
//            focusManager = focusManager
//        )
        OutlinedTextDate(
            value = animalEditUiState.price,
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
//                if (!isDateFactory) {
//                    OutlinedTextDate(
//                        value = dateFactory,
//                        intRes = R.string.outlined_text_date_factory,
//                        intResSup = if (!isGroupAnimal) R.string.support_text_date_factory else R.string.support_text_date_factory_s,
//                        onValueChange = { openDialogFactory = !openDialogFactory }
//                    )
//                }
            }
        }
        OutlinedTextCount(
            value = animalEditUiState.foodDay,
            onValueChange = {
//                foodDay = it.toConvertDb()
            },
            isError = false,
            intRes = R.string.outlined_food_day_animals,
            intResSup = if (!isGroupAnimal) R.string.support_text_food_day_animal else R.string.support_text_food_day_animals,
            suffix = stringResource(R.string.suffix_pieces),
            isWarehouseShow = false,
            isDropMenuShow = true,
            focusManager = focusManager
        )
//        ButtonPanel(
//            onRefreshClick = { saveInRoomSale(errorBoolean()) },
//            onArchiveClick = { onValueChange(animalEditUiState.copy(arhiv = "1")) },
//            onDeleteClick = { deleteInRoom(animalEditUiState.idPT.toString()) }
//        )
    }
}


@Composable
private fun ButtonPanel(
    onRefreshClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column {
        ButtonRefresh { onRefreshClick() }
        ButtonArchive { onArchiveClick() }
        ButtonDelete { onDeleteClick() }
    }
}