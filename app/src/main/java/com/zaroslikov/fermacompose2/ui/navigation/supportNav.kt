package com.zaroslikov.fermacompose2.ui.navigation

fun navNull(route: String, itemOneRoute: String = "itemIdPT", itemOne: String) =
    "$route?$itemOneRoute=$itemOne"

fun navNull(
    route: String,
    itemOneRoute: String = "itemIdPT",
    itemOne: String,
    itemTwoRoute: String = "itemId",
    itemTwo: String
) =
    "$route?$itemOneRoute=$itemOne&$itemTwoRoute=$itemTwo"

fun nav(route: String, itemOne: String) = "$route/$itemOne"
fun nav(route: String, itemOne: String, itemTwo: String) = "$route/$itemOne/$itemTwo"

sealed class UiEvent {
    data object NavigateBack : UiEvent()
}