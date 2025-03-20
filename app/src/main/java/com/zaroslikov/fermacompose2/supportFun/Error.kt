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
    isErrorSlash: (Boolean) -> Unit
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

