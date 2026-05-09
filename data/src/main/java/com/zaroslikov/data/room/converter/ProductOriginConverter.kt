package com.zaroslikov.data.room.converter

import androidx.room.TypeConverter
import com.zaroslikov.domain.models.enums.ProductOrigin

class ProductOriginConverter {
    @TypeConverter
    fun fromProductOrigin(productOrigin: ProductOrigin): Int = productOrigin.ordinal

    @TypeConverter
    fun toProductOrigin(id: Int): ProductOrigin = ProductOrigin.fromCode(id)
}
