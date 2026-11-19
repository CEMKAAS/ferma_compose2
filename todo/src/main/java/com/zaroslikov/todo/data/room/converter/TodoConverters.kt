package com.zaroslikov.todo.data.room.converter

import androidx.room.TypeConverter
import com.zaroslikov.todo.domain.model.TodoAssigneeProfile
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoLinkType
import com.zaroslikov.todo.domain.model.TodoListIcon
import com.zaroslikov.todo.domain.model.TodoListViewMode
import com.zaroslikov.todo.domain.model.TodoPriority
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Enum'ы хранятся стабильным строковым кодом, а не ordinal/name. */
class TodoConverters {

    @TypeConverter
    fun fromAssigneeProfile(value: TodoAssigneeProfile?): String? = value?.let { Json.encodeToString(it) }

    @TypeConverter
    fun toAssigneeProfile(value: String?): TodoAssigneeProfile? = value?.let { Json.decodeFromString(it) }

    @TypeConverter
    fun fromAssignees(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toAssignees(value: String): List<String> = Json.decodeFromString(value)

    @TypeConverter
    fun fromRepeatPeriod(value: com.zaroslikov.todo.domain.model.TodoRepeatPeriod): String = value.code

    @TypeConverter
    fun toRepeatPeriod(value: String): com.zaroslikov.todo.domain.model.TodoRepeatPeriod =
        com.zaroslikov.todo.domain.model.TodoRepeatPeriod.fromCode(value)

    @TypeConverter
    fun fromPriority(value: TodoPriority): String = value.code

    @TypeConverter
    fun toPriority(value: String): TodoPriority = TodoPriority.fromCode(value)

    @TypeConverter
    fun fromLinkType(value: TodoLinkType): String = value.code

    @TypeConverter
    fun toLinkType(value: String): TodoLinkType = TodoLinkType.fromCode(value)

    @TypeConverter
    fun fromViewMode(value: TodoListViewMode): String = value.code

    @TypeConverter
    fun toViewMode(value: String): TodoListViewMode = TodoListViewMode.fromCode(value)

    @TypeConverter
    fun fromListIcon(value: TodoListIcon): String = value.code

    @TypeConverter
    fun toListIcon(value: String): TodoListIcon = TodoListIcon.fromCode(value)

    // NULL — пользовательский статус
    @TypeConverter
    fun fromDefaultStatus(value: TodoDefaultStatus?): String? = value?.code

    @TypeConverter
    fun toDefaultStatus(value: String?): TodoDefaultStatus? = TodoDefaultStatus.fromCode(value)
}
