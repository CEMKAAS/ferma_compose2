package com.zaroslikov.fermacompose2.utils

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Птенцы из «Инкубатора» между разбором ссылки и выбором проекта — тот же приём, что у
 * [QrNavigationManager] для шаблонов: ссылка разбирается при запуске
 * (`InventoryAppViewModel`), а спрашивает «куда» первый экран (`FirstViewModel`).
 *
 * Второе, что он держит, — одноразовая просьба открыть проект на странице «Животные»:
 * после записи птенцов первый экран уводит в проект, и показать стоит именно новую
 * группу, а не страницу по умолчанию. Её забирает `SectionWorkspaceViewModel`.
 */
@Singleton
class IncubatorChicksManager @Inject constructor() {
    private var chicks: IncubatorChicks? = null
    private var showAnimalsPage = false

    fun put(chicks: IncubatorChicks) {
        this.chicks = chicks
    }

    fun peek(): IncubatorChicks? = chicks

    fun consume(): IncubatorChicks? {
        val value = chicks
        chicks = null
        return value
    }

    fun clear() {
        chicks = null
    }

    fun requestAnimalsPage() {
        showAnimalsPage = true
    }

    fun consumeAnimalsPage(): Boolean {
        val value = showAnimalsPage
        showAnimalsPage = false
        return value
    }
}
