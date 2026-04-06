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

fun navNull(
    route: String,
    itemOneRoute: String = "itemIdPT",
    itemOne: String,
    itemTwoRoute: String = "itemId",
    itemTwo: String,
    itemThreeRoute: String = "itemId",
    itemThree: String
) =
    "$route?$itemOneRoute=$itemOne&$itemTwoRoute=$itemTwo&$itemThreeRoute=$itemThree"

fun nav(route: String, itemOne: String) = "$route/$itemOne"
fun nav(route: String, itemOne: String, itemTwo: String) = "$route/$itemOne/$itemTwo"
fun nav(route: String, itemOne: String, itemTwo: String, itemThree: String) =
    "$route/$itemOne/$itemTwo/$itemThree"

sealed class UiEvent {
    data object NavigateBack : UiEvent()
}


sealed class EventFile {
    data class File(val value: String) : EventFile()
}