package com.zaroslikov.crm.util

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.zaroslikov.crm.domain.model.CrmContactType
import java.net.URI

/** Чем закончилось открытие контакта — экран выбирает сообщение. */
enum class CrmContactOpenResult {
    /** Открыто внешнее приложение. */
    OPENED,

    /** Адрес собран, но открыть нечем — значение скопировано (`crm_msg_no_app`). */
    NO_APP,

    /** Открывать нечего (тип «Другое», чужая ссылка) — значение скопировано (`crm_msg_copied`). */
    COPIED
}

/** Хосты, ссылки на которые открываются для мессенджеров; остальные ссылки только копируются. */
private val ALLOWED_HOSTS = mapOf(
    CrmContactType.TELEGRAM to setOf("t.me", "telegram.me"),
    CrmContactType.WHATSAPP to setOf("wa.me", "api.whatsapp.com"),
    CrmContactType.VK to setOf("vk.com", "m.vk.com", "vk.ru")
)

// Как android.net.Uri.encode: без кодирования остаются буквы, цифры и `_-!.~'()*`.
// Своя реализация, чтобы функция оставалась чистой и проверялась JVM-тестами.
private const val UNRESERVED = "_-!.~'()*"

private fun encodeUriPart(text: String, allow: String = ""): String = buildString {
    text.toByteArray(Charsets.UTF_8).forEach { byte ->
        val c = (byte.toInt() and 0xFF).toChar()
        if (c in 'a'..'z' || c in 'A'..'Z' || c in '0'..'9' || c in UNRESERVED || c in allow) append(c)
        else append('%').append("%02X".format(byte.toInt() and 0xFF))
    }
}

/**
 * Ссылка, введённая пользователем, открывается только на хост из белого списка и только
 * по https; иначе `null` — значение копируется, а не уходит в браузер.
 */
private fun allowedLink(type: CrmContactType, text: String): String? {
    val uri = try {
        URI(text)
    } catch (e: Exception) {
        return null
    }
    val host = uri.host?.lowercase()?.removePrefix("www.") ?: return null
    if (host !in ALLOWED_HOSTS[type].orEmpty()) return null
    return "https://" + host + uri.rawPath.orEmpty() + (uri.rawQuery?.let { "?$it" } ?: "")
}

private fun String.isHttpLink(): Boolean =
    startsWith("http://", ignoreCase = true) || startsWith("https://", ignoreCase = true)

/**
 * Адрес, по которому открывается контакт, или `null`, если открыть нечем
 * (тип «Другое», пустое значение, ссылка на чужой сайт) — тогда значение только копируется.
 */
fun crmContactUri(type: CrmContactType, value: String): String? {
    val text = value.trim()
    if (text.isEmpty()) return null
    val digits = text.filter { it.isDigit() }
    return when (type) {
        CrmContactType.PHONE -> "tel:" + text.filter { it.isDigit() || it == '+' }
        CrmContactType.EMAIL -> "mailto:" + encodeUriPart(text, allow = "@")
        CrmContactType.ADDRESS -> "geo:0,0?q=" + encodeUriPart(text)
        CrmContactType.TELEGRAM -> when {
            text.isHttpLink() -> allowedLink(type, text)
            text.startsWith("+") && digits.isNotEmpty() -> "https://t.me/+$digits"
            else -> "https://t.me/" + encodeUriPart(text.removePrefix("@"))
        }
        CrmContactType.WHATSAPP -> if (text.isHttpLink()) allowedLink(type, text)
        else digits.takeIf { it.isNotEmpty() }?.let { "https://wa.me/$it" }
        CrmContactType.VK -> when {
            text.isHttpLink() -> allowedLink(type, text)
            else -> "https://vk.com/" + encodeUriPart(text.removePrefix("@").removePrefix("vk.com/"))
        }
        CrmContactType.OTHER -> null
    }
}

private fun CrmContactType.intentAction(): String = when (this) {
    CrmContactType.PHONE -> Intent.ACTION_DIAL
    CrmContactType.EMAIL -> Intent.ACTION_SENDTO
    else -> Intent.ACTION_VIEW
}

/**
 * Открывает контакт во внешнем приложении (звонилка, почта, карты, мессенджер).
 * Если открыть нечем — копирует значение в буфер.
 */
fun Context.openCrmContactResult(type: CrmContactType, value: String): CrmContactOpenResult {
    val uri = crmContactUri(type, value)
    if (uri == null) {
        copyCrmText(value)
        return CrmContactOpenResult.COPIED
    }
    return try {
        startActivity(
            Intent(type.intentAction(), Uri.parse(uri)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        CrmContactOpenResult.OPENED
    } catch (e: ActivityNotFoundException) {
        copyCrmText(value)
        CrmContactOpenResult.NO_APP
    }
}

/**
 * @return `true`, если приложение открыто; `false`, если значение скопировано.
 * Чтобы различать «нет приложения» и «только копирование», используй [openCrmContactResult].
 */
fun Context.openCrmContact(type: CrmContactType, value: String): Boolean =
    openCrmContactResult(type, value) == CrmContactOpenResult.OPENED

fun Context.copyCrmText(value: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("crm", value))
}
