package com.zaroslikov.fermacompose2.supportFun


//fun <T : Enum<T>> validateCount(
//    boolean: Boolean,
//    value: String
//): Boolean {
//    return boolean = value == ""
//}


fun String.isError(): Boolean {
    return this == ""
}

fun String.isErrorSlash(): Boolean {
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
    isErrorSlash(title.isErrorSlash())
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
    isErrorSlash(title.isErrorSlash())
    isErrorPrice(price.isError())
    return !(title.contains("/") || title == "" || count == "" || price == "")
}

fun isErrorExpenses(
    title: String,
    count: String,
    price: String,
    dailyExpensesFoodUI: String,
    countAnimalUI: String,
    setDailyExpensesFoodAndCountUI: Boolean,
    isErrorTitle: (Boolean) -> Unit,
    isErrorCount: (Boolean) -> Unit,
    isErrorSlash: (Boolean) -> Unit,
    isErrorPrice: (Boolean) -> Unit,
    isErrorDailyExpensesFood: (Boolean) -> Unit,
    isErrorCountAnimalUI: (Boolean) -> Unit,
): Boolean {
    isErrorTitle(title.isError())
    isErrorCount(count.isError())
    isErrorSlash(title.isErrorSlash())
    isErrorPrice(price.isError())

    if (setDailyExpensesFoodAndCountUI) {
        isErrorDailyExpensesFood(dailyExpensesFoodUI.isError())
        isErrorCountAnimalUI(countAnimalUI.isError())
    }

    return if (setDailyExpensesFoodAndCountUI) {
        !(title.isErrorSlash() || title.isError() || count.isError() || price.isError() || dailyExpensesFoodUI.isError() || countAnimalUI.isError())
    } else {
        !(title.isErrorSlash() || title.isError() || count.isError() || price.isError())
    }
}


fun isErrorAnimal(
    title: String,
    type: String,
    count: String,
    state: Boolean,
    isErrorTitle: (Boolean) -> Unit,
    isErrorType: (Boolean) -> Unit,
    isErrorCount: (Boolean) -> Unit,
): Boolean {
    isErrorTitle(title.isError())
    isErrorType(type.isError())
    if (!state) isErrorCount(count.isError())
    return !(title == "" || (count == "" && !state) || type == "")
}

fun isErrorVersion(
    title: String,
    isErrorTitle: (Boolean) -> Unit,
): Boolean {
    isErrorTitle(title.isError())
    return !(title == "")
}

fun isErrorAnimalSale(
    title: String,
    count: String,
    countAll: Int,
    isAnimalGroup: Boolean,
    isErrorTitle: (Boolean) -> Unit,
    isErrorCount: (Boolean) -> Unit,
    isErrorCountMore: (Boolean) -> Unit
): Boolean {
    isErrorTitle(title.isError())

    if (isAnimalGroup) {
        isErrorCount(count.isError())
        isErrorCountMore(countAll < count.toConvertZero())
    } else {
        isErrorCount(false)
        isErrorCountMore(false)
    }
    return if (isAnimalGroup) {
        !(title == "" || count == "" || (countAll < count.toConvertZero()))
    } else {
        !(title == "")
    }
}