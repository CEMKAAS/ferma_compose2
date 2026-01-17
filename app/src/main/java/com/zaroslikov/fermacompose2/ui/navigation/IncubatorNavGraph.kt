package com.zaroslikov.fermacompose2.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncubatorNavHost(
    navController: NavHostController,
    itemPT: Long,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "${WarehouseDestination.route}/${itemPT}",
        modifier = modifier
    ) {

    }
}