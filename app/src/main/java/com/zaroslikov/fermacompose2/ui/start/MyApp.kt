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
import com.zaroslikov.fermacompose2.ui.home.HomeDestination
import com.zaroslikov.fermacompose2.ui.sale.SaleDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerSheet(
    scope: CoroutineScope,
    navigateToStart:()->Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    drawerState: DrawerState,
    x: Int,
    idPTNavigation: String
) {
    val drawerItems = listOf(

        DrawerItems(
            R.drawable.baseline_arrow_back_24, "Вернуться к проектам", StartDestination.route
        ),

//        DrawerItems(
//            R.drawable.baseline_warehouse_24, "Мой Склад", ItemDetailsDestination.route
//        ),
//        DrawerItems(
//            R.drawable.baseline_currency_ruble_24, "Мой Финансы", "Finance"
//        ),
        DrawerItems(
            R.drawable.baseline_add_circle_outline_24, "Мои Товары", HomeDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_add_card_24, "Мои Продажи", SaleDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_add_shopping_cart_24, "Мои Покупки", "Expenses"
        ),
//        DrawerItems(
//            R.drawable.baseline_edit_note_24, "Мои Списания", "WriteOff"
//        ),
//        DrawerItems(
//            R.drawable.baseline_bar_chart_24, "Мои Графики", "Chart"
//        ),
//        DrawerItems(
//            R.drawable.baseline_cruelty_free_24, "Мои Животные", "Animal"
//        )

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

data class DrawerItems(
    val icon: Int,
    val text: String,
    val route: String
)

data class DrawerNavigation(
    val routeDrawer: String,
    val idProjectDrawer: String
)