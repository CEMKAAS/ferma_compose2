package com.zaroslikov.fermacompose2.ui.project.sections

import android.util.Log
import com.zaroslikov.domain.models.dto.BaseProductSection
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.supportFun.convertSize
import com.zaroslikov.fermacompose2.supportFun.convertVolume
import com.zaroslikov.fermacompose2.supportFun.convertWeight

data class BrieflyItem(
    val title: String,
    val weight: ValueItem?,
    val linear: ValueItem?,
    val volume: ValueItem?,
    val pieces: ValueItem?,
    val price: Pair<Double, Suffix>?,
    val rowCount: Int
)

data class ValueItem(
    val value: Pair<Double, Suffix>,
    val price: Pair<Double, Suffix>? = null
)

fun mapperToBrieflyItem(
    title: String,
    items: List<BaseProductSection>,
    settings: DomainSettings
): BrieflyItem {
    var weight = 0.0
    var priceWeight = 0.0
    var volume = 0.0
    var priceVolume = 0.0
    var linear = 0.0
    var priceLinear = 0.0
    var pieces = 0.0
    var pricePieces = 0.0
    var price = 0.0

    val priceSuffix = settings.currencySuffix

    items.forEach { item ->
        val suffix = item.countSuffix
        price += item.priceAll ?: item.price ?: 0.0
        Log.i("add_table", "suffix: $suffix title:$title")
        when (suffix) {
            Suffix.GRAM, Suffix.KILOGRAM, Suffix.TONS -> {
                weight += item.count.convertWeight(
                    item.countSuffix,
                    settings.weightSuffix
                )
                priceWeight += item.priceAll ?: item.price ?: 0.0
            }

            Suffix.MILLILITRES, Suffix.LITERS, Suffix.CUBIC_METERS -> {
                volume += item.count.convertVolume(
                    item.countSuffix,
                    settings.volumeSuffix,
                )
                priceVolume += item.priceAll ?: item.price ?: 0.0
            }

            Suffix.CENTIMETERS, Suffix.MILLIMETERS, Suffix.METERS -> {
                linear += item.count.convertSize(
                    item.countSuffix,
                    settings.linearSuffix
                )
                priceLinear += item.priceAll ?: item.price ?: 0.0
            }

            else -> {
                pieces += item.count
                pricePieces += item.priceAll ?: item.price ?: 0.0
            }
        }
    }
    return BrieflyItem(
        title = title,
        price = if (price > 0) price to settings.currencySuffix else null,
        weight = if (weight > 0)
            ValueItem(
                weight to settings.weightSuffix,
                if (priceWeight > 0)
                    priceWeight to priceSuffix else null
            ) else null,
        volume = if (volume > 0)
            ValueItem(
                volume to settings.volumeSuffix,
                if (priceVolume > 0)
                    priceVolume to priceSuffix else null
            ) else null,
        linear = if (linear > 0)
            ValueItem(
                linear to settings.linearSuffix,
                if (priceLinear > 0)
                    priceLinear to priceSuffix else null
            ) else null,
        pieces = if (pieces > 0)
            ValueItem(
                pieces to Suffix.PIECES,
                if (pricePieces > 0)
                    pricePieces to priceSuffix else null
            ) else null,
        rowCount = items.size,
    )
}