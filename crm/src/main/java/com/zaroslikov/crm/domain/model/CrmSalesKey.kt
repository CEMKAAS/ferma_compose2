package com.zaroslikov.crm.domain.model

/**
 * Ключ клиента в продажах (`sales_key`). Нормализуется так же, как SQLite `TRIM(buyer)`
 * в запросах статистики: срезаются только пробелы, табуляция и неразрывный пробел остаются —
 * иначе ключ клиента разойдётся с покупателем из продаж.
 */
fun crmSalesKey(raw: String): String = raw.trim(' ')
