package com.zaroslikov.fermacompose2.ui.start

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.animal.list_screen.AnimalDestination
import com.zaroslikov.fermacompose2.ui.finance.FinanceDestination
import com.zaroslikov.fermacompose2.ui.sections.HomeDestination
import com.zaroslikov.fermacompose2.ui.sections.expenses.list_screen.ExpensesDestination
//import com.zaroslikov.fermacompose2.ui.finance.FinanceDestination
//import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.HomeDestination
import com.zaroslikov.fermacompose2.ui.sections.note.list_screen.NoteDestination
import com.zaroslikov.fermacompose2.ui.sections.sale.list_screen.SaleDestination
//import com.zaroslikov.fermacompose2.ui.warehouse.warehouseScreen.WarehouseDestination
import com.zaroslikov.fermacompose2.ui.sections.writeOff.list_screen.WriteOffDestination
import com.zaroslikov.fermacompose2.ui.start.StartScreen.StartDestination
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun DrawerSheet(
    scope: CoroutineScope,
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    drawerState: DrawerState,
    x: Int,
    idPTNavigation: String
) {
    val context = LocalContext.current
    val drawerItems = listOf(

        DrawerItems(
            R.drawable.baseline_arrow_back_24, "Вернуться к проектам", StartDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_warehouse_24,
            "Мой Склад",
            HomeDestination.route //WarehouseDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_currency_ruble_24, "Мои Финансы", FinanceDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_add_circle_outline_24, "Моя Продукция", HomeDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_add_card_24, "Мои Продажи", SaleDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_add_shopping_cart_24, "Мои Покупки", ExpensesDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_edit_note_24, "Мои Списания", WriteOffDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_cruelty_free_24, "Мои Животные", AnimalDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_edit_document_24, "Мои Заметки", NoteDestination.route
        )

    )

    var selectedItem by remember {
        mutableStateOf(drawerItems[x])
    }

    ModalDrawerSheet {
        drawerItems.forEach {
            NavigationDrawerItem(
                label = {
                    Text(
                        text = it.text,
                        fontSize = if (it.icon == R.drawable.baseline_arrow_back_24) 20.sp
                        else TextUnit.Unspecified
                    )
                },
                selected = it == selectedItem,
                icon = {
                    Image(painter = painterResource(id = it.icon), contentDescription = it.text)
                },
                onClick = {
                    selectedItem = it
                    scope.launch {
                        if (it.route == StartDestination.route) {
                            navigateToStart()
                        } else {
                            navigateToModalSheet(
                                DrawerNavigation(
                                    it.route,
                                    idPTNavigation
                                )
                            )
                            AppMetrica.reportEvent("Переход ${it.text}")
                        }
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }
            )
        }

        Button(
            onClick = {
                AppMetrica.reportEvent("Переход в группу")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/myfermaapp"))
                context.startActivity(intent)
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(text = "Вступить в группу VK!")
        }
    }
}



fun formatter(number: Double): String {
    val numberFormat = NumberFormat.getInstance(Locale("ru", "RU"))
    numberFormat.minimumFractionDigits = 0
    numberFormat.maximumFractionDigits = 2
    return numberFormat.format(number).toString()
}

fun Double.formatNumber(isGroupingUsed: Boolean = true): String {
    val numberFormat = NumberFormat.getInstance(Locale("ru", "RU"))
    numberFormat.minimumFractionDigits = 0
    numberFormat.maximumFractionDigits = 2
    numberFormat.isGroupingUsed = isGroupingUsed
    return numberFormat.format(this).toString()
}

fun Int.formatNumber(isGroupingUsed: Boolean = true): String {
    val numberFormat = NumberFormat.getInstance(Locale("ru", "RU"))
    numberFormat.minimumFractionDigits = 0
    numberFormat.maximumFractionDigits = 2
    numberFormat.isGroupingUsed = isGroupingUsed
    return numberFormat.format(this).toString()
}

fun dateBuilder(day: Int, month: Int, year: Int): String {
    return "%02d.%02d.%04d".format(day, month, year)
}

fun dateBuilder(day: Int, month: String, year: Int): String {
    return "%02d %s %04d".format(day, month, year)
}

fun dateBuilderFinance(date: String): String {
    val dateList = date.split(".")
    val day = dateList[0]
    val mount = dateList[1].toInt()
    val mount2 = monthToResString2(mount)
    return "%02d".format(day) + " $mount2"
}

fun monthToResString(month: Int): Int {
    return when (month) {
        1 -> R.string.month_yan
        2 -> R.string.month_feb
        3 -> R.string.month_mar
        4 -> R.string.month_apr
        5 -> R.string.month_may
        6 -> R.string.month_jun
        7 -> R.string.month_jul
        8 -> R.string.month_aug
        9 -> R.string.month_sep
        10 -> R.string.month_oct
        11 -> R.string.month_nov
        12 -> R.string.month_dec
        else -> R.string.month_yan
    }
}


fun monthToResString2(month: Int): Int {
    return when (month) {
        1 -> R.string.month_yan_
        2 -> R.string.month_feb_
        3 -> R.string.month_mar_
        4 -> R.string.month_apr_
        5 -> R.string.month_may_
        6 -> R.string.month_jun_
        7 -> R.string.month_jul_
        8 -> R.string.month_aug_
        9 -> R.string.month_sep_
        10 -> R.string.month_oct_
        11 -> R.string.month_nov_
        12 -> R.string.month_dec_
        else -> R.string.month_yan_
    }
}

fun monthToResString3(month: Int): Int {
    return when (month) {
        1 -> R.string.month_za_yan
        2 -> R.string.month_za_feb
        3 -> R.string.month_za_mar
        4 -> R.string.month_za_apr
        5 -> R.string.month_za_may
        6 -> R.string.month_za_jun
        7 -> R.string.month_za_jul
        8 -> R.string.month_za_aug
        9 -> R.string.month_za_sep
        10 -> R.string.month_za_oct
        11 -> R.string.month_za_nov
        12 -> R.string.month_za_dec
        else -> R.string.month_za_yan
    }
}

fun formatterTime(hour: Int, minute: Int): String {
    val formattedHour = hour.toString().padStart(2, '0')
    val formattedMinute = minute.toString().padStart(2, '0')

    return "$formattedHour:$formattedMinute"
}

fun dateLong(data: String): Long {
    val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    return if (data == "") {
        calendar.timeInMillis
    } else {
        val dateLong: Date? = format.parse(data)
        dateLong?.time ?: calendar.timeInMillis
    }
}


@Composable
fun AlertDialogInfo(
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    AlertDialog(
        icon = {
            Icon(painterResource(R.drawable.icon_info), contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText, textAlign = TextAlign.Justify)
        },
        onDismissRequest = onConfirmation,
        confirmButton = {
            TextButton(
                onClick = onConfirmation

            ) {
                Text("Отлично!")
            }
        }
    )
}


data class DrawerItems(
    val icon: Int,
    val text: String,
    val route: String
)

data class DrawerNavigation(
    val routeDrawer: String,
    val idProjectDrawer: String
)


