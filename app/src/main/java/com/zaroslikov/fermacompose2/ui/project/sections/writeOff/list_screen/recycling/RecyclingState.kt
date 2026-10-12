package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen.recycling

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainRecycling
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.violet_1
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/** «Тип списания» в форме: в БД первые два — `status`, переработка — отдельная запись recycling_table. */
enum class WriteOffType(
    @DrawableRes val iconRes: Int,
    @StringRes val titleRes: Int,
    val color: Color
) {
    OWN_NEEDS(R.drawable.baseline_cottage_24, R.string.ration_button_own_needs, violet_1),
    DISPOSAL(R.drawable.baseline_delete_24, R.string.ration_button_disposal, error_base),
    RECYCLING(R.drawable.baseline_autorenew_24, R.string.write_off_type_recycling, green_shamrock)
}

/** Одна позиция сырья; [key] — стабильный ключ строки формы (позиции можно удалять). */
data class RecyclingIngredientForm(
    val key: Long,
    val title: String = "",
    val countSuffix: Suffix = Suffix.PIECES,
    val productOrigin: ProductOrigin? = null,
    val count: String = "",
    val warehouseList: List<DomainCountSuffix> = emptyList()
)

data class RecyclingForm(
    val ingredients: List<RecyclingIngredientForm> = listOf(RecyclingIngredientForm(key = 0)),
    val nextKey: Long = 1,
    val title: String = "",
    val count: String = "",
    val countSuffix: Suffix = Suffix.PIECES,
    val isDelayed: Boolean = false,
    val readyDate: String = dateToday()
)

/** Переработка со своим сырьём — для блока «В переработке» и шторки деталей. */
data class RecyclingItem(
    val recycling: DomainRecycling,
    val ingredients: List<DomainWriteOffTable>,
    /** Сколько дней осталось до готовности; null — без даты или уже завершена. */
    val daysLeft: Long?,
    /** Доля прошедшего срока переработки, 0..1. */
    val progress: Float
)

private val formFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

/** Дата формы (`dd.MM.yyyy`). */
fun String.toFormLocalDate(): LocalDate = LocalDate.parse(this, formFormatter)

fun LocalDate.toFormString(): String = format(formFormatter)

fun DomainRecycling.toRecyclingItem(
    ingredients: List<DomainWriteOffTable>,
    today: LocalDate = LocalDate.now()
): RecyclingItem {
    val start = LocalDate.of(year, month, day)
    val ready = readyYear?.let { LocalDate.of(it, readyMonth ?: 1, readyDay ?: 1) }
    val total = ready?.let { ChronoUnit.DAYS.between(start, it) } ?: 0L
    val progress = when {
        isCompleted || ready == null || total <= 0L -> 1f
        else -> (ChronoUnit.DAYS.between(start, today).toFloat() / total).coerceIn(0f, 1f)
    }
    return RecyclingItem(
        recycling = this,
        ingredients = ingredients,
        daysLeft = if (isCompleted || ready == null) null
        else ChronoUnit.DAYS.between(today, ready).coerceAtLeast(0L),
        progress = progress
    )
}
