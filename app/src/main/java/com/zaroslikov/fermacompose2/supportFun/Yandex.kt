package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.fermacompose2.Domain.models.DomainAddTable
import io.appmetrica.analytics.AppMetrica

fun metricAdd(domainAddTable: DomainAddTable) {
    val eventParameters: MutableMap<String, Any> = HashMap()
    eventParameters["Имя"] = domainAddTable.title
    eventParameters["Кол-во"] = "${domainAddTable.count} ${domainAddTable.suffix}"
    eventParameters["Категория"] = domainAddTable.category
    eventParameters["Животное"] = domainAddTable.animal
    eventParameters["Примечание"] = domainAddTable.note
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

fun metricaWriteOff(
    title: String,
    count: String,
    suffix: String,
    price: String,
    note: String,
    state: Boolean
) {
    val eventParameters: MutableMap<String, Any> = HashMap()
    eventParameters["Имя"] = title
    eventParameters["Кол-во"] = "$title $count $suffix $price ₽"
    eventParameters["Статус"] = if (state) 0 else 1
    eventParameters["Примечание"] = note
    AppMetrica.reportEvent("WriteOff Products", eventParameters)
}

fun metricaNote(
    title: String,
) {
    val eventParameters: MutableMap<String, Any> = HashMap()
    eventParameters["Заголовок"] = title
    AppMetrica.reportEvent("Заметки", eventParameters)
}

fun metricalAnimal(
    title: String,
    type:String
) {
    val eventParameters: MutableMap<String, Any> = HashMap()
    eventParameters["Имя"] = title
    eventParameters["Тип"] = type
    AppMetrica.reportEvent("Animal", eventParameters);
}