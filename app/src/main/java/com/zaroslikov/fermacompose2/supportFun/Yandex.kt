package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.fermacompose2.Domain.models.DomainAddTable
import com.zaroslikov.fermacompose2.Domain.models.DomainExpensesTable
import com.zaroslikov.fermacompose2.Domain.models.DomainSaleTable
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
    domainSaleTable: DomainSaleTable
) {
    val eventParameters: MutableMap<String, Any> = HashMap()
    eventParameters["Имя"] = domainSaleTable.title
    eventParameters["Кол-во"] = "${domainSaleTable.title} ${domainSaleTable.count} ${domainSaleTable.priceAll}"
    eventParameters["Категория"] = domainSaleTable.category
    eventParameters["Покупатель"] = domainSaleTable.buyer
    eventParameters["Примечание"] = domainSaleTable.note
    AppMetrica.reportEvent("Sale Products", eventParameters)
}

fun metricalExpenses(
   domainExpensesTable: DomainExpensesTable
) {
    val eventParameters: MutableMap<String, Any> = HashMap()
    eventParameters["Имя"] = domainExpensesTable.title
    eventParameters["Кол-во"] = "${domainExpensesTable.title} ${domainExpensesTable.count} ${domainExpensesTable.suffix} ${domainExpensesTable.priceAll} ₽"
    eventParameters["Категория"] = domainExpensesTable.category
    eventParameters["Примечание"] = domainExpensesTable.note
    eventParameters["Корм"] = domainExpensesTable.showFood
    eventParameters["Склад"] = domainExpensesTable.showWarehouse
    eventParameters["Распределение"] = domainExpensesTable.showAnimals
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