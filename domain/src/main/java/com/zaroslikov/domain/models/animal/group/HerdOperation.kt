package com.zaroslikov.domain.models.animal.group

import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainWriteOffTable

/**
 * Корректировка поголовья сборной группы: одна операция ([version] — продажа, списание, забой,
 * приход или покупка) над несколькими членами. Каждому члену — своя запись счётчика с меткой
 * группы и своя запись продажи/списания/расхода (animalCountId проставляет репозиторий).
 */
data class DomainHerdOperation(
    val herdId: Long,
    val version: AnimalCountVersion,
    val date: String,
    val note: String,
    val items: List<DomainHerdOperationItem>
)

data class DomainHerdOperationItem(
    val animalId: Long,
    /** Сколько голов уходит или приходит; у индивидуального животного — 1. */
    val count: Double,
    val sale: DomainSaleTable? = null,
    val writeOff: DomainWriteOffTable? = null,
    val expenses: DomainExpensesTable? = null,
    /** Продукция забоя. */
    val products: List<DomainAddTable> = emptyList(),
    /** Обезличенная группа: сколько из [count] самок и самцов; остальное — пол не известен. */
    val femalesCount: Int = 0,
    val malesCount: Int = 0
)

/** Запись истории поголовья сборной группы: запись счётчика члена с меткой группы. */
data class DomainHerdCountRecord(
    val id: Long,
    val count: String,
    val suffix: Suffix,
    val date: String,
    val note: String,
    val version: AnimalCountVersion?,
    val animalId: Long,
    val animalName: String,
    val isGroup: Boolean,
    val price: Double?,
    val priceAll: Double?,
    val buyer: String?,
    /** Обезличенная группа: сколько из количества самок и самцов. */
    val femalesCount: Int = 0,
    val malesCount: Int = 0
)
