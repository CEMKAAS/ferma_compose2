package com.zaroslikov.fermacompose2.ui.navigation

import androidx.annotation.StringRes
import com.zaroslikov.fermacompose2.ui.finance.FinanceAnalysisDestination
import com.zaroslikov.fermacompose2.ui.home.AddEntryDestination

fun navNull(route: String, itemOneRoute: String, itemOne: String) =
    "$route?$itemOneRoute=$itemOne"

fun navNull(
    route: String,
    itemOneRoute: String,
    itemOne: String,
    itemTwoRoute: String,
    itemTwo: String
) =
    "$route?$itemOneRoute=$itemOne&$itemTwoRoute=$itemTwo"

fun nav(route: String, itemOne: String) = "$route/$itemOne"
fun nav(route: String, itemOne: String, itemTwo: String) = "$route/$itemOne/$itemTwo"

sealed class UiEvent {
    data object NavigateBack : UiEvent()
    data class ShowSnackbar(@StringRes val message: String) : UiEvent()
}