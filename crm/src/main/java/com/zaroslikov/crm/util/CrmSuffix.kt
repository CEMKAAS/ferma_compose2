package com.zaroslikov.crm.util

import androidx.annotation.StringRes
import com.zaroslikov.crm.R
import com.zaroslikov.crm.domain.model.CrmContactType
import com.zaroslikov.crm.domain.model.CrmInteractionType
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Подпись единицы/валюты. Строки `:app` модулю недоступны — копия в `crm_suffix_*`. */
@StringRes
fun Suffix.toCrmResId(): Int = when (this) {
    Suffix.PIECES -> R.string.crm_suffix_pieces
    Suffix.HEADS -> R.string.crm_suffix_heads
    Suffix.UNITS -> R.string.crm_suffix_units
    Suffix.GRAM -> R.string.crm_suffix_gram
    Suffix.KILOGRAM -> R.string.crm_suffix_kilogram
    Suffix.TONS -> R.string.crm_suffix_tons
    Suffix.MILLILITRES -> R.string.crm_suffix_millilitres
    Suffix.LITERS -> R.string.crm_suffix_liters
    Suffix.CUBIC_METERS -> R.string.crm_suffix_cubic_meters
    Suffix.MILLIMETERS -> R.string.crm_suffix_millimeters
    Suffix.CENTIMETERS -> R.string.crm_suffix_centimeters
    Suffix.METERS -> R.string.crm_suffix_meters
    Suffix.RUBLE -> R.string.crm_suffix_ruble
    Suffix.TENGE -> R.string.crm_suffix_tenge
    Suffix.DOLLAR -> R.string.crm_suffix_dollar
    Suffix.EURO -> R.string.crm_suffix_euro
    Suffix.GRAM_DAY -> R.string.crm_suffix_gram_day
    Suffix.KILOGRAM_DAY -> R.string.crm_suffix_kilogram_day
    Suffix.TONS_DAY -> R.string.crm_suffix_tons_day
    Suffix.KILOGRAM_TO_CUBIC_METERS -> R.string.crm_suffix_kilogram_to_cubic_meters
    Suffix.KILOGRAM_TO_LITERS -> R.string.crm_suffix_kilogram_to_liters
    Suffix.NO -> R.string.crm_suffix_no
}

@StringRes
fun CrmContactType.toResId(): Int = when (this) {
    CrmContactType.PHONE -> R.string.crm_contact_phone
    CrmContactType.EMAIL -> R.string.crm_contact_email
    CrmContactType.TELEGRAM -> R.string.crm_contact_telegram
    CrmContactType.WHATSAPP -> R.string.crm_contact_whatsapp
    CrmContactType.VK -> R.string.crm_contact_vk
    CrmContactType.ADDRESS -> R.string.crm_contact_address
    CrmContactType.OTHER -> R.string.crm_contact_other
}

@StringRes
fun CrmInteractionType.toResId(): Int = when (this) {
    CrmInteractionType.CALL -> R.string.crm_interaction_call
    CrmInteractionType.MEETING -> R.string.crm_interaction_meeting
    CrmInteractionType.MESSAGE -> R.string.crm_interaction_message
    CrmInteractionType.NOTE -> R.string.crm_interaction_note
}

/** Валюта проекта; у проекта может не быть строки настроек — тогда рубль. */
fun SettingsRepository.crmCurrency(idPT: Long): Flow<Suffix> =
    getSettings(idPT)
        .map { it.currencySuffix }
        .catch { emit(Suffix.RUBLE) }
