@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.warehouse

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.suffixCurrencyList
import com.zaroslikov.domain.models.list.suffixHeightList
import com.zaroslikov.domain.models.list.suffixVolumeList
import com.zaroslikov.domain.models.list.suffixWeightList
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_17
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_4
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_10
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_g_4
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_10
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.supportFun.formatDateToLong
import com.zaroslikov.fermacompose2.supportFun.toFullResId
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.add.getByteArrayFromDrawable
import com.zaroslikov.fermacompose2.ui.add.uriToByteArray
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextMiniCard
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_20
import com.zaroslikov.fermacompose2.ui.dateBuilder
import com.zaroslikov.fermacompose2.ui.monthToResString
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseEditScreen.WarehouseEditState
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.violet_4
import com.zaroslikov.fermacompose2.violet_5
import com.zaroslikov.fermacompose2.violet_6
import com.zaroslikov.fermacompose2.violet_7
import com.zaroslikov.fermacompose2.white


object WarehouseEditDestination : NavigationDestination {
    override val route = "ProjectAdd"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "${route}/{$itemIdArg}"
}

@Composable
fun WarehouseEditScreen(
    navigateBack: () -> Unit,
    viewModel: WarehouseEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val titleRes = if (state.isInsertProject) R.string.warehouse_edit_screen_title_add else
        R.string.warehouse_edit_screen_title_edit

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
        else WarehouseEditBody(
            modifier = Modifier.modifierScreen(innerPadding),
            state = state,
            onIntent = viewModel::onIntent,
            onNavigateBackClick = navigateBack
        )
    }
}

@Composable
private fun WarehouseEditBody(
    modifier: Modifier,
    state: WarehouseEditState,
    onIntent: (WarehouseEditIntent) -> Unit,
    onNavigateBackClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        MainSettingsCard(
            nameProject = state.currentProject.titleProject, onValueChange = {
                onIntent(WarehouseEditIntent.NameProjectChanged(it))
            },
            currentIcon = state.currentIcon,
            imageData = state.imageData,
            onImageSelected = { onIntent(WarehouseEditIntent.IconClicked(it)) },
            onAddImageClicked = { /*onIntent(WarehouseEditIntent.IconClicked(it))*/ }
        )
        DateSettingsCard(state.currentProject.data) { onIntent(WarehouseEditIntent.DateClicked(it)) }
        CurrencySettingsCard(state.currentSettings.currencySuffix) {
            onIntent(WarehouseEditIntent.CurrencyClicked(it))
        }
        WarningCard(
            colorBackground = Color(0xFFF0FDF4),
            colorBorder = Color(0xFFB9F8CF),
            colorIcon = price_green,
            colorIconBackground = Color(0xFFDCFCE7),
            colorTitle = Color(0xFF008236),
            colorText = Color(0xFF008236),
            icon = R.drawable.icon_info,
            title = R.string.warehouse_edit_screen_warning_title,
            text = R.string.warehouse_edit_screen_warning_text,
        )
        UnitsMeasurementSettingsCard(
            currentWeight = state.currentSettings.weightSuffix,
            currentVolume = state.currentSettings.volumeSuffix,
            currentLinear = state.currentSettings.linearSuffix,
            onWeightClick = { onIntent(WarehouseEditIntent.WeightClicked(it)) },
            onVolumeClick = { onIntent(WarehouseEditIntent.VolumeClicked(it)) },
            onLinearClick = { onIntent(WarehouseEditIntent.LinearClicked(it)) },
        )
        SaveButton(
            isInsertProject = state.isInsertProject,
            enabledButton = state.enabledButton(),
            onIntent = onIntent,
            onNavigateBackClick = onNavigateBackClick
        )
    }
}


