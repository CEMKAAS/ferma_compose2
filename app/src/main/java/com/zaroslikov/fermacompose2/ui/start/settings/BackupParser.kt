package com.zaroslikov.fermacompose2.ui.start.settings

import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.LegacyAppIcons
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Разбор файла бэкапа.
 *
 * Файлы, снятые до версии схемы 7, хранят в иконках идентификатор ресурса той сборки,
 * которая делала бэкап. Одно и то же число в разных сборках означает разные иконки,
 * поэтому переводим их не по одному, а всем файлом сразу: [LegacyAppIcons.tableFor]
 * подбирает таблицу той сборки, которая объясняет больше всего значений.
 *
 * Всё остальное разбирается как раньше — тем же [Json], так что несовпадение полей
 * по-прежнему честно роняет чтение файла, а не портит данные молча.
 */
object BackupParser {

    private const val ICON_FIELD = "currentIcon"
    private val ICON_TABLES = listOf("projectTable", "animalTable")

    fun parse(value: String): BackupData {
        val root = Json.parseToJsonElement(value)
        return Json.decodeFromJsonElement(normalizeIcons(root))
    }

    /** Заменяет идентификаторы ресурсов старых сборок на коды [AppIcon]. */
    internal fun normalizeIcons(root: JsonElement): JsonElement {
        if (root !is JsonObject) return root

        val legacyIds = LegacyAppIcons.tableFor(root.storedIcons())
        return JsonObject(
            root.mapValues { (name, value) ->
                if (name in ICON_TABLES && value is JsonArray) value.mapIcons(legacyIds) else value
            }
        )
    }

    private fun JsonObject.storedIcons(): List<Int> =
        ICON_TABLES.flatMap { table ->
            (this[table] as? JsonArray).orEmpty().mapNotNull { row ->
                (row as? JsonObject)?.get(ICON_FIELD)?.jsonPrimitive?.intOrNull
            }
        }

    private fun JsonArray.mapIcons(legacyIds: Map<Int, AppIcon>): JsonArray = JsonArray(
        map { row ->
            val stored = (row as? JsonObject)?.get(ICON_FIELD)?.jsonPrimitive?.intOrNull
                ?: return@map row

            // Код иконки уже стабилен — трогаем только идентификаторы ресурсов.
            if (AppIcon.fromCode(stored) != null) return@map row

            val icon = legacyIds[stored]
            JsonObject(
                (row as JsonObject) + (ICON_FIELD to (icon?.let { JsonPrimitive(it.code) }
                    ?: JsonNull))
            )
        }
    )

    private fun JsonArray?.orEmpty(): JsonArray = this ?: JsonArray(emptyList())
}