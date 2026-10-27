package com.zaroslikov.assistant.nlu

/** Нечёткое сопоставление слов пользователя с названиями из БД (продукты, животные, покупатели). */
object EntityResolver {

    data class Match<T>(val value: T, val score: Double)

    /** Сходство двух слов: 1.0 — совпали, 0.9 — общий стем, 0.75 — одно начинается с другого, ≤0.7 — опечатка. */
    fun wordScore(query: String, candidate: String): Double {
        val q = normalizeRu(query)
        val c = normalizeRu(candidate)
        if (q.isEmpty() || c.isEmpty()) return 0.0
        if (q == c) return 1.0
        val sq = RuStemmer.stem(q)
        val sc = RuStemmer.stem(c)
        if (sq == sc && sq.length >= 3) return 0.9
        val minStem = minOf(sq.length, sc.length)
        if (minStem >= 4 && (sq.startsWith(sc) || sc.startsWith(sq))) return 0.75
        if (minStem >= 5 && levenshtein(sq, sc) <= 1) return 0.7
        if (minOf(q.length, c.length) >= 6 && levenshtein(q, c) <= 2) return 0.6
        return 0.0
    }

    /**
     * Сходство набора слов запроса с названием из нескольких слов: среднее лучших совпадений
     * по словам запроса; каждое лишнее слово кандидата снижает оценку, чтобы «молоко» предпочиталось
     * «козьему молоку», когда сказано просто «молока».
     */
    fun phraseScore(queryWords: List<String>, candidate: String): Double {
        val candidateWords = normalizeRu(candidate).split(Regex("""[\s\-]+""")).filter { it.isNotBlank() }
        if (queryWords.isEmpty() || candidateWords.isEmpty()) return 0.0
        val perWord = queryWords.map { q -> candidateWords.maxOf { c -> wordScore(q, c) } }
        if (perWord.any { it < 0.6 }) return 0.0
        val base = perWord.average()
        val extra = (candidateWords.size - queryWords.size).coerceAtLeast(0)
        return (base - extra * 0.1).coerceAtLeast(0.0)
    }

    fun <T> best(queryWords: List<String>, candidates: List<T>, threshold: Double, name: (T) -> String): Match<T>? =
        candidates
            .map { Match(it, phraseScore(queryWords, name(it))) }
            .filter { it.score >= threshold }
            .maxByOrNull { it.score }

    fun <T> bestWord(query: String, candidates: List<T>, threshold: Double, name: (T) -> String): Match<T>? =
        best(listOf(query), candidates, threshold, name)
}