@Composable
private fun MainSettingsCard(
    nameProject: String,
    currentIcon: Int,
    imageData: ImageBitmap?,
    onValueChange: (String) -> Unit,
    onImageSelected: (Int) -> Unit,
    onAddImageClicked: () -> Unit,
) {
    val imageResources = listOf(
        R.drawable.livestock,
        R.drawable.icons_chicken_s,
        R.drawable.icons_goat,
        R.drawable.icons_cow,
        R.drawable.icons_pig,
        R.drawable.icons_sheep,
        R.drawable.icons_hourse,
        R.drawable.icons_rabbit,
        R.drawable.icons_farming_pets,
        R.drawable.icons_pets,
        R.drawable.icons_plant,
        R.drawable.icons_farming_1,
        R.drawable.icons_farming_2,
        R.drawable.baseline_add_photo_alternate_24
    )

    val context = LocalContext.current
    var byteArray by remember { mutableStateOf(imageData ?: byteArrayOf()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            byteArray = (uriToByteArray(context, imageUri) ?: getByteArrayFromDrawable(
                context,
                imageResources[0]
            ))
        }
    val painter = rememberAsyncImagePainter(model = imageUri)
    val (painter2, isPainter) = when {
        currentIcon == R.drawable.baseline_add_photo_alternate_24 && imageUri != null -> painter to true
        else -> painterResource(currentIcon) to false
    }
    var expanded by remember { mutableStateOf(false) }

    CardFieldNew {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imageData != null) {
                IconTransaction2(
                    modifier = Modifier.clickable(onClick = { expanded = !expanded }),
                    image = imageData,
                    color = price_green_2,
                    sizeCard = 80.dp,
                    isPainter = isPainter
                )
            } else
                IconTransaction2(
                    modifier = Modifier.clickable(onClick = { expanded = !expanded }),
                    image = painter2,
                    color = price_green_2,
                    sizeCard = 80.dp,
                    isPainter = isPainter
                )

            OutlinedTextNew(
                value = nameProject,
                onValueChange = onValueChange,
                labelIntRes = R.string.warehouse_edit_screen_name_project,
                supportingText = R.string.warehouse_edit_screen_name_project_support
            )
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = expanded
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(thickness = 1.dp, color = gray_6)
                    Text(
                        stringResource(R.string.warehouse_edit_screen_choice_icon_project),
                        style = text_14,
                        color = marengo
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        itemVerticalAlignment = Alignment.CenterVertically
                    ) {
                        imageResources.forEachIndexed { index, it ->
                            ImageCard(painterResource(it), isSelected = it == currentIcon) {
                                if (index == imageResources.lastIndex) launcher.launch("image/*")
                                onImageSelected(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateSettingsCard(
    date: String,
    onClick: (String) -> Unit
) {
    var openDialog by remember { mutableStateOf(false) }

    val dateList = date.split(".")
    val dateText = dateBuilder(
        dateList[0].toInt(), stringResource(monthToResString(dateList[1].toInt())),
        dateList[2].toInt()
    )

    if (openDialog) {
        val selectableDates = when {
            /*minDate != null -> MinDateSelectableDates(formatDateToLong(minDate)) */
            else -> PastOrPresentSelectableDates
        }
        val datePickerState = rememberDatePickerState(
            selectableDates = selectableDates,
            initialSelectedDateMillis = formatDateToLong(date)
        )
        DatePickerDialogSample(datePickerState, date) {
            onClick(it)
            openDialog = !openDialog
        }
    }
    CardFieldNew(
        onClick = { openDialog = !openDialog }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTransaction2(
                icon = R.drawable.baseline_calendar_month_24,
                colorIcon = green_10,
                color = green_g_4,
                sizeCard = 36.dp
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    stringResource(R.string.warehouse_edit_screen_project_creation_date),
                    style = text_16,
                    color = black_2
                )
                Text(
                    text = dateText,
                    style = text_14,
                    color = gray_7
                )
            }
        }
    }
}

@Composable
fun CurrencySettingsCard(
    currentCurrency: Suffix,
    baseColor: Color = blue_8,
    secondColor: Color = blue_3,
    onClick: (Suffix) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    CardFieldNew(
        onClick = { expanded = !expanded }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconTransaction2(
                        icon = R.drawable.baseline_currency_ruble_24,
                        colorIcon = baseColor,
                        color = secondColor,
                        sizeCard = 36.dp
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            stringResource(R.string.warehouse_edit_screen_currency),
                            style = text_16,
                            color = black_2
                        )
                        Text(
                            stringResource(currentCurrency.toFullResId()),
                            style = text_14,
                            color = gray_7
                        )
                    }
                }
                TextMiniCard(
                    value = stringResource(currentCurrency.toResId()),
                    textColor = baseColor,
                    backgroundColor = secondColor
                )
            }
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = expanded
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 8.dp),
                        thickness = 1.dp,
                        color = gray_6
                    )
                    suffixCurrencyList.forEach {
                        CurrencyCard(
                            isDone = currentCurrency == it,
                            suffix = it,
                            backgroundColor = blue_3,
                            borderColor = blue_4,
                            isCurrency = true
                        ) {
                            onClick(it)
                            expanded = !expanded
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageCard(
    image: Painter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (borderColor, containerColor) = if (isSelected) green_6 to price_green_2
    else grey_2 to white
    BorderCard(
        padding = PaddingValues(17.dp),
        borderColor = borderColor,
        containerColor = containerColor,
        onClick = onClick
    ) {
        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
private fun CurrencyCard(
    isDone: Boolean,
    suffix: Suffix,
    backgroundColor: Color,
    borderColor: Color,
    isCurrency: Boolean,
    onClick: () -> Unit
) {
    val borderStroke = BorderStroke(1.dp, borderColor)
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) backgroundColor else ghostly_white
        ),
        shape = RoundedCornerShape(14.dp),
        border = if (isDone) borderStroke else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(if ((!isCurrency)) 4.dp else 12.dp)
            ) {
                if (isCurrency)
                    Text(stringResource(suffix.toResId()), style = text_20, color = black)
                Text(stringResource(suffix.toFullResId()), style = text_14, color = black_2)
                if (!isCurrency)
                    Text("(${stringResource(suffix.toResId())})", style = text_14, color = black)
            }
            if (isDone)
                Icon(
                    Icons.Default.Done,
                    contentDescription = null,
                    tint = borderColor
                )
        }
    }
}


