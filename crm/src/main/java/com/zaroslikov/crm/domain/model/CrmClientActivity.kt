package com.zaroslikov.crm.domain.model

/**
 * Активность клиента. Не хранится в БД — вычисляется в UI по датам покупок,
 * см. `util/CrmClientActivity.kt`.
 */
enum class CrmClientActivity {
    /** Первая покупка не старше 30 дней. */
    NEW,

    /** Покупал за последние 90 дней. */
    ACTIVE,

    /** Последняя покупка старше 90 дней. */
    INACTIVE,

    /** Продаж по клиенту нет. */
    NO_SALES
}
