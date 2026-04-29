@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.warehouse

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.zaroslikov.fermacompose2.orang_20
import com.zaroslikov.fermacompose2.orang_21
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.supportFun.formatDateToLong
import com.zaroslikov.fermacompose2.supportFun.toFullResId
import com.zaroslikov.fermacompose2.supportFun.toResId
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
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.ui.elements.сompositions.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.elements.сompositions.NotificationFun
import com.zaroslikov.fermacompose2.ui.elements.сompositions.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.NotificationParameters
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseEditScreen.WarehouseEditIntent
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseEditScreen.WarehouseEditState
import com.zaroslikov.fermacompose2.ui.start.settings.SD
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.violet_4
import com.zaroslikov.fermacompose2.violet_5
import com.zaroslikov.fermacompose2.violet_6
import com.zaroslikov.fermacompose2.violet_7
import com.zaroslikov.fermacompose2.white
import java.io.ByteArrayOutputStream
import java.io.File


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

    val eventFlow = viewModel.navigation
    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
            }
        }
    }

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
        )
    }
}

@Composable
private fun WarehouseEditBody(
    modifier: Modifier,
    state: WarehouseEditState,
    onIntent: (WarehouseEditIntent) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        MainSettingsCard(
            nameProject = state.currentProject.title,
            onValueChange = {
                onIntent(WarehouseEditIntent.NameProjectChanged(it))
            },
            currentIcon = state.currentIcon,
            imagePath = state.imagePath,
            iconList = state.iconList,
            iconBoxColor = price_green_2,
            onImageSelected = { onIntent(WarehouseEditIntent.ImagePathClicked(it)) },
            onIconSelected = { onIntent(WarehouseEditIntent.IconClicked(it)) },
        )
        DateSettingsCard(state.currentProject.date) {
            onIntent(WarehouseEditIntent.DateClicked(it))
        }
        NotificationCard(
            isShowNotification = state.isShowNotification,
            currentNotification = state.currentNotification,
            notificationList = state.notificationList,
            onIntent = onIntent
        )
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
            enabledButton = state.hasAnyError,
            onIntent = onIntent
        )
    }
}


@Composable
fun MainSettingsCard(
    iconList: List<Int>,
    nameProject: String? = null,
    imagePath: String?,
    currentIcon: Int,
    iconBoxColor: Color,
    onValueChange: (String) -> Unit = {},
    onImageSelected: (String?) -> Unit,
    onIconSelected: (Int) -> Unit,
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val bytes = uriToByteArray(context, uri)
        val path = bytes?.let {
            saveImageToInternalStorage(context, it)
        }
        onImageSelected(path)
        onIconSelected(0)
    }
    var expanded by remember { mutableStateOf(false) }

    CardFieldNew {
        Column {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconTransaction2(
                        modifier = Modifier.clickable { expanded = !expanded },
                        imagePath = imagePath,
                        currentIcon = currentIcon,
                        color = iconBoxColor,
                        sizeCard = 128.dp
                    )
                    Text(
                        stringResource(R.string.add_incubator_screen_download_image),
                        style = text_14,
                        color = gray_7
                    )
                }
                if (nameProject != null)
                    OutlinedTextNew(
                        value = nameProject,
                        onValueChange = onValueChange,
                        labelIntRes = R.string.warehouse_edit_screen_name_project,
                        supportingText = R.string.warehouse_edit_screen_name_project_support,
                        isBorderCard = false
                    )
            }
            AnimatedVisibility(
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
                        iconList.forEachIndexed { index, icon ->
                            ImageCard(painterResource(icon), isSelected = icon == currentIcon) {
                                if (index == iconList.lastIndex) launcher.launch("image/*")
                                else {
                                    onIconSelected(icon)
                                    onImageSelected(null)
                                }
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
                iconColor = green_10,
                boxColor = green_g_4,
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
private fun NotificationCard(
    isShowNotification: Boolean,
    currentNotification: NotificationParameters,
    notificationList: List<NotificationParameters>,
    onIntent: (WarehouseEditIntent) -> Unit
) {
    CardFieldNew(padding = PaddingValues(horizontal = 20.dp, vertical = 15.dp)) {
        Column {
            SD(
                titleRes = R.string.warehouse_edit_screen_notification,
                icon = R.drawable.baseline_notifications_none_24,
                color = orang_20,
                iconColor = orang_21,
            ) {
                Switch(
                    checked = isShowNotification,
                    onCheckedChange = { onIntent(WarehouseEditIntent.ShowNotificationClicked(it)) }
                )
            }
            AnimatedVisibility(
                visible = isShowNotification
            ) {
                Column {
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    NotificationFun(
                        notificationList = notificationList.filter { it.isVisibility },
                        time = currentNotification.time,
                        note = currentNotification.note,
                        isEntry = currentNotification.isEntry,
                        maxCount = 2,
                        onChoiceClick = { onIntent(WarehouseEditIntent.ChoiceNotificationClicked(it)) },
                        onRemoveClick = { onIntent(WarehouseEditIntent.RemoveNotificationClicked(it)) },
                        onCancelClick = { onIntent(WarehouseEditIntent.CancelNotificationClicked) },
                        onTimeChange = { onIntent(WarehouseEditIntent.TimeNotificationChanged(it)) },
                        onNoteChange = { onIntent(WarehouseEditIntent.NoteNotificationChanged(it)) },
                        onAddClick = { onIntent(WarehouseEditIntent.AddNotificationClicked) },
                        onEditClick = { onIntent(WarehouseEditIntent.EditNotificationClicked) }
                    )
                }
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
        Column {
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
                            icon = R.drawable.icon_money,
                            iconColor = baseColor,
                            boxColor = secondColor,
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
            }
            AnimatedVisibility(
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
                    iconColor = violet_6,
                    boxColor = violet_5,
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
    Column(
        modifier = Modifier
            .drawBehind {
                val strokeWidth = 4.dp.toPx()
                drawRect(
                    color = driverColor,
                    topLeft = Offset.Zero,
                    size = Size(strokeWidth, size.height)
                )
            }
            .padding(start = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = stringResource(titleRes),
                    style = text_16,
                    color = black_2
                )
                Text(
                    text = stringResource(currentSuffix.toFullResId()) +
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
            visible = expanded
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(Modifier.padding(2.dp))
                listSuffix.forEach { suffix ->
                    CurrencyCard(
                        isDone = currentSuffix == suffix,
                        suffix = suffix,
                        backgroundColor = backgroundColor,
                        borderColor = borderColor,
                        isCurrency = false
                    ) {
                        onClick(suffix)
                        expanded = false
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
) {
    GradientButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(if (isInsertProject) R.string.button_text_add else R.string.button_text_edit_title),
        onClick = {
            if (isInsertProject) onIntent(WarehouseEditIntent.InsertClicked)
            else onIntent(WarehouseEditIntent.EditClicked)
        },
        colors = listOf(Color(0xFF00A63E), Color(0xFF009966)),
        enabled = enabledButton,
        paddingValues = PaddingValues(vertical = 14.dp)
    )
}

fun uriToByteArray(context: Context, uri: Uri?): ByteArray? {
    if (uri == null) return null

    // Загружаем Bitmap из URI
    val inputStream = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)

    // Преобразуем Bitmap в ByteArray
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}

fun saveImageToInternalStorage(
    context: Context,
    bytes: ByteArray
): String {
    val file = File(
        context.filesDir,
        "project_${System.currentTimeMillis()}.jpg"
    )

    file.writeBytes(bytes)

    return file.absolutePath
}