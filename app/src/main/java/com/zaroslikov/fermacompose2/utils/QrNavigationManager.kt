package com.zaroslikov.fermacompose2.utils

import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrPayload
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QrNavigationManager @Inject constructor() {
    private var template: QrPayload? = null

    fun put(template: QrPayload) {
        this.template = template
    }

    fun peek(): QrPayload? = template

    fun consume(): QrPayload? {
        val value = template
        template = null
        return value
    }

    fun clear() {
        template = null
    }
}