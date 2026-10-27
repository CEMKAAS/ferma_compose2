package com.zaroslikov.assistant.settings

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Настройки помощника: записывать без подтверждения. */
@Singleton
class AssistantPrefs @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("assistant_prefs", Context.MODE_PRIVATE)

    var autoConfirm: Boolean
        get() = prefs.getBoolean(KEY_AUTO_CONFIRM, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_CONFIRM, value).apply()

    private companion object {
        const val KEY_AUTO_CONFIRM = "auto_confirm"
    }
}
