package com.zaroslikov.domain.models.animal.pregnancy

/** Как записать живой приплод: отдельными особями или одной группой-помётом. */
enum class LitterMode { INDIVIDUALS, GROUP }

/** Роды — вход `AnimalPregnancyRepository.registerBirth`. */
data class PregnancyBirth(
    val pregnancyId: Long,
    /** Дата родов `dd.MM.yyyy`. */
    val date: String,
    val female: Int,
    val male: Int,
    val stillborn: Int,
    val mode: LitterMode,
    /** INDIVIDUALS: сначала [female] имён самок, затем [male] имён самцов (size == female + male). */
    val names: List<String>,
    /** GROUP: имя группы-помёта. */
    val groupName: String,
    /** Примечание к стартовой записи счётчика («Приплод от …»). */
    val countNote: String,
    val note: String
) {
    val alive: Int get() = female + male
}
