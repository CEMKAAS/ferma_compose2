package com.zaroslikov.fermacompose2.ui.project.sections.recycling

import com.zaroslikov.domain.models.table.DomainRecycling
import com.zaroslikov.domain.models.table.DomainRecyclingProduct
import com.zaroslikov.domain.models.table.DomainRecyclingWithProducts
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import java.time.LocalDateTime
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
    /** Сколько минут осталось до готовности; null — без даты или уже завершена. */
    val minutesLeft: Long?,
    /** Доля прошедшего срока переработки по времени, 0..1. */
    val progress: Float
)

/** Момент готовности: дата + время (null — начало дня, как у старых записей); null — без даты. */
fun DomainRecyclingProduct.readyDateTime(): LocalDateTime? = readyYear?.let {
    LocalDateTime.of(it, readyMonth ?: 1, readyDay ?: 1, readyHour ?: 0, readyMinute ?: 0)
}

/** Прогресс считается в минутах от начала переработки (без времени старта — от 00:00 её дня). */
fun DomainRecyclingWithProducts.toRecyclingItem(now: LocalDateTime = LocalDateTime.now()): RecyclingItem {
    val start = LocalDateTime.of(
        recycling.year, recycling.month, recycling.day,
        recycling.startHour ?: 0, recycling.startMinute ?: 0
    )
    return RecyclingItem(
        recycling = recycling,
        products = products.map { product ->
            val ready = product.readyDateTime()
            val total = ready?.let { ChronoUnit.MINUTES.between(start, it) } ?: 0L
            RecyclingProductItem(
                product = product,
                minutesLeft = if (product.isCompleted || ready == null) null
                else ChronoUnit.MINUTES.between(now, ready).coerceAtLeast(0L),
                progress = when {
                    product.isCompleted || ready == null || total <= 0L -> 1f
                    else -> (ChronoUnit.MINUTES.between(start, now).toFloat() / total).coerceIn(0f, 1f)
                }
            )
        },
        ingredients = ingredients
    )
}

fun List<RecyclingItem>.findRecycling(id: Long?): RecyclingItem? =
    id?.let { find { item -> item.recycling.id == it } }

/** Сырьё для снэкбаров: «Молоко 10 л, Соль 1 кг». */
@JvmName("ingredientCountText")
fun List<DomainWriteOffTable>.toIngredientCountText(resourceProvider: ResourceProvider): String =
    joinToString { "${it.title} ${it.count.formatNumber()} ${resourceProvider.getString(it.countSuffix.toResId())}" }

/** Продукция для снэкбаров: «Сыр 2 кг, Творог 1 кг». */
fun List<DomainRecyclingProduct>.toCountText(resourceProvider: ResourceProvider): String =
    joinToString { "${it.title} ${it.count.formatNumber()} ${resourceProvider.getString(it.countSuffix.toResId())}" }
