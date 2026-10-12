package com.zaroslikov.domain.models.animal.group

object AnimalAutoName {
    /** Имена "base k": нумерация с max(k)+1 среди существующих "base число" (без учёта регистра). */
    fun generate(base: String, existingNames: Collection<String>, n: Int): List<String> {
        if (n <= 0) return emptyList()
        val regex = Regex("^" + Regex.escape(base) + " (\\d+)$", RegexOption.IGNORE_CASE)
        val max = existingNames.maxOfOrNull {
            regex.matchEntire(it)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
        } ?: 0L
        return (1..n).map { "$base ${max + it}" }
    }
}

/** Сколько особей можно выделить из счётчика группы за одну операцию. */
const val MAX_SPLIT = 200
