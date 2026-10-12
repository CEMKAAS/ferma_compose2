package com.zaroslikov.crm.domain.model

import java.time.LocalDate

/** Сегодняшняя дата в формате `yyyy-MM-dd`, как даты хранятся в CRM. */
fun crmToday(): String = LocalDate.now().toString()
