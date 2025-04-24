package com.zaroslikov.fermacompose2.supportFun


fun enableCheckBoxExpenses(
    value: String,
    valueTwo: String? = null,
    boolean: Boolean
): Boolean {

    return if (valueTwo != null)
        when {
            (value == "" || valueTwo == "") -> false
            boolean -> false
            else -> true
        }
    else
        when {
            value == "" -> false
            boolean -> false
            else -> true
        }
}