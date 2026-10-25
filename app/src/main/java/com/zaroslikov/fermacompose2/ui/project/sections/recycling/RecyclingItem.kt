package com.zaroslikov.fermacompose2.ui.project.sections.recycling

import com.zaroslikov.domain.models.table.DomainRecycling
import com.zaroslikov.domain.models.table.DomainRecyclingProduct
import com.zaroslikov.domain.models.table.DomainRecyclingWithProducts
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Партия переработки с продукцией и сырьём — для блока «В переработке» и шторки деталей. */
data class RecyclingItem(
    val recycling: DomainRecycling,
    val products: List<RecyclingProductItem>,
    val ingredients: List<DomainWriteOffTable>
) {
    /** Названия всей продукции партии — подпись строк сырья в списаниях. */
    val title: String get() = products.joinToString { it.product.title }
}

data class RecyclingProductItem(
    val product: DomainRecyclingProduct,
    /** Сколько дней осталось до готовности; null — без даты или уже завершена. */
    val daysLeft: Long?,
    /** Доля прошедшего срока переработки, 0..1. */
    val progress: Float
)

fun DomainRecyclingWithProducts.toRecyclingItem(today: LocalDate = LocalDate.now()): RecyclingItem {
    val start = LocalDate.of(recycling.year, recycling.month, recycling.day)
    return RecyclingItem(
        recycling = recycling,
        products = products.map { product ->
            val ready = product.readyYear?.let {
                LocalDate.of(it, product.readyMonth ?: 1, product.readyDay ?: 1)
            }
            val total = ready?.let { ChronoUnit.DAYS.between(start, it) } ?: 0L
            RecyclingProductItem(
                product = product,
                daysLeft = if (product.isCompleted || ready == null) null
                else ChronoUnit.DAYS.between(today, ready).coerceAtLeast(0L),
                progress = when {
                    product.isCompleted || ready == null || total <= 0L -> 1f
                    else -> (ChronoUnit.DAYS.between(start, today).toFloat() / total).coerceIn(0f, 1f)
                }
            )
        },
        ingredients = ingredients
    )
}

fun List<RecyclingItem>.findRecycling(id: Long?): RecyclingItem? =
    id?.let { find { item -> item.recycling.id == it } }

/** Продукция для снэкбаров: «Сыр 2 кг, Творог 1 кг». */
fun List<DomainRecyclingProduct>.toCountText(resourceProvider: ResourceProvider): String =
    joinToString { "${it.title} ${it.count.formatNumber()} ${resourceProvider.getString(it.countSuffix.toResId())}" }
