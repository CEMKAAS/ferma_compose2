package com.zaroslikov.fermacompose2.ui.project.finance.traceBoard

import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainWriteOffTable

/** Категория узла/нити доски — определяет цвет и иконку. */
enum class TraceCategory {
    PRODUCT,
    PRODUCTION,
    SALE,
    OWN_NEEDS,
    DISPOSAL,
    RECYCLING,
    RECYCLING_RESULT,
    INGREDIENT,

    /** Остаток на складе — только для сводки, узлов этой категории нет. */
    STOCK
}

/**
 * Вид узла — определяет колонку раскладки:
 * INGREDIENT (−2) → SOURCE (−1) → PRODUCT (0) → HUB (1) → OPERATION (2) → RESULT (3).
 */
enum class TraceNodeKind {
    PRODUCT,
    SOURCE,
    HUB,

    /** Категория операций внутри хаба («Детям»); есть, только если у операций хаба указаны категории. */
    CATEGORY,
    OPERATION,
    RESULT,
    INGREDIENT
}

/** Вид животного-производителя — определяет цвет значка, как в списке животных. */
enum class TraceAnimalKind {
    MALE,
    FEMALE,

    /** Обезличенная группа. */
    GROUP,

    /** Входит в сборную группу. */
    HERD,

    /** Животное не указано, удалено или пол неизвестен. */
    UNKNOWN
}

/**
 * Исходная запись операции. Если она есть, по нажатию открывается та же карточка деталей,
 * что и в разделах продаж и списаний, но без кнопок изменения и удаления.
 */
sealed interface TraceRecord {
    data class Sale(val sale: DomainSaleTable) : TraceRecord
    data class WriteOff(val writeOff: DomainWriteOffTable) : TraceRecord
}

/** Строка в нижнем листе с деталями узла. */
data class TraceDetail(val label: String, val value: String)

/**
 * Узел доски.
 *
 * @property value основная величина («12 кг», «3 500 ₽ · 12 кг»)
 * @property caption подпись (дата, «N операций»)
 * @property childCount число дочерних операций у хаба
 * @property targetProduct продукт, доску которого можно открыть по тапу (результат/ингредиент переработки)
 */
data class TraceNode(
    val id: String,
    val kind: TraceNodeKind,
    val category: TraceCategory,
    val title: String,
    val subtitle: String? = null,
    val value: String,
    val caption: String? = null,
    val icon: AppIcon? = null,
    val imagePath: String? = null,
    val childCount: Int = 0,
    val details: List<TraceDetail> = emptyList(),
    val targetProduct: Triple<Long, String, Suffix>? = null,
    val record: TraceRecord? = null,
    /** Только у источников-животных: цвет значка. */
    val animalKind: TraceAnimalKind? = null
)

/** Нить между узлами: от правого края [from] к левому краю [to]. */
data class TraceEdge(val from: String, val to: String, val category: TraceCategory)

/** Строка сводки над доской: категория и итог в базовой единице («12 кг»). */
data class TraceSummaryItem(val category: TraceCategory, val value: String)

/**
 * Граф доски.
 *
 * @property nodes все узлы, кроме [product]
 * @property children дочерние узлы: хаб → операции; операция-переработка → результат;
 *   источник-переработка → ингредиенты. Нужно раскладке для группировки и сворачивания.
 * @property summary итоги для панели сверху: произведено, продано, списано, переработано, свои нужды
 */
data class TraceGraph(
    val product: TraceNode,
    val nodes: List<TraceNode>,
    val edges: List<TraceEdge>,
    val children: Map<String, List<String>>,
    val summary: List<TraceSummaryItem> = emptyList()
) {
    val nodesById: Map<String, TraceNode> by lazy {
        (nodes + product).associateBy { it.id }
    }
}
