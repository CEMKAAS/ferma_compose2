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
import com.zaroslikov.fermacompose2.supportFun.currentTime
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.violet_1
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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

/** Когда товар будет готов: сразу, через срок (часы и минуты) от начала переработки или к дате и времени. */
enum class RecyclingReadyMode { NONE, HOURS, DATE_TIME }

/**
 * Одна позиция готовой продукции; [key] — из того же счётчика, что и у сырья.
 * [id] — товар партии при правке переработки; null — новый.
 */
data class RecyclingProductForm(
    val key: Long,
    val id: Long? = null,
    val title: String = "",
    val count: String = "",
    val countSuffix: Suffix = Suffix.PIECES,
    val readyMode: RecyclingReadyMode = RecyclingReadyMode.NONE,
    val hours: String = "",
    val minutes: String = "",
    val readyDate: String = dateToday(),
    /** Время готовности `HH:mm` для режима [RecyclingReadyMode.DATE_TIME]. */
    val readyTime: String = currentTime()
)

data class RecyclingForm(
    val ingredients: List<RecyclingIngredientForm> = listOf(RecyclingIngredientForm(key = 0)),
    val products: List<RecyclingProductForm> = listOf(RecyclingProductForm(key = 1)),
    val nextKey: Long = 2,
    /** Время постановки на переработку (`HH:mm`); задаётся, только если хоть один товар «готов позже». */
    val startTime: String = currentTime()
)

/** Хотя бы у одного товара включено «Будет готово позже» — тогда в форме есть поле времени. */
fun RecyclingForm.isDelayed(): Boolean = products.any { it.readyMode != RecyclingReadyMode.NONE }

/** Начало переработки: время из формы, если товары зреют, иначе — момент сохранения. */
fun RecyclingForm.start(date: String): LocalDateTime =
    if (isDelayed()) recyclingStart(date, LocalTime.parse(startTime)) else recyclingStart(date)

private val formFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

/** Дата формы (`dd.MM.yyyy`). */
fun String.toFormLocalDate(): LocalDate = LocalDate.parse(this, formFormatter)

fun LocalDate.toFormString(): String = format(formFormatter)

/**
 * Начало переработки: её дата [date] (`dd.MM.yyyy`) и время суток [time] — по умолчанию текущее,
 * т. е. момент сохранения. От него отсчитываются «Через N часов».
 */
fun recyclingStart(date: String, time: LocalTime = LocalTime.now()): LocalDateTime =
    date.toFormLocalDate().atTime(time.truncatedTo(ChronoUnit.MINUTES))

/** Срок в минутах из часов и минут формы; null — срок нулевой или минут больше 59. */
private fun RecyclingProductForm.durationMinutes(): Long? {
    val h = hours.toIntOrNull() ?: 0
    val m = minutes.toIntOrNull() ?: 0
    return if (m > 59) null else (h * 60L + m).takeIf { it > 0 }
}

/** Срок «через время» не задан: ноль, пустые поля или минут больше 59. */
fun RecyclingProductForm.isErrorHours(): Boolean =
    readyMode == RecyclingReadyMode.HOURS && durationMinutes() == null

/** Пользователь уже начал вводить срок — ошибку показываем только тогда. */
fun RecyclingProductForm.isDurationTouched(): Boolean = hours.isNotEmpty() || minutes.isNotEmpty()

/** Дата и время готовности не позже начала переработки [start]. */
fun RecyclingProductForm.isErrorReadyDateTime(start: LocalDateTime): Boolean =
    readyMode == RecyclingReadyMode.DATE_TIME && readyDateTime(start)?.isAfter(start) != true

/** Момент готовности товара; null — поступает на склад сразу (или срок ещё не введён). */
fun RecyclingProductForm.readyDateTime(start: LocalDateTime): LocalDateTime? = when (readyMode) {
    RecyclingReadyMode.NONE -> null
    RecyclingReadyMode.HOURS -> durationMinutes()?.let { start.plusMinutes(it) }
    RecyclingReadyMode.DATE_TIME -> readyDate.toFormLocalDate().atTime(LocalTime.parse(readyTime))
}

/** Название товара совпадает с сырьём ([rawKeys] из [rawTitleKeys]): из молока не получается «Молоко». */
fun RecyclingProductForm.isRawTitle(rawKeys: Set<String>): Boolean =
    title.trim().lowercase() in rawKeys

fun RecyclingProductForm.isValid(start: LocalDateTime, rawKeys: Set<String>): Boolean =
    title.isNotBlank() && !title.isSlash() && !isRawTitle(rawKeys) && count.toConvertZeroDouble() > 0.0 &&
            !isErrorHours() && !isErrorReadyDateTime(start)

/** Названия сырья (trim, без регистра) — их не предлагаем в готовой продукции. */
fun RecyclingForm.rawTitleKeys(): Set<String> =
    ingredients.map { it.title.trim().lowercase() }.filter { it.isNotEmpty() }.toSet()
