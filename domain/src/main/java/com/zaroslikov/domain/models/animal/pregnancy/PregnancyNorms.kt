package com.zaroslikov.domain.models.animal.pregnancy

import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason

/**
 * Нормы беременности по видам (ориентиры, дни от случки). Только для [AnimalSpecies.isMammal].
 * Окна «за N дней до родов» переведены в дни от случки.
 */
object PregnancyNorms {

    private val RABBIT = norms(
        species = AnimalSpecies.RABBIT, gestation = 31, min = 28, max = 35,
        terms = PregnancyTerms("Сукрольна", "Окрол", "Крольчонок", "Крольчата"),
        stages = listOf(
            stage(
                "palpation", 10, 14, "Контрольная случка / пальпация",
                "Подсадите самца повторно или прощупайте живот: плоды размером с горошину",
                PregnancyStageKind.CHECK
            ),
            stage(
                "nest", 25, 27, "Поставить маточник",
                "Поставьте маточник с сеном и соломой, обеспечьте свободный доступ к воде",
                PregnancyStageKind.PREPARATION
            ),
        ),
        tips = listOf(
            "За 1–3 дня до окрола крольчиха выщипывает пух — не беспокойте её",
            "Возможна ложная сукрольность и рассасывание — проверьте пальпацией на 10–14 день",
            "Перед окролом крольчихе нужно много воды: при жажде она может съесть крольчат",
        ),
    )

    private val PIG = norms(
        species = AnimalSpecies.PIG, gestation = 114, min = 110, max = 118,
        terms = PregnancyTerms("Супоросна", "Опорос", "Поросёнок", "Поросята"),
        stages = listOf(
            stage(
                "heat_check", 18, 24, "Проверка на повторную охоту",
                "Если охота повторилась — свиноматка не покрылась, нужна повторная случка",
                PregnancyStageKind.CHECK
            ),
            stage(
                "ultrasound", 25, 35, "УЗИ супоросности",
                "Подтвердите супоросность УЗИ",
                PregnancyStageKind.EXAM
            ),
            stage(
                "farrowing_pen", 107, 109, "Перевод в станок для опороса",
                "Переведите свиноматку в чистый подготовленный станок за 5–7 дней до опороса",
                PregnancyStageKind.PREPARATION
            ),
        ),
        tips = listOf(
            "Срок супоросности легко запомнить: 3 месяца, 3 недели и 3 дня",
            "За сутки до опороса свиноматка беспокоится и носит подстилку — гнездится",
            "Не перекармливайте во второй половине срока: крупные плоды осложняют опорос",
        ),
    )

    private val COW = norms(
        species = AnimalSpecies.COW, gestation = 283, min = 270, max = 295,
        terms = PregnancyTerms("Стельна", "Отёл", "Телёнок", "Телята"),
        stages = listOf(
            stage(
                "heat_check", 18, 24, "Проверка на повторную охоту",
                "Если охота повторилась — корова не покрылась, нужно повторное осеменение",
                PregnancyStageKind.CHECK
            ),
            stage(
                "rectal_exam", 30, 60, "Ректальное исследование / УЗИ",
                "Подтвердите стельность у ветеринара",
                PregnancyStageKind.EXAM
            ),
            stage(
                "dry_off", 223, 223, "Запуск (сухостой)",
                "Прекратите доение за 60 дней до отёла — корова переходит в сухостой",
                PregnancyStageKind.PREPARATION
            ),
        ),
        tips = listOf(
            "Сухостойный период нужен для восстановления вымени и развития телёнка",
            "За 1–2 дня до отёла связки у корня хвоста расслабляются, вымя наливается",
            "Подготовьте чистое сухое место для отёла заранее",
        ),
    )

    private val SHEEP = norms(
        species = AnimalSpecies.SHEEP, gestation = 147, min = 142, max = 152,
        terms = PregnancyTerms("Суягна", "Окот", "Ягнёнок", "Ягнята"),
        stages = listOf(
            stage(
                "heat_check", 16, 17, "Проверка на повторную охоту",
                "Если охота повторилась — овца не покрылась",
                PregnancyStageKind.CHECK
            ),
            stage(
                "ultrasound", 40, 60, "УЗИ суягности",
                "Подтвердите суягность и число плодов УЗИ",
                PregnancyStageKind.EXAM
            ),
            stage(
                "lambing_prep", 133, 133, "Подготовка к окоту",
                "За 2 недели до окота подготовьте кошару и выстригите шерсть вокруг вымени",
                PregnancyStageKind.PREPARATION
            ),
        ),
        tips = listOf(
            "В последние 6 недель плоды растут быстрее всего — усильте кормление",
            "При многоплодии выше риск кетоза: следите за аппетитом овцы",
        ),
    )

