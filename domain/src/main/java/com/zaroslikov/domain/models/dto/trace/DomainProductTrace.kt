package com.zaroslikov.domain.models.dto.trace

import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.table.DomainRecycling
import com.zaroslikov.domain.models.table.DomainWriteOffTable

/**
 * Все операции одного продукта (`idPT` + `title`) для доски продукции.
 *
 * @property production строки производства с животным-производителем
 * @property sales продажи именно продукта (не животных)
 * @property writeOffs все списания продукта, включая сырьё переработки (`recyclingId != null`)
 * @property recyclingUsingProduct партии переработки, в которых наш продукт был сырьём
 * @property recyclingProducingProduct партии переработки, результат которых — наш продукт
 * @property producingIngredients сырьё партий из [recyclingProducingProduct]
 */
data class DomainProductTrace(
    val production: List<DomainTraceProduction> = emptyList(),
    val sales: List<DomainSaleTable> = emptyList(),
    val writeOffs: List<DomainWriteOffTable> = emptyList(),
    val recyclingUsingProduct: List<DomainRecycling> = emptyList(),
    val recyclingProducingProduct: List<DomainRecycling> = emptyList(),
    val producingIngredients: List<DomainWriteOffTable> = emptyList()
)
