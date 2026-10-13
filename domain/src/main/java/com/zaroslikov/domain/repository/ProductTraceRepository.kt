package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.dto.trace.DomainProductTrace
import kotlinx.coroutines.flow.Flow

/** Данные для доски продукции: все операции продукта `title` в проекте `idPT`. */
interface ProductTraceRepository {
    fun getProductTrace(idPT: Long, title: String): Flow<DomainProductTrace>
}
