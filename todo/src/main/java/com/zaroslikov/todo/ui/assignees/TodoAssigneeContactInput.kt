package com.zaroslikov.todo.ui.assignees

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

private const val LOCAL_PHONE_DIGITS = 10

internal fun sanitizeTodoAssigneePhone(value: String): String {
    val hasPrefix = value.trimStart().startsWith('+')
    val digits = buildString {
        value.forEach { character -> character.digitToIntOrNull()?.let { append(it) } }
    }
    val prefix = if (hasPrefix) "+" else ""
    return prefix + digits
}

internal fun sanitizeTodoAssigneeEmail(value: String): String = value.filterNot(Char::isWhitespace)

internal fun updateTodoAssigneePhone(current: String, updated: String): String {
    if (updated == current) return current
    val candidate = sanitizeTodoAssigneePhone(updated)
    if (candidate.length <= maximumPhoneLength(candidate)) return candidate

    // В старых номерах можно удалять лишние цифры за несколько правок.
    val previous = sanitizeTodoAssigneePhone(current)
    return if (previous.length > maximumPhoneLength(previous) && candidate.length < previous.length) {
        candidate
    } else {
        current
    }
}

internal fun isValidTodoAssigneePhone(value: String): Boolean {
    if (value.isEmpty()) return true
    val maximumLength = maximumPhoneLength(value)
    if (value.length > maximumLength || value != sanitizeTodoAssigneePhone(value)) return false
    return when {
        value.startsWith("+7") -> value.length == maximumLength
        value.startsWith('+') -> value.length >= 8 && value[1] in '1'..'9'
        else -> value.length >= LOCAL_PHONE_DIGITS
    }
}

private fun maximumPhoneLength(value: String): Int = when {
    value.startsWith("+7") -> LOCAL_PHONE_DIGITS + 2
    value.startsWith('+') -> 16
    canHaveRussianTrunkPrefix(value) -> LOCAL_PHONE_DIGITS + 1
    else -> LOCAL_PHONE_DIGITS
}

private val emailLocalPart = Regex("[\\p{L}\\p{M}\\p{N}!#\$%&'*+/=?^_\u0060{|}~.-]+")
private val emailDomainLabel = Regex("[\\p{L}\\p{N}](?:[\\p{L}\\p{M}\\p{N}-]{0,61}[\\p{L}\\p{M}\\p{N}])?")

internal fun isValidTodoAssigneeEmail(value: String): Boolean {
    if (value.isEmpty()) return true
    val local = value.substringBefore('@')
    val domain = value.substringAfter('@', "")
    if (!emailLocalPart.matches(local) || local.startsWith('.') || local.endsWith('.') || ".." in local) return false
    val labels = domain.split('.')
    return labels.size > 1 && labels.last().any(Char::isLetter) && labels.all(emailDomainLabel::matches)
}

private fun canHaveRussianTrunkPrefix(value: String): Boolean = value.startsWith('7') || value.startsWith('8')

internal fun formatTodoAssigneePhone(value: String): String =
    if (isValidTodoAssigneePhone(value)) TodoAssigneePhoneTransformation.filter(AnnotatedString(value)).text.text
    else value

internal object TodoAssigneePhoneTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val value = text.text
        if (value.isEmpty() || value.length > maximumPhoneLength(value) || value != sanitizeTodoAssigneePhone(value)) {
            return TransformedText(text, OffsetMapping.Identity)
        }
        val prefixLength = when {
            value.startsWith("+7") -> 2
            value.startsWith('+') -> return TransformedText(text, OffsetMapping.Identity)
            value.length == LOCAL_PHONE_DIGITS + 1 -> 1
            else -> 0
        }
        val offsets = IntArray(value.length + 1)
        val output = StringBuilder("+7")
        if (prefixLength == 2) offsets[1] = 1
        for (index in prefixLength until value.length) {
            output.append(when (index - prefixLength) {
                0 -> " ("
                3 -> ") "
                6, 8 -> "-"
                else -> ""
            })
            offsets[index] = output.length
            output.append(value[index])
        }
        offsets[value.length] = output.length
        return TransformedText(AnnotatedString(output.toString()), object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = offsets[offset]

            override fun transformedToOriginal(offset: Int): Int =
                offsets.indexOfFirst { it >= offset }
        })
    }
}
