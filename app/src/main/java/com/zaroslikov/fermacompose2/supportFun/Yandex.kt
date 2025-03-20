package com.zaroslikov.fermacompose2.supportFun

import io.appmetrica.analytics.AppMetrica

fun metricaAdd(
    title: String,
    count: String,
    category: String,
    animal: String? = null,
    note: String
) {
    val eventParameters: MutableMap<String, Any> = HashMap()
    eventParameters["Имя"] = title
    eventParameters["Кол-во"] = "$title $count"
    eventParameters["Категория"] = category
    eventParameters["Животное"] = animal.toString()
    eventParameters["Примечание"] = note
    AppMetrica.reportEvent("Add Products", eventParameters);
}

fun metricaSale(
    title: String,
    count: String,
    price: String,
    category: String,
    buyer: String,
    note: String
) {
    val eventParameters: MutableMap<String, Any> = HashMap()
    eventParameters["Имя"] = title
    eventParameters["Кол-во"] = "$title $count $price₽"
    eventParameters["Категория"] = category
    eventParameters["Покупатель"] = buyer
    eventParameters["Примечание"] = note
    AppMetrica.reportEvent("Sale Products", eventParameters)
}

fun metricaExpenses(
    title: String,
    count: String,
    suffix: String,
    price: String,
    category: String,
    showFoodUI: Boolean,
    showWarehouseUI: Boolean,
    showAnimalsUI: Boolean,
    note: String
) {
    val eventParameters: MutableMap<String, Any> = HashMap()
    eventParameters["Имя"] = title
    eventParameters["Кол-во"] = "$title $count $suffix $price ₽"
    eventParameters["Категория"] = category
    eventParameters["Примечание"] = note
    eventParameters["Корм"] = showFoodUI
    eventParameters["Склад"] = showWarehouseUI
    eventParameters["Распределение"] = showAnimalsUI
    AppMetrica.reportEvent("Expenses Products", eventParameters)
}