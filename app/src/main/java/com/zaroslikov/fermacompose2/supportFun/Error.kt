package com.zaroslikov.fermacompose2.supportFun

import android.util.Log
//import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.KillTitleList


//fun <T : Enum<T>> validateCount(
//    boolean: Boolean,
//    value: String
//): Boolean {
//    return boolean = value == ""
//}


fun String.isError(): Boolean {
    return this == ""
}

fun String.isSlash(): Boolean {
    return this.contains("/")
}

fun isErrorAdd(
    title: String,
    count: String,
    isErrorTitle: (Boolean) -> Unit,
    isErrorCount: (Boolean) -> Unit,
    isErrorSlash: (Boolean) -> Unit = {}
): Boolean {
    isErrorTitle(title.isError())
    isErrorCount(count.isError())
    isErrorSlash(title.isSlash())
    return !(title.contains("/") || title == "" || count == "")
}

fun isErrorSale(
    title: String,
    count: String,
    price: String,
    isErrorTitle: (Boolean) -> Unit,
    isErrorCount: (Boolean) -> Unit,
    isErrorSlash: (Boolean) -> Unit,
    isErrorPrice: (Boolean) -> Unit
): Boolean {
    isErrorTitle(title.isError())
    isErrorCount(count.isError())
    isErrorSlash(title.isSlash())
    isErrorPrice(price.isError())
    return !(title.contains("/") || title == "" || count == "" || price == "")
}

fun isErrorAnimal(
    title: String,
    type: String,
    count: String,
    isGroupAnimal: Boolean,
    isErrorTitle: (Boolean) -> Unit,
    isErrorType: (Boolean) -> Unit,
    isErrorCount: (Boolean) -> Unit,
): Boolean {
    isErrorTitle(title.isError())
    isErrorType(type.isError())
    if (isGroupAnimal) isErrorCount(count.isError())
    return !(title == "" || (isGroupAnimal && count == "") || type == "")
}

fun isErrorVersion(
    version: Int,
    title: String,
    countAll: String,
    isAnimalGroup: Boolean,
    isErrorTitle: (Boolean) -> Unit,
    isErrorCountAnimal: (Boolean) -> Unit
): Boolean {
    println(version)
    println(title.toConvertZeroDouble() == 0.0)
    return when {
        version == 2 && title.toConvertZeroDouble() == 0.0 -> {
            isErrorTitle(title.isError())
            println(version)
            println(title.toConvertZeroDouble() == 0.0)
            false
        }

        version != 3 || !isAnimalGroup -> {
            isErrorTitle(title.isError())
            title.isNotBlank()
        }

        else -> {
            isErrorTitle(title.isError())
            isErrorCountAnimal(countAll.isError())
            !(title.isError() || countAll.isError())
        }
    }
}

fun isErrorAnimalSale(
    title: String,
    count: String,
    countAll: Int,
    isAnimalGroup: Boolean,
    isErrorTitle: (Boolean) -> Unit,
    isErrorCount: (Boolean) -> Unit,
    isErrorCountMore: (Boolean) -> Unit,
    isErrorCountZero: (Boolean) -> Unit
): Boolean {
    isErrorTitle(title.isError())

    if (isAnimalGroup) {
        isErrorCount(count.isError())
        isErrorCountMore(countAll < count.toConvertZero())
        isErrorCountZero(isAnimalCountZero(count))
    } else {
        isErrorCount(false)
        isErrorCountMore(false)
    }
    return if (isAnimalGroup) {
        !(title == "" || count == "" || (countAll < count.toConvertZero()) || isAnimalCountZero(
            count
        ))
    } else {
        !(title == "")
    }
}

fun isErrorAddAnimal(
    count: String,
    isErrorCount: (Boolean) -> Unit
): Boolean {
    isErrorCount(count.isBlank())
    return count != ""
}

fun isErrorWriteOffAnimal(
    count: String,
    countAll: String,
    isErrorCount: (Boolean) -> Unit,
    isErrorCountAll: (Boolean) -> Unit,
    isAnimalGroup: Boolean
): Boolean {
    if (isAnimalGroup) {
        if (count.isBlank()) {
            isErrorCount(true)
            return false
        } else {
            if (count.toInt() > countAll.toInt()) {
                isErrorCountAll(true)
                return false
            } else {
                isErrorCount(false)
                return true
            }
        }
    } else {
        isErrorCount(false)
        return true
    }
}

/*fun isErrorKillAnimal(
    countAnimal: String,
    countAnimalAll: String,
    isAnimalGroup: Boolean,
    textFields: SnapshotStateList<KillTitleList>,
    isErrorCount: (Boolean) -> Unit,
    isErrorCountMore: (Boolean) -> Unit,
    isErrorCountZero: (Boolean) -> Unit,
): Boolean {
    textFields.forEachIndexed { index, it ->
        textFields[index] = it.copy(
            isError = it.title.isError(),
            isErrorSlash = it.title.isSlash(),
            isErrorCount = it.count.isError()
        )
    }

    val hasFieldError = textFields.any { it.isError || it.isErrorCount || it.isErrorSlash }

    if (isAnimalGroup) {
        val countError = countAnimal.isError()
        val countZero = isAnimalCountZero(countAnimal)
        val countMore = isAnimalCountIncrease(countAnimal, countAnimalAll)

        isErrorCount(countError)
        isErrorCountZero(countZero)
        isErrorCountMore(countMore)

        return !(hasFieldError || countError || countZero || countMore)
    } else {
        return !hasFieldError
    }
}*/

fun isAnimalCountIncrease(
    count: String,
    countAll: String
): Boolean {
    Log.i(
        "count23", "isAnimalCountIncrease:" +
                "countAll =  ${countAll.toConvertZeroString()}" +
                " ${count.toConvertZeroString().toConvertDbOnlyInt()} > " +
                "${countAll.toConvertZeroString().toConvertOnlyInt2()}"
    )
    return count.toConvertZeroString().toConvertOnlyInt2() > countAll.toConvertZeroString()
        .toConvertOnlyInt2()
}

fun isAnimalWeightIncrease(
    count: String,
    countAll: String
): Boolean {
    return count.toConvertZeroString().toConvertDbDouble() > countAll.toConvertZeroString()
        .toConvertDbDouble()
}

fun isAnimalCountZero(
    count: String
): Boolean {
    return count.toConvertZeroDbInt() == 0
}

fun isAnimalCountDifference(
    countAll: String,
    count: String
): String {
    return (countAll.toConvertZeroString().toConvertDbOnlyInt() - count.toConvertZeroString()
        .toConvertDbOnlyInt()).toString()
}

fun isAnimalCountDifference2(
    countAll: String,
    oldCount: String,
    newCount: String
): Int {
    return countAll.toConvertZeroString().toConvertOnlyInt2() + oldCount.toConvertZeroString()
        .toConvertOnlyInt2() - newCount.toConvertZeroString()
        .toConvertOnlyInt2()
}

fun isAnimalCountDifference3(
    countAll: String,
    oldCount: String,
    newCount: String
): Int {
    return countAll.toConvertZeroString().toConvertOnlyInt2() + oldCount.toConvertZeroString()
        .toConvertOnlyInt2() - newCount.toConvertZeroString()
        .toConvertOnlyInt2()
}

fun animalCountWeightComposition(
    weight: String,
    countAnimal: String
): String {
    return (weight.toConvertZeroString().toConvertDbDouble() * countAnimal.toConvertZeroString()
        .toConvertDbDouble()).formatNumber()
}