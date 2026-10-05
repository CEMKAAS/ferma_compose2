package com.zaroslikov.fermacompose2.ui.elements.icon

import androidx.annotation.DrawableRes
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.fermacompose2.R

/**
 * Единственное место, где стабильная иконка [AppIcon] превращается в ресурс.
 *
 * Идентификатор ресурса живёт только внутри сборки: он раздаётся заново при каждой сборке
 * и сдвигается, когда меняется набор ресурсов приложения или библиотек. Наружу — в БД,
 * бэкапы, QR — уходит только `AppIcon`.
 */
@get:DrawableRes
val AppIcon.drawableRes: Int
    get() = when (this) {
        AppIcon.LIVESTOCK -> R.drawable.livestock
        AppIcon.ICONS_CHICKEN_S -> R.drawable.icons_chicken_s
        AppIcon.ICONS_GOAT -> R.drawable.icons_goat
        AppIcon.ICONS_COW -> R.drawable.icons_cow
        AppIcon.ICONS_PIG -> R.drawable.icons_pig
        AppIcon.ICONS_SHEEP -> R.drawable.icons_sheep
        AppIcon.ICONS_HORSE -> R.drawable.icons_hourse
        AppIcon.ICONS_RABBIT -> R.drawable.icons_rabbit
        AppIcon.ICONS_FARMING_PETS -> R.drawable.icons_farming_pets
        AppIcon.ICONS_PETS -> R.drawable.icons_pets
        AppIcon.ICONS_PLANT -> R.drawable.icons_plant
        AppIcon.ICONS_FARMING_1 -> R.drawable.icons_farming_1
        AppIcon.ICONS_FARMING_2 -> R.drawable.icons_farming_2
        AppIcon.PETS -> R.drawable.baseline_pets_24
        AppIcon.CHICKEN -> R.drawable.chicken
        AppIcon.CHICK -> R.drawable.chiken
        AppIcon.DUCK -> R.drawable.duck
        AppIcon.GOOSE -> R.drawable.external_goose_birds_icongeek26_outline_icongeek26
        AppIcon.QUAIL -> R.drawable.quail
        AppIcon.TURKEY -> R.drawable.turkeycock
        AppIcon.CRUELTY_FREE -> R.drawable.baseline_cruelty_free_24
        AppIcon.COW -> R.drawable.cow
        AppIcon.HORSE -> R.drawable.hourse
        AppIcon.GOAT -> R.drawable.goat
        AppIcon.SHEEP -> R.drawable.sheep
        AppIcon.PIG -> R.drawable.pig
        AppIcon.EGG -> R.drawable.outline_egg_24
    }

/** Иконки хозяйства — порядок задаёт вид сетки выбора. */
val PROJECT_ICONS: List<AppIcon> = listOf(
    AppIcon.LIVESTOCK,
    AppIcon.ICONS_CHICKEN_S,
    AppIcon.ICONS_GOAT,
    AppIcon.ICONS_COW,
    AppIcon.ICONS_PIG,
    AppIcon.ICONS_SHEEP,
    AppIcon.ICONS_HORSE,
    AppIcon.ICONS_RABBIT,
    AppIcon.ICONS_FARMING_PETS,
    AppIcon.ICONS_PETS,
    AppIcon.ICONS_PLANT,
    AppIcon.ICONS_FARMING_1,
    AppIcon.ICONS_FARMING_2,
)

/** Иконки животных. */
val ANIMAL_ICONS: List<AppIcon> = listOf(
    AppIcon.PETS,
    AppIcon.CHICKEN,
    AppIcon.DUCK,
    AppIcon.GOOSE,
    AppIcon.QUAIL,
    AppIcon.TURKEY,
    AppIcon.CRUELTY_FREE,
    AppIcon.COW,
    AppIcon.HORSE,
    AppIcon.GOAT,
    AppIcon.ICONS_RABBIT,
    AppIcon.SHEEP,
    AppIcon.PIG,
)

/** Иконки инкубатора. */
val INCUBATOR_ICONS: List<AppIcon> = listOf(
    AppIcon.CHICKEN,
    AppIcon.DUCK,
    AppIcon.GOOSE,
    AppIcon.QUAIL,
    AppIcon.TURKEY,
    AppIcon.EGG,
)