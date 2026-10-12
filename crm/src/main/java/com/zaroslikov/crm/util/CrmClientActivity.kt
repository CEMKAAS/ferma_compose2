package com.zaroslikov.crm.util

import androidx.annotation.StringRes
import com.zaroslikov.crm.R
import com.zaroslikov.crm.domain.model.CrmClientActivity

/** Первая покупка не старше стольких дней — клиент «новый». */
const val CRM_NEW_CLIENT_DAYS = 30

/** Последняя покупка старше стольких дней — клиент «неактивный». */
const val CRM_INACTIVE_CLIENT_DAYS = 90

/**
 * Активность клиента по датам первой и последней покупки (`yyyy-MM-dd`).
 * Проверка «новый» идёт первой: клиент с единственной недавней покупкой — новый, а не активный.
 */
fun crmClientActivity(
    firstDate: String?,
    lastDate: String?,
    today: String = today()
): CrmClientActivity {
    val sinceLast = lastDate?.let { daysBetween(it, today) } ?: return CrmClientActivity.NO_SALES
    val sinceFirst = firstDate?.let { daysBetween(it, today) } ?: sinceLast
    return when {
        sinceFirst <= CRM_NEW_CLIENT_DAYS -> CrmClientActivity.NEW
        sinceLast > CRM_INACTIVE_CLIENT_DAYS -> CrmClientActivity.INACTIVE
        else -> CrmClientActivity.ACTIVE
    }
}

@StringRes
fun CrmClientActivity.toResId(): Int = when (this) {
    CrmClientActivity.NEW -> R.string.crm_activity_new
    CrmClientActivity.ACTIVE -> R.string.crm_activity_active
    CrmClientActivity.INACTIVE -> R.string.crm_activity_inactive
    CrmClientActivity.NO_SALES -> R.string.crm_activity_no_sales
}
