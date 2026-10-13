package com.zaroslikov.fermacompose2.ui.project.finance.traceBoard

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.animal_2
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_4
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_8
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_8

/** Основной цвет категории: нити, рамки хабов, полоски карточек. */
val TraceCategory.color: Color
    get() = when (this) {
        TraceCategory.PRODUCT -> price_green
        TraceCategory.PRODUCTION -> blue_1
        TraceCategory.SALE -> price_green
        TraceCategory.OWN_NEEDS -> violet_1
        TraceCategory.DISPOSAL -> error_base
        TraceCategory.RECYCLING -> green_shamrock
        TraceCategory.RECYCLING_RESULT -> orang_2
        TraceCategory.INGREDIENT -> gray_7
        TraceCategory.STOCK -> black_2
    }

/** Светлый фон под иконкой категории. */
val TraceCategory.tint: Color
    get() = when (this) {
        TraceCategory.PRODUCT -> green_8
        TraceCategory.PRODUCTION -> blue_3
        TraceCategory.SALE -> green_8
        TraceCategory.OWN_NEEDS -> violet_8
        TraceCategory.DISPOSAL -> red_11
        TraceCategory.RECYCLING -> green_4
        TraceCategory.RECYCLING_RESULT -> orang_8
        TraceCategory.INGREDIENT -> gray_6
        TraceCategory.STOCK -> gray_6
    }

@get:DrawableRes
val TraceCategory.iconRes: Int
    get() = when (this) {
        TraceCategory.PRODUCT -> R.drawable.icon_add_product
        TraceCategory.PRODUCTION -> R.drawable.icon_add_product
        TraceCategory.SALE -> R.drawable.icon_sale
        TraceCategory.OWN_NEEDS -> R.drawable.baseline_cottage_24
        TraceCategory.DISPOSAL -> R.drawable.baseline_delete_24
        TraceCategory.RECYCLING -> R.drawable.baseline_autorenew_24
        TraceCategory.RECYCLING_RESULT -> R.drawable.icon_open_box
        TraceCategory.INGREDIENT -> R.drawable.icon_list
        TraceCategory.STOCK -> R.drawable.baseline_warehouse_24
    }

/** Подпись итога в панели сверху («Произведено», «Продано»…); у остальных категорий — как в легенде. */
@get:StringRes
val TraceCategory.summaryTitleRes: Int
    get() = when (this) {
        TraceCategory.PRODUCTION -> R.string.trace_board_summary_produced
        TraceCategory.SALE -> R.string.trace_board_summary_sold
        TraceCategory.OWN_NEEDS -> R.string.trace_board_summary_own_needs
        TraceCategory.DISPOSAL -> R.string.trace_board_summary_disposal
        TraceCategory.RECYCLING -> R.string.trace_board_summary_recycled
        else -> titleRes
    }

/** Подпись категории в легенде. */
@get:StringRes
val TraceCategory.titleRes: Int
    get() = when (this) {
        TraceCategory.PRODUCT -> R.string.trace_board_legend_product
        TraceCategory.PRODUCTION -> R.string.trace_board_legend_production
        TraceCategory.SALE -> R.string.trace_board_legend_sale
        TraceCategory.OWN_NEEDS -> R.string.trace_board_legend_own_needs
        TraceCategory.DISPOSAL -> R.string.trace_board_legend_disposal
        TraceCategory.RECYCLING -> R.string.trace_board_legend_recycling
        TraceCategory.RECYCLING_RESULT -> R.string.trace_board_legend_result
        TraceCategory.INGREDIENT -> R.string.trace_board_legend_ingredient
        TraceCategory.STOCK -> R.string.trace_board_summary_stock
    }

/** Градиент значка животного — та же палитра, что в списке животных и сборных группах. */
val TraceAnimalKind.colors: List<Color>
    get() = when (this) {
        TraceAnimalKind.MALE -> listOf(Color(0xFF51A2FF), Color(0xFF615FFF))
        TraceAnimalKind.FEMALE -> listOf(Color(0xFFFB64B6), Color(0xFFFF2056))
        TraceAnimalKind.GROUP -> listOf(animal_1, animal_2)
        TraceAnimalKind.HERD -> listOf(Color(0xFFFE9A00), Color(0xFFFF6900))
        TraceAnimalKind.UNKNOWN -> listOf(Color(0xFF6A7282), Color(0xFF99A1AF))
    }
