package com.zaroslikov.domain.models.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Вид животного. Хранится в `animal_table.species` кодом [code] — по нему строятся расчёты
 * по виду (например, беременности для [isMammal]; птиц выводят в инкубаторе).
 *
 * `animal_table.type` при этом остаётся названием для показа: у известного вида это имя
 * по полу/группе («Корова»/«Бык»/«Коровы»), у [UNKNOWN] — тип, который вписал пользователь.
 */
enum class AnimalSpecies(
    val code: Int,
    /** Общепринятое название вида; может совпадать с [femaleName] или [maleName]. */
    val commonName: String,
    val femaleName: String,
    val maleName: String,
    /** Название для группы животных. */
    val groupName: String,
    val isBird: Boolean,
    /** Другие написания (молодняк, синонимы): вид по ним распознаётся, но название не переименовывается. */
    internal val aliases: List<String> = emptyList(),
) {
    /** Вид, неизвестный системе: название хранится только в `type`. */
    UNKNOWN(0, "", "", "", "", isBird = false),
    RABBIT(1, "Кролик", "Крольчиха", "Кролик", "Кролики", isBird = false, listOf("Крольчата")),
    PIG(2, "Свинья", "Свинья", "Хряк", "Свиньи", isBird = false, listOf("Свиноматка", "Поросёнок", "Поросята")),
    COW(3, "Корова", "Корова", "Бык", "Коровы", isBird = false, listOf("Быки", "Тёлка", "Телёнок", "Телята", "Бычок", "КРС")),
    SHEEP(4, "Овца", "Овца", "Баран", "Овцы", isBird = false, listOf("Бараны", "Ягнёнок", "Ягнята")),
    GOAT(5, "Коза", "Коза", "Козёл", "Козы", isBird = false, listOf("Козлы", "Козлёнок", "Козлята")),
    HORSE(6, "Лошадь", "Кобыла", "Жеребец", "Лошади", isBird = false, listOf("Конь", "Кони", "Жеребёнок", "Жеребята")),
    TURKEY(7, "Индюк", "Индейка", "Индюк", "Индюки", isBird = true, listOf("Индюшка", "Индейки", "Индюшата")),
    CHICKEN(8, "Курица", "Курица", "Петух", "Курицы", isBird = true, listOf("Куры", "Несушка", "Несушки", "Петухи", "Цыплята")),
    QUAIL(9, "Перепел", "Перепёлка", "Перепел", "Перепела", isBird = true, listOf("Перепёлки", "Перепелята")),
    DUCK(10, "Утка", "Утка", "Селезень", "Утки", isBird = true, listOf("Утята")),
    GOOSE(11, "Гусь", "Гусыня", "Гусь", "Гуси", isBird = true, listOf("Гусята")),
    PHEASANT(12, "Фазан", "Фазаниха", "Фазан", "Фазаны", isBird = true, listOf("Фазанка", "Фазанята")),
    MUSCOVY_DUCK(13, "Индоутка", "Индоутка", "Индоселезень", "Индоутки", isBird = true, listOf("Мускусная утка"));

    val isKnown: Boolean get() = this != UNKNOWN

    val isMammal: Boolean get() = isKnown && !isBird

    /** Название особи по полу: `true` — самец, `false` — самка (как `DomainAnimalTable.sex`). */
    fun nameFor(sex: Boolean): String = if (sex) maleName else femaleName

    fun nameFor(isGroup: Boolean, sex: Boolean): String = if (isGroup) groupName else nameFor(sex)

    /** Названия, которые приводятся к полу/группе. */
    private val canonicalNames: Set<String> by lazy {
        listOf(commonName, femaleName, maleName, groupName).map { normalize(it) }.toSet()
    }

    private val allNames: Set<String> by lazy {
        canonicalNames + aliases.map { normalize(it) }
    }

    companion object {
        /** Известные виды — для подсказок и перебора. */
        val known: List<AnimalSpecies> = entries.filter { it.isKnown }

        /** Неизвестный код (например, из бэкапа более новой версии) — [UNKNOWN]. */
        fun fromCode(code: Int): AnimalSpecies = entries.firstOrNull { it.code == code } ?: UNKNOWN

        /** Вид по введённому типу (без учёта регистра, пробелов по краям и «ё»), или `null` для своего типа. */
        fun fromType(type: String): AnimalSpecies? {
            val key = normalize(type)
            if (key.isEmpty()) return null
            return known.firstOrNull { key in it.allNames }
        }

        /** Вид по типу; свой тип — [UNKNOWN]. */
        fun resolve(type: String): AnimalSpecies = fromType(type) ?: UNKNOWN

        fun fromTypeEgg(typeEgg: TypeEgg): AnimalSpecies = when (typeEgg) {
            TypeEgg.CHICKENS -> CHICKEN
            TypeEgg.GEESE -> GOOSE
            TypeEgg.QUAILS -> QUAIL
            TypeEgg.TURKEYS -> TURKEY
            TypeEgg.DUCKS -> DUCK
        }

        /**
         * Пол, однозначно заданный названием («Бык» → самец, «Крольчиха» → самка), иначе `null`.
         * Общепринятое название вида («Кролик», «Гусь», «Корова») пол не задаёт.
         */
        fun sexOf(type: String): Boolean? {
            val key = normalize(type)
            val species = fromType(type) ?: return null
            if (key == normalize(species.commonName)) return null
            return when (key) {
                normalize(species.maleName) -> true
                normalize(species.femaleName) -> false
                else -> null
            }
        }

        /**
         * Приводит название вида к полу/группе («Корова» + самец → «Бык»).
         * Свой тип и синонимы («Несушка», «Телёнок») не меняются.
         */
        fun adaptType(type: String, isGroup: Boolean, sex: Boolean): String {
            val species = fromType(type) ?: return type
            return if (normalize(type) in species.canonicalNames) species.nameFor(isGroup, sex) else type
        }

        /**
         * Подсказки для поля «Тип»: популярные виды в нужной форме + свои типы, уже встречавшиеся
         * в проекте. У своих типов [AnimalTypeSuggestion.species] = UNKNOWN, даже если это синоним
         * («Цыплята») — так UI отличает системные типы от введённых пользователем.
         */
        fun suggestions(isGroup: Boolean, sex: Boolean, existingTypes: List<String>): List<AnimalTypeSuggestion> {
            val custom = existingTypes
                .map { it.trim() }
                .filter { type -> type.isNotEmpty() && known.none { normalize(type) in it.canonicalNames } }
                .distinctBy { normalize(it) }
            return known.map { AnimalTypeSuggestion(it.nameFor(isGroup, sex), it) } +
                    custom.map { AnimalTypeSuggestion(it, UNKNOWN) }
        }

        private fun normalize(value: String): String =
            value.trim().lowercase().replace('ё', 'е')
    }
}

/** Подсказка типа: [species] = UNKNOWN — тип, введённый пользователем. */
data class AnimalTypeSuggestion(val name: String, val species: AnimalSpecies) {
    val isSystem: Boolean get() = species.isKnown
}

/** Вид числом [AnimalSpecies.code]: бэкапы и QR не зависят от имён констант. */
object AnimalSpeciesAsCodeSerializer : KSerializer<AnimalSpecies> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.zaroslikov.domain.models.enums.AnimalSpecies", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: AnimalSpecies) = encoder.encodeInt(value.code)

    override fun deserialize(decoder: Decoder): AnimalSpecies = AnimalSpecies.fromCode(decoder.decodeInt())
}
