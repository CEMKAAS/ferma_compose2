package com.zaroslikov.fermacompose2.ui.start

import androidx.compose.foundation.Image
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.animal.AnimalDestination
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesDestination
import com.zaroslikov.fermacompose2.ui.finance.FinanceDestination
import com.zaroslikov.fermacompose2.ui.home.HomeDestination
import com.zaroslikov.fermacompose2.ui.sale.SaleDestination
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseDestination
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffDestination
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
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
    val drawerItems = listOf(

        DrawerItems(
            R.drawable.baseline_arrow_back_24, "Вернуться к проектам", StartDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_warehouse_24, "Мой Склад", WarehouseDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_currency_ruble_24, "Мой Финансы", FinanceDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_add_circle_outline_24, "Мои Товары", HomeDestination.route
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
        )

    )

    var selectedItem by remember {
        mutableStateOf(drawerItems[x])
    }

    ModalDrawerSheet() {
        drawerItems.forEach {
            NavigationDrawerItem(
                label = { Text(text = it.text) },
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
    }
}

fun formatter(number: Double): String {
    val numberFormat = NumberFormat.getInstance(Locale("ru", "RU"))
    numberFormat.minimumFractionDigits = 0
    numberFormat.maximumFractionDigits = 2
    return numberFormat.format(number).toString()
}

fun formatterTime(hour: Int, minute: Int): String {
    val formattedHour = hour.toString().padStart(2, '0')
    val formattedMinute = minute.toString().padStart(2, '0')

    return "$formattedHour:$formattedMinute"
}




fun dateLong(data: String): Long {
    val format = SimpleDateFormat("dd.MM.yyyy")
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    return if (data == "") {
        calendar.timeInMillis
    } else {
        val dateLong: Date? = format.parse(data)
        dateLong?.time ?: calendar.timeInMillis
    }
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