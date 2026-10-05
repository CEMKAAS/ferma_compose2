package com.zaroslikov.data.room.converter

import androidx.room.TypeConverter
import com.zaroslikov.domain.models.enums.AppIcon

class AppIconConverter {

    @TypeConverter
    fun fromAppIcon(value: AppIcon?): Int? = value?.code

    /**
     * Читает и значения версии 7+ ([AppIcon.code]), и идентификаторы ресурсов, записанные
     * старыми сборками: миграция чинит базу, а этот путь страхует строки, приехавшие
     * из бэкапа или созданные до миграции. Неизвестное значение — `null`, иконка по умолчанию.
     */
    @TypeConverter
    fun toAppIcon(value: Int?): AppIcon? = value?.let(AppIcon::fromStoredValue)
}