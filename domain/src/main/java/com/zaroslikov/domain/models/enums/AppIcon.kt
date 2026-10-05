package com.zaroslikov.domain.models.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Стабильный идентификатор иконки проекта или животного.
 *
 * Наружу (в БД, в бэкап) уходит только [code] — константа приложения, которая никогда
 * не меняется. Идентификатор ресурса (`R.drawable.*`) хранить нельзя: aapt2 раздаёт его
 * заново на каждой сборке, и он сдвигается, когда меняется набор ресурсов приложения
 * или библиотек. Именно так сломалась 3.1.1: обновление зависимостей сократило таблицу
 * drawable с 488 до 412 записей, и сохранённый `outline_egg_24` (`0x7f0801e3`) перестал
 * существовать — `Resources$NotFoundException` на первом же экране.
 *
 * Ресурс подставляется только в UI-слое, см. `AppIcon.drawableRes` в модуле `:app`.
 */
enum class AppIcon(val code: Int) {
    LIVESTOCK(1),
    ICONS_CHICKEN_S(2),
    ICONS_GOAT(3),
    ICONS_COW(4),
    ICONS_PIG(5),
    ICONS_SHEEP(6),
    ICONS_HORSE(7),
    ICONS_RABBIT(8),
    ICONS_FARMING_PETS(9),
    ICONS_PETS(10),
    ICONS_PLANT(11),
    ICONS_FARMING_1(12),
    ICONS_FARMING_2(13),
    PETS(14),
    CHICKEN(15),
    CHICK(16),
    DUCK(17),
    GOOSE(18),
    QUAIL(19),
    TURKEY(20),
    CRUELTY_FREE(21),
    COW(22),
    HORSE(23),
    GOAT(24),
    SHEEP(25),
    PIG(26),
    EGG(27);

    companion object {

        private val BY_CODE: Map<Int, AppIcon> = entries.associateBy(AppIcon::code)

        fun fromCode(code: Int): AppIcon? = BY_CODE[code]

        /**
         * Читает значение, сохранённое любой версией приложения.
         *
         * Идентификатор ресурса опознаётся только если он означает одну и ту же иконку
         * во всех выпущенных сборках ([LegacyAppIcons.UNAMBIGUOUS]). Спорные значения дают
         * `null` — лучше иконка по умолчанию, чем чужая. Чтобы разобрать и спорные,
         * нужен весь набор значений сразу: [LegacyAppIcons.tableFor].
         */
        fun fromStoredValue(value: Int): AppIcon? =
            BY_CODE[value] ?: LegacyAppIcons.UNAMBIGUOUS[value]
    }
}

/**
 * Перевод иконок из сборок, которые хранили идентификатор ресурса (до версии схемы 7).
 *
 * За историю приложения таблица ресурсов перестраивалась шесть раз: id зависит от полного
 * набора ресурсов приложения и библиотек, поэтому любое обновление зависимостей или новая
 * картинка сдвигают номера. Значения ниже сняты с реальных сборок этих релизов
 * (`processReleaseResources`, файл `R.txt`).
 *
 * Одно и то же число в разных сборках означает разные иконки, поэтому по одному значению
 * восстановить иконку нельзя. Зато можно по всему набору: [tableFor] выбирает ту сборку,
 * которая объясняет больше всего сохранённых значений. Это же и есть та сборка, чьи
 * иконки пользователь видел последней.
 */
object LegacyAppIcons {

