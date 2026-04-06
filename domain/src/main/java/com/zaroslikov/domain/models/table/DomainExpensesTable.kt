package com.zaroslikov.domain.models

import com.zaroslikov.domain.models.dto.BaseProductSection
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.serialization.Serializable

@Serializable
data class DomainExpensesTable(
    val id: Long = 0,
    override val title: String, // название
    override val count: Double, // Кол-во
    override val countSuffix: Suffix,
    val day: Int,  // день
    val month: Int, // месяц
    val year: Int, // время
    override val price: Double,
    override val priceAll: Double? = null,
    val category: String,
    val note: String,
    val isFood: Boolean,
    val isShowFood: Boolean, // Показывать на складе еду
    val feedFood: Double? = null, // Ежедневный расход еды
    val feedFoodSuffix: Suffix? = null, // Суффикс ежедневного расхода
    val countAnimal: Int? = null, // Кол-во животных
    val foodDesignedDay: Int? = null, // Кол-во дней
    val lastDayFood: String? = null, //Последний день еды

    val weight: Double? = null,
    val weightSuffix: Suffix? = null,
    val idPT: Long,
    val animalId: Long? = null,
    val animalVaccinationId: Long? = null,
    val animalCountId: Long? = null,
) : BaseProductSection

