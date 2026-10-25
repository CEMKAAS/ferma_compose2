package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen.recycling

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.violet_1
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

/** Когда товар будет готов: сразу, через N дней после переработки или к дате. */
enum class RecyclingReadyMode { NONE, DAYS, DATE }

/** Одна позиция готовой продукции; [key] — из того же счётчика, что и у сырья. */
data class RecyclingProductForm(
    val key: Long,
    val title: String = "",
    val count: String = "",
    val countSuffix: Suffix = Suffix.PIECES,
    val readyMode: RecyclingReadyMode = RecyclingReadyMode.NONE,
    val days: String = "",
    val readyDate: String = dateToday()
)

data class RecyclingForm(
    val ingredients: List<RecyclingIngredientForm> = listOf(RecyclingIngredientForm(key = 0)),
    val products: List<RecyclingProductForm> = listOf(RecyclingProductForm(key = 1)),
    val nextKey: Long = 2
)

private val formFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

/** Дата формы (`dd.MM.yyyy`). */
fun String.toFormLocalDate(): LocalDate = LocalDate.parse(this, formFormatter)

fun LocalDate.toFormString(): String = format(formFormatter)

/** Срок в днях не задан целым числом больше нуля. */
fun RecyclingProductForm.isErrorDays(): Boolean =
    readyMode == RecyclingReadyMode.DAYS && (days.toIntOrNull() ?: 0) <= 0

/** Дата готовности раньше даты переработки [start] (`dd.MM.yyyy`). */
fun RecyclingProductForm.isErrorReadyDate(start: String): Boolean =
    readyMode == RecyclingReadyMode.DATE && readyDate.toFormLocalDate().isBefore(start.toFormLocalDate())

/** Дата готовности товара; null — поступает на склад сразу. */
fun RecyclingProductForm.readyLocalDate(start: String): LocalDate? = when (readyMode) {
    RecyclingReadyMode.NONE -> null
    RecyclingReadyMode.DAYS -> days.toIntOrNull()?.let { start.toFormLocalDate().plusDays(it.toLong()) }
    RecyclingReadyMode.DATE -> readyDate.toFormLocalDate()
}

fun RecyclingProductForm.isValid(start: String): Boolean =
    title.isNotBlank() && !title.isSlash() && count.toConvertZeroDouble() > 0.0 &&
            !isErrorDays() && !isErrorReadyDate(start)

/** Названия сырья (trim, без регистра) — их не предлагаем в готовой продукции. */
fun RecyclingForm.rawTitleKeys(): Set<String> =
    ingredients.map { it.title.trim().lowercase() }.filter { it.isNotEmpty() }.toSet()