    private val TABLE_1_17: Map<Int, AppIcon> = mapOf(
        0x7f080088 to AppIcon.CRUELTY_FREE,
        0x7f08009c to AppIcon.PETS,
        0x7f0800ad to AppIcon.CHICKEN,
        0x7f0800ae to AppIcon.CHICK,
        0x7f0800c5 to AppIcon.COW,
        0x7f0800d9 to AppIcon.DUCK,
        0x7f0800de to AppIcon.GOOSE,
        0x7f0800df to AppIcon.GOAT,
        0x7f0800e6 to AppIcon.HORSE,
        0x7f08011e to AppIcon.ICONS_CHICKEN_S,
        0x7f08011f to AppIcon.ICONS_COW,
        0x7f080120 to AppIcon.ICONS_FARMING_1,
        0x7f080121 to AppIcon.ICONS_FARMING_2,
        0x7f080122 to AppIcon.ICONS_FARMING_PETS,
        0x7f080123 to AppIcon.ICONS_GOAT,
        0x7f080124 to AppIcon.ICONS_HORSE,
        0x7f080125 to AppIcon.ICONS_PETS,
        0x7f080126 to AppIcon.ICONS_PIG,
        0x7f080127 to AppIcon.ICONS_PLANT,
        0x7f080128 to AppIcon.ICONS_RABBIT,
        0x7f080129 to AppIcon.ICONS_SHEEP,
        0x7f08012b to AppIcon.LIVESTOCK,
        0x7f0801d4 to AppIcon.EGG,
        0x7f0801e5 to AppIcon.PIG,
        0x7f0801e8 to AppIcon.QUAIL,
        0x7f0801ec to AppIcon.SHEEP,
        0x7f0801f2 to AppIcon.TURKEY,
    )

    private val TABLE_3_0_0: Map<Int, AppIcon> = mapOf(
        0x7f08008b to AppIcon.CRUELTY_FREE,
        0x7f08009f to AppIcon.PETS,
        0x7f0800b1 to AppIcon.CHICKEN,
        0x7f0800b2 to AppIcon.CHICK,
        0x7f0800cb to AppIcon.COW,
        0x7f0800df to AppIcon.DUCK,
        0x7f0800e4 to AppIcon.GOOSE,
        0x7f0800e5 to AppIcon.GOAT,
        0x7f0800ec to AppIcon.HORSE,
        0x7f080129 to AppIcon.ICONS_CHICKEN_S,
        0x7f08012a to AppIcon.ICONS_COW,
        0x7f08012b to AppIcon.ICONS_FARMING_1,
        0x7f08012c to AppIcon.ICONS_FARMING_2,
        0x7f08012d to AppIcon.ICONS_FARMING_PETS,
        0x7f08012e to AppIcon.ICONS_GOAT,
        0x7f08012f to AppIcon.ICONS_HORSE,
        0x7f080130 to AppIcon.ICONS_PETS,
        0x7f080131 to AppIcon.ICONS_PIG,
        0x7f080132 to AppIcon.ICONS_PLANT,
        0x7f080133 to AppIcon.ICONS_RABBIT,
        0x7f080134 to AppIcon.ICONS_SHEEP,
        0x7f080136 to AppIcon.LIVESTOCK,
        0x7f0801df to AppIcon.EGG,
        0x7f0801f0 to AppIcon.PIG,
        0x7f0801f3 to AppIcon.QUAIL,
        0x7f0801f7 to AppIcon.SHEEP,
        0x7f0801fd to AppIcon.TURKEY,
    )

    private val TABLE_3_0_3: Map<Int, AppIcon> = mapOf(
        0x7f08008b to AppIcon.CRUELTY_FREE,
        0x7f08009f to AppIcon.PETS,
        0x7f0800b1 to AppIcon.CHICKEN,
        0x7f0800b2 to AppIcon.CHICK,
        0x7f0800cb to AppIcon.COW,
        0x7f0800df to AppIcon.DUCK,
        0x7f0800e4 to AppIcon.GOOSE,
        0x7f0800e5 to AppIcon.GOAT,
        0x7f0800ec to AppIcon.HORSE,
        0x7f080129 to AppIcon.ICONS_CHICKEN_S,
        0x7f08012a to AppIcon.ICONS_COW,
        0x7f08012b to AppIcon.ICONS_FARMING_1,
        0x7f08012c to AppIcon.ICONS_FARMING_2,
        0x7f08012d to AppIcon.ICONS_FARMING_PETS,
        0x7f08012e to AppIcon.ICONS_GOAT,
        0x7f08012f to AppIcon.ICONS_HORSE,
        0x7f080130 to AppIcon.ICONS_PETS,
        0x7f080131 to AppIcon.ICONS_PIG,
        0x7f080132 to AppIcon.ICONS_PLANT,
        0x7f080133 to AppIcon.ICONS_RABBIT,
        0x7f080134 to AppIcon.ICONS_SHEEP,
        0x7f080136 to AppIcon.LIVESTOCK,
        0x7f0801e0 to AppIcon.EGG,
        0x7f0801f8 to AppIcon.PIG,
        0x7f0801fc to AppIcon.QUAIL,
        0x7f080200 to AppIcon.SHEEP,
        0x7f080206 to AppIcon.TURKEY,
    )

