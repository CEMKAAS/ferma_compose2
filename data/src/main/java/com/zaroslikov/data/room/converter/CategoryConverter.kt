package com.zaroslikov.data.room.converter

import androidx.room.TypeConverter

class CategoryConverter {
    @TypeConverter
    fun fromCategory(category: Category): Int = category.id

    @TypeConverter
    fun toCategory(id: Int): Category = Category.fromId(id)
}