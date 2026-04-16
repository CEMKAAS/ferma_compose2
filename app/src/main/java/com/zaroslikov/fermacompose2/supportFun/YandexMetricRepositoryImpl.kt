package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.dto.add.DomainFastAddProduct
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.fermacompose2.ui.incubator_project.AddIncubator.AddIncubator
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.EntryBookmark
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddEntryState2
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.size.CurrentAnimalSize
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.vaccination.Vaccination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.weight.CurrentAnimalWeight
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalEntryState2
import com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen.ExpensesEntryState2
import com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen.SaleEntryState2
import com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen.WriteOffEntryState2
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseEditScreen.WarehouseEditState
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.FoodListUi
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import io.appmetrica.analytics.AppMetrica
import javax.inject.Inject

class YandexMetricRepositoryImpl @Inject constructor(
    private val resourceProvider: ResourceProvider
) : YandexMetricRepository {

    private fun getString(suffix: Suffix): String {
        return resourceProvider.getString(suffix.toResId())
    }

    override fun metricalProject(state: WarehouseEditState) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Имя"] = state.nameProject
        eventParameters["Валюта"] = getString(state.currentSettings.currencySuffix)
        eventParameters["Весовая единица"] = getString(state.currentSettings.weightSuffix)
        eventParameters["Объемная единица"] = getString(state.currentSettings.volumeSuffix)
        eventParameters["Линейная единица"] = getString(state.currentSettings.linearSuffix)
        eventParameters["Уведомления"] =
            if (state.isShowNotification) "Включено" else "Выключенны"
        AppMetrica.reportEvent("Добавление проекта", eventParameters);
    }

    override fun metricalIncubator(state: AddIncubator) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Имя"] = state.title
        eventParameters["Марка"] = state.brand
        eventParameters["Модель"] = state.model
        eventParameters["Вместимость"] = state.capacity
        eventParameters["Стоимость"] = state.price
        eventParameters["Автопереворот"] = if (state.isAutoRotation) "Включено" else "Выключено"
        eventParameters["Автопроветривание"] =
            if (state.isAutoVentilation) "Включено" else "Выключено"
        eventParameters["Примечание"] = state.note.ifBlank { "Заметка не указана" }
        AppMetrica.reportEvent("Добавление инкубатора", eventParameters);
    }

    override fun metricalBookmark(state: EntryBookmark) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Имя"] = state.title
        eventParameters["Тип яиц"] = state.type
        eventParameters["Порода"] = state.breed
        eventParameters["Колличество"] = state.count
        eventParameters["Стоимость"] = state.price
        eventParameters["Авторасчет"] = if (state.isAutoPrice) "Включено" else "Выключено"
        eventParameters["Время закладки"] = state.time
        eventParameters["Автопереворот"] = if (state.autoRotation) "Включено" else "Выключено"
        eventParameters["Автопроветривание"] =
            if (state.autoVentilation) "Включено" else "Выключено"
        eventParameters["Примечание"] = state.note.ifBlank { "Заметка не указана" }
        AppMetrica.reportEvent("Добавление закладки", eventParameters);
    }

    override fun metricalFastAdd(state: DomainFastAddProduct) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Имя"] = state.title
        eventParameters["Категория"] = state.category ?: "Категория не указана"
        AppMetrica.reportEvent("Быстрое добавление товара", eventParameters)
    }

    override fun metricalWriteOffFood(state: FoodListUi) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Имя"] = state.title
        eventParameters["Дней до конца"] = state.daysEnd
        AppMetrica.reportEvent("Списание корма", eventParameters)
    }

    override fun metricAdd(domainAddTable: AddEntryState2) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Имя"] = domainAddTable.title
        eventParameters["Категория"] = domainAddTable.category
        eventParameters["Животное"] = domainAddTable.animal.ifBlank { "Животное не указано" }
        eventParameters["Примечание"] = domainAddTable.note.ifBlank { "Заметка не указана" }
        AppMetrica.reportEvent("Добавление продукции", eventParameters)
    }

    override fun metricSale(
        domainSaleTable: SaleEntryState2
    ) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Имя"] = domainSaleTable.title
        eventParameters["Категория"] = domainSaleTable.category
        eventParameters["Покупатель"] = domainSaleTable.buyer.ifBlank { "Покупатль не указан" }
        eventParameters["Примечание"] = domainSaleTable.note.ifBlank { "Заметка не указана" }
        AppMetrica.reportEvent("Продажа продукции", eventParameters)
    }

    override fun metricalExpenses(
        domainExpensesTable: ExpensesEntryState2
    ) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Имя"] = domainExpensesTable.title
        eventParameters["Категория"] = domainExpensesTable.category
        eventParameters["Корм"] = if (domainExpensesTable.isShowFood) "Корм" else "Обычная покупка"
        eventParameters["Примечание"] = domainExpensesTable.note.ifBlank { "Заметка не указана" }
        AppMetrica.reportEvent("Покупка продукции", eventParameters)
    }

    override fun metricalWriteOff(currentProduct: WriteOffEntryState2) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Имя"] = currentProduct.title
        eventParameters["Статус"] =
            if (currentProduct.status) "На утилизацию" else "На собсвенные нужды"
        eventParameters["Примечание"] = currentProduct.note.ifBlank { "Заметка не указана" }
        AppMetrica.reportEvent("Списание продукции", eventParameters)
    }

    override fun metricalNote(
        title: String,
    ) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Заголовок"] = title
        AppMetrica.reportEvent("Добавление заметок", eventParameters)
    }

    override fun metricalAnimal(
        state: AnimalEntryState2,
    ) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Имя"] = state.title
        eventParameters["Тип"] = state.type
        eventParameters["Пол"] = if (state.sex) "Мужской" else "Женский"
        eventParameters["Тип добавления"] =
            if (state.isAnimalGroup) "Группа животных" else " Одно животное"
        eventParameters["Дата рождения совпадает"] =
            if (state.isDateFactory) "Не совпадает датой завода" else "Совпадает с датой завода"
        eventParameters["Примечание"] = state.note.ifBlank { "Заметка не указана" }

        AppMetrica.reportEvent("Добавление животного", eventParameters);
    }

    override fun metricalAnimalSize(
        state: CurrentAnimalSize,
    ) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Количество"] = state.size
        eventParameters["Единица измерения"] = getString(state.suffix)
        eventParameters["Примечание"] = state.note.ifBlank { "Заметка не указана" }
        AppMetrica.reportEvent("Добавление размера животного", eventParameters);
    }

    override fun metricalAnimalWeight(
        state: CurrentAnimalWeight,
    ) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Количество"] = state.weight
        eventParameters["Единица измерения"] = getString(state.suffix)
        eventParameters["Примечание"] = state.note.ifBlank { "Заметка не указана" }
        AppMetrica.reportEvent("Добавление веса животного", eventParameters);
    }

    override fun metricalAnimalVaccination(
        state: Vaccination
    ) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Вакцинация"] = state.vaccination
        eventParameters["Примечание"] = state.note.ifBlank { "Заметка не указана" }
        AppMetrica.reportEvent("Добавление вакцинации", eventParameters);
    }

    override fun metricalAnimalCount(
        state: DomainAnimalCount,
    ) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Количество"] = state.count
        eventParameters["Единица измерения"] = getString(state.suffix)
        eventParameters["Вид добавления"] = when (state.version) {
            AnimalCountVersion.SALE -> "Продажа животных"
            AnimalCountVersion.EXPENSES -> "Покупка животных"
            AnimalCountVersion.KILL -> "Забой животных"
            AnimalCountVersion.WRITE_OFF -> "Списание животных"
            AnimalCountVersion.ADD -> "Добавление животных"
            AnimalCountVersion.INCUBATOR -> "Добавление животных через инкубатор"
            null -> "Неизвестная ошибка "
        }
        eventParameters["Примечание"] = state.note.ifBlank { "Заметка не указана" }
        AppMetrica.reportEvent("Добавление размера", eventParameters);
    }

    override fun metricalProfile(
        name: String,
    ) {
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["Имя"] = name
        AppMetrica.reportEvent("Профиль", eventParameters);
    }
}
