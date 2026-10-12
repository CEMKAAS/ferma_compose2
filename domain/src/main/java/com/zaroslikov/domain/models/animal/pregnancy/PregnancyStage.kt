package com.zaroslikov.domain.models.animal.pregnancy

import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason

enum class PregnancyStageKind { CHECK, EXAM, PREPARATION, BIRTH_WINDOW, BIRTH, OVERDUE }

/** Этап беременности. Дни считаются от случки: день случки = 0. [key] стабилен — входит в имя work-а. */
data class PregnancyStage(
    val key: String,
    val dayFrom: Int,
    val dayTo: Int,
    val title: String,
    val description: String,
    val notify: Boolean,
    val kind: PregnancyStageKind
)

/** Названия по виду. */
data class PregnancyTerms(
    /** «Сукрольна». */
    val pregnantAdjective: String,
    /** «Окрол». */
    val birthName: String,
    /** «Крольчонок». */
    val youngSingular: String,
    /** «Крольчата». */
    val youngPlural: String
)

data class SpeciesPregnancyNorms(
    val species: AnimalSpecies,
    val gestationDays: Int,
    val minDays: Int,
    val maxDays: Int,
    val terms: PregnancyTerms,
    /** Отсортированы по [PregnancyStage.dayFrom]. */
    val stages: List<PregnancyStage>,
    val tips: List<String>,
    /** [PregnancyInterruptReason.RESORPTION] только у кролика. */
    val interruptReasons: List<PregnancyInterruptReason>
)