@Composable
private fun UnitsMeasurementSettingsCard(
    currentWeight: Suffix,
    currentVolume: Suffix,
    currentLinear: Suffix,
    onWeightClick: (Suffix) -> Unit,
    onVolumeClick: (Suffix) -> Unit,
    onLinearClick: (Suffix) -> Unit
) {
    CardFieldNew {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconTransaction2(
                    icon = R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24,
                    colorIcon = violet_6,
                    color = violet_5,
                    sizeCard = 36.dp
                )
                Text(
                    stringResource(R.string.warehouse_edit_screen_default_units_measurement),
                    style = text_16,
                    color = black_2
                )
            }
            UnitsMeasurement(
                titleRes = R.string.warehouse_edit_screen_weight_units,
                currentSuffix = currentWeight,
                listSuffix = suffixWeightList,
                backgroundColor = orang_4,
                borderColor = orang_9,
                textColor = orang_6,
                driverColor = orang_10,
                onClick = onWeightClick
            )
            UnitsMeasurement(
                titleRes = R.string.warehouse_edit_screen_volume_units,
                currentSuffix = currentVolume,
                listSuffix = suffixVolumeList,
                backgroundColor = blue_3,
                borderColor = blue_4,
                textColor = blue_8,
                driverColor = blue_17,
                onClick = onVolumeClick
            )
            UnitsMeasurement(
                titleRes = R.string.warehouse_edit_screen_linear_dimensions,
                currentSuffix = currentLinear,
                listSuffix = suffixHeightList,
                backgroundColor = violet_3,
                borderColor = violet_4,
                textColor = violet_6,
                driverColor = violet_7,
                onClick = onLinearClick
            )
        }
    }
}

@Composable
private fun UnitsMeasurement(
    @StringRes titleRes: Int,
    currentSuffix: Suffix,
    listSuffix: List<Suffix>,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    driverColor: Color,
    onClick: (Suffix) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 4.dp, color = driverColor)
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { expanded = !expanded }),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        stringResource(titleRes),
                        style = text_16,
                        color = black_2
                    )
                    Text(
                        stringResource(currentSuffix.toFullResId()) +
                                " (${stringResource(currentSuffix.toResId())})",
                        style = text_14,
                        color = gray_7
                    )
                }
                TextMiniCard(
                    value = stringResource(currentSuffix.toResId()),
                    textColor = textColor,
                    backgroundColor = backgroundColor
                )
            }
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = expanded
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listSuffix.forEach {
                        CurrencyCard(
                            isDone = currentSuffix == it,
                            suffix = it,
                            backgroundColor = backgroundColor,
                            borderColor = borderColor,
                            isCurrency = false
                        ) {
                            onClick(it)
                            expanded = !expanded
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SaveButton(
    isInsertProject: Boolean,
    enabledButton: Boolean,
    onIntent: (WarehouseEditIntent) -> Unit,
    onNavigateBackClick: () -> Unit
) {
    GradientButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(if (isInsertProject) R.string.button_text_add else R.string.button_text_edit_title),
        onClick = {
            if (isInsertProject) onIntent(WarehouseEditIntent.InsertClicked)
            else onIntent(WarehouseEditIntent.EditClicked)
            onNavigateBackClick()
        },
        colors = listOf(Color(0xFF00A63E), Color(0xFF009966)),
        enable = enabledButton,
        paddingValues = PaddingValues(vertical = 14.dp)
    )
}