    private val GOAT = norms(
        species = AnimalSpecies.GOAT, gestation = 150, min = 145, max = 155,
        terms = PregnancyTerms("Сукозна", "Окот", "Козлёнок", "Козлята"),
        stages = listOf(
            stage(
                "heat_check", 18, 22, "Проверка на повторную охоту",
                "Если охота повторилась — коза не покрылась",
                PregnancyStageKind.CHECK
            ),
            stage(
                "ultrasound", 30, 50, "УЗИ сукозности",
                "Подтвердите сукозность УЗИ",
                PregnancyStageKind.EXAM
            ),
            stage(
                "dry_off", 90, 105, "Запуск",
                "Прекратите доение за 45–60 дней до окота",
                PregnancyStageKind.PREPARATION
            ),
        ),
        tips = listOf(
            "Запуск обязателен: без отдыха вымени снижаются удои после окота",
            "За несколько дней до окота связки у хвоста расслабляются, вымя наливается",
        ),
    )

    private val HORSE = norms(
        species = AnimalSpecies.HORSE, gestation = 340, min = 320, max = 360,
        terms = PregnancyTerms("Жерёбая", "Выжеребка", "Жеребёнок", "Жеребята"),
        stages = listOf(
            stage(
                "ultrasound_1", 14, 16, "УЗИ",
                "Первое УЗИ: подтверждение жерёбости и проверка на двойню",
                PregnancyStageKind.EXAM
            ),
            stage(
                "ultrasound_2", 25, 30, "Повторное УЗИ (сердцебиение)",
                "Проверьте сердцебиение плода",
                PregnancyStageKind.EXAM
            ),
            stage(
                "ultrasound_3", 45, 60, "Контрольное УЗИ",
                "Контрольное УЗИ: жерёбость сохраняется",
                PregnancyStageKind.EXAM
            ),
            stage(
                "foaling_prep", 310, 310, "Подготовка к выжеребке",
                "За месяц до выжеребки подготовьте денник и наблюдайте за кобылой",
                PregnancyStageKind.PREPARATION
            ),
        ),
        tips = listOf(
            "Срок жерёбости сильно колеблется — до 2–3 недель в обе стороны от среднего",
            "Перед выжеребкой на сосках появляются «восковые пробки»",
            "Двойня у лошадей опасна — её важно выявить на первом УЗИ",
        ),
    )

    val all: List<SpeciesPregnancyNorms> = listOf(RABBIT, PIG, COW, SHEEP, GOAT, HORSE)

    private val bySpecies: Map<AnimalSpecies, SpeciesPregnancyNorms> = all.associateBy { it.species }

    /** `null` для птиц и [AnimalSpecies.UNKNOWN]. */
    fun of(species: AnimalSpecies): SpeciesPregnancyNorms? = bySpecies[species]

    private fun stage(
        key: String,
        from: Int,
        to: Int,
        title: String,
        description: String,
        kind: PregnancyStageKind
    ) = PregnancyStage(key, from, to, title, description, notify = true, kind = kind)

    /** Добавляет к этапам вида три общих: окно родов, день родов и превышение срока. */
    private fun norms(
        species: AnimalSpecies,
        gestation: Int,
        min: Int,
        max: Int,
        terms: PregnancyTerms,
        stages: List<PregnancyStage>,
        tips: List<String>,
    ): SpeciesPregnancyNorms {
        val birth = terms.birthName.lowercase()
        val common = listOf(
            PregnancyStage(
                key = "birth_window", dayFrom = min, dayTo = max,
                title = "Скоро $birth",
                description = "${terms.birthName} может начаться в любой день — наблюдайте за самкой",
                notify = true, kind = PregnancyStageKind.BIRTH_WINDOW
            ),
            PregnancyStage(
                key = "birth", dayFrom = gestation, dayTo = gestation,
                title = "Ожидается $birth",
                description = "По сроку сегодня ожидается $birth",
                notify = true, kind = PregnancyStageKind.BIRTH
            ),
            PregnancyStage(
                key = "overdue", dayFrom = max + 1, dayTo = max + 1,
                title = "Срок превышен",
                description = "Срок превышен — осмотр ветеринара",
                notify = true, kind = PregnancyStageKind.OVERDUE
            ),
        )
        return SpeciesPregnancyNorms(
            species = species,
            gestationDays = gestation,
            minDays = min,
            maxDays = max,
            terms = terms,
            stages = (stages + common).sortedBy { it.dayFrom },
            tips = tips,
            interruptReasons = PregnancyInterruptReason.entries.filter {
                it != PregnancyInterruptReason.RESORPTION || species == AnimalSpecies.RABBIT
            },
        )
    }
}
