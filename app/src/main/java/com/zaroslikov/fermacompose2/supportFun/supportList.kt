package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.suffixAllList
import com.zaroslikov.domain.models.list.suffixHeightList
import com.zaroslikov.domain.models.list.suffixPiecesList
import com.zaroslikov.domain.models.list.suffixVolumeList
import com.zaroslikov.domain.models.list.suffixWeightList

fun Suffix.toSuffixList(): List<Suffix> {
    return when (this) {
        Suffix.PIECES, Suffix.HEADS, Suffix.UNITS -> suffixPiecesList
        Suffix.GRAM, Suffix.KILOGRAM, Suffix.TONS -> suffixWeightList
        Suffix.MILLILITRES, Suffix.LITERS, Suffix.CUBIC_METERS -> suffixVolumeList
        Suffix.MILLIMETERS, Suffix.CENTIMETERS, Suffix.METERS -> suffixHeightList
        else -> suffixAllList
    }
}