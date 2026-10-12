package com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy

import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination

/** Экран беременности самки: `animalPregnancy?itemIdPT={..}&itemId={..}`. */
object AnimalPregnancyDestination : NavigationDestination {
    override val route = "animalPregnancy"
    override val titleRes = R.string.pregnancy_title
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}
