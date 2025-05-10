package com.zaroslikov.fermacompose2.supportFun

fun infoTextKillAnimal(count: String, suffix: String): String {
    return "${count.toConvertZeroString().toFormatNumber()} " +
            suffix
}