    private val TABLE_3_1_0: Map<Int, AppIcon> = mapOf(
        0x7f08008b to AppIcon.CRUELTY_FREE,
        0x7f08009f to AppIcon.PETS,
        0x7f0800b1 to AppIcon.CHICKEN,
        0x7f0800b2 to AppIcon.CHICK,
        0x7f0800cb to AppIcon.COW,
        0x7f0800df to AppIcon.DUCK,
        0x7f0800e4 to AppIcon.GOOSE,
        0x7f0800e5 to AppIcon.GOAT,
        0x7f0800ec to AppIcon.HORSE,
        0x7f08012b to AppIcon.ICONS_CHICKEN_S,
        0x7f08012c to AppIcon.ICONS_COW,
        0x7f08012d to AppIcon.ICONS_FARMING_1,
        0x7f08012e to AppIcon.ICONS_FARMING_2,
        0x7f08012f to AppIcon.ICONS_FARMING_PETS,
        0x7f080130 to AppIcon.ICONS_GOAT,
        0x7f080131 to AppIcon.ICONS_HORSE,
        0x7f080132 to AppIcon.ICONS_PETS,
        0x7f080133 to AppIcon.ICONS_PIG,
        0x7f080134 to AppIcon.ICONS_PLANT,
        0x7f080135 to AppIcon.ICONS_RABBIT,
        0x7f080136 to AppIcon.ICONS_SHEEP,
        0x7f080138 to AppIcon.LIVESTOCK,
        0x7f0801e3 to AppIcon.EGG,
        0x7f0801fc to AppIcon.PIG,
        0x7f080200 to AppIcon.QUAIL,
        0x7f080204 to AppIcon.SHEEP,
        0x7f08020a to AppIcon.TURKEY,
    )

    private val TABLE_3_1_0_RUSTORE: Map<Int, AppIcon> = mapOf(
        0x7f08008b to AppIcon.CRUELTY_FREE,
        0x7f08009f to AppIcon.PETS,
        0x7f0800b1 to AppIcon.CHICKEN,
        0x7f0800b2 to AppIcon.CHICK,
        0x7f0800cb to AppIcon.COW,
        0x7f0800df to AppIcon.DUCK,
        0x7f0800e4 to AppIcon.GOOSE,
        0x7f0800e5 to AppIcon.GOAT,
        0x7f0800ec to AppIcon.HORSE,
        0x7f08012b to AppIcon.ICONS_CHICKEN_S,
        0x7f08012c to AppIcon.ICONS_COW,
        0x7f08012d to AppIcon.ICONS_FARMING_1,
        0x7f08012e to AppIcon.ICONS_FARMING_2,
        0x7f08012f to AppIcon.ICONS_FARMING_PETS,
        0x7f080130 to AppIcon.ICONS_GOAT,
        0x7f080131 to AppIcon.ICONS_HORSE,
        0x7f080132 to AppIcon.ICONS_PETS,
        0x7f080133 to AppIcon.ICONS_PIG,
        0x7f080134 to AppIcon.ICONS_PLANT,
        0x7f080135 to AppIcon.ICONS_RABBIT,
        0x7f080136 to AppIcon.ICONS_SHEEP,
        0x7f080138 to AppIcon.LIVESTOCK,
        0x7f0801e3 to AppIcon.EGG,
        0x7f0801fb to AppIcon.PIG,
        0x7f0801ff to AppIcon.QUAIL,
        0x7f080203 to AppIcon.SHEEP,
        0x7f080209 to AppIcon.TURKEY,
    )

