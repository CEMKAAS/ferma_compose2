package com.zaroslikov.fermacompose2.data.converter

import androidx.room.TypeConverter
import com.zaroslikov.fermacompose2.ui.composeElement.Category

class CategoryConverter {
    @TypeConverter
    fun fromCategory(category: Category): Int = category.id

    @TypeConverter
    fun toCategory(id: Int): Category = Category.fromId(id)
}