    private val TABLE_3_1_1: Map<Int, AppIcon> = mapOf(
        0x7f080061 to AppIcon.CRUELTY_FREE,
        0x7f080075 to AppIcon.PETS,
        0x7f080086 to AppIcon.CHICKEN,
        0x7f080087 to AppIcon.CHICK,
        0x7f0800a0 to AppIcon.COW,
        0x7f0800ac to AppIcon.DUCK,
        0x7f0800b0 to AppIcon.GOOSE,
        0x7f0800b1 to AppIcon.GOAT,
        0x7f0800b8 to AppIcon.HORSE,
        0x7f0800ea to AppIcon.ICONS_CHICKEN_S,
        0x7f0800eb to AppIcon.ICONS_COW,
        0x7f0800ec to AppIcon.ICONS_FARMING_1,
        0x7f0800ed to AppIcon.ICONS_FARMING_2,
        0x7f0800ee to AppIcon.ICONS_FARMING_PETS,
        0x7f0800ef to AppIcon.ICONS_GOAT,
        0x7f0800f0 to AppIcon.ICONS_HORSE,
        0x7f0800f1 to AppIcon.ICONS_PETS,
        0x7f0800f2 to AppIcon.ICONS_PIG,
        0x7f0800f3 to AppIcon.ICONS_PLANT,
        0x7f0800f4 to AppIcon.ICONS_RABBIT,
        0x7f0800f5 to AppIcon.ICONS_SHEEP,
        0x7f0800f6 to AppIcon.LIVESTOCK,
        0x7f080170 to AppIcon.EGG,
        0x7f080188 to AppIcon.PIG,
        0x7f08018c to AppIcon.QUAIL,
        0x7f080190 to AppIcon.SHEEP,
        0x7f080195 to AppIcon.TURKEY,
    )

    /** Все выпущенные таблицы, от новых к старым — на них опирается [tableFor]. */
    private val TABLES: List<Map<Int, AppIcon>> = listOf(
        TABLE_3_1_1,
        TABLE_3_1_0,
        TABLE_3_1_0_RUSTORE,
        TABLE_3_0_3,
        TABLE_3_0_0,
        TABLE_1_17,
    )

    /** Значения, которые во всех сборках означают одну и ту же иконку. */
    val UNAMBIGUOUS: Map<Int, AppIcon> = buildMap {
        val contested = mutableSetOf<Int>()
        TABLES.forEach { table ->
            table.forEach { (id, icon) ->
                val known = this[id]
                if (known != null && known != icon) contested += id
                put(id, icon)
            }
        }
        contested.forEach(::remove)
    }

    /**
     * Подбирает таблицу перевода под конкретную базу или файл бэкапа.
     *
     * [storedValues] — все сохранённые значения иконок. Побеждает сборка, которая
     * объясняет больше всего значений: её иконки пользователь и видел до обновления.
     * При ничьей (например, сборки 3.1.0 для Google и RuStore почти одинаковы) берётся
     * то, в чём победители согласны. Что осталось спорным, сбросится на иконку
     * по умолчанию — лучше так, чем показать чужую.
     */
    fun tableFor(storedValues: Collection<Int>): Map<Int, AppIcon> {
        val values = storedValues.toSet()
        if (values.isEmpty()) return UNAMBIGUOUS

        val scores = TABLES.map { table -> values.count(table::containsKey) }
        val best = scores.max()
        if (best == 0) return UNAMBIGUOUS

        val winners = TABLES.filterIndexed { index, _ -> scores[index] == best }
        val agreed = winners.reduce { agreed, table ->
            agreed.filter { (id, icon) -> table[id] == icon }
        }
        return UNAMBIGUOUS + agreed
    }
}

/**
 * Сериализует иконку числом, чтобы бэкапы, снятые до перехода на [AppIcon], читались
 * без потерь: там на этом месте лежит идентификатор ресурса старой сборки.
 *
 * Спорные идентификаторы этот сериализатор не разбирает — для них нужен весь файл сразу,
 * этим занимается нормализация бэкапа перед разбором.
 */
object AppIconAsCodeSerializer : KSerializer<AppIcon?> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.zaroslikov.domain.models.enums.AppIcon", PrimitiveKind.INT)
            .nullable

    override fun serialize(encoder: Encoder, value: AppIcon?) {
        if (value == null) encoder.encodeNull() else encoder.encodeInt(value.code)
    }

    override fun deserialize(decoder: Decoder): AppIcon? =
        if (decoder.decodeNotNullMark()) {
            AppIcon.fromStoredValue(decoder.decodeInt())
        } else {
            decoder.decodeNull()
        }
}