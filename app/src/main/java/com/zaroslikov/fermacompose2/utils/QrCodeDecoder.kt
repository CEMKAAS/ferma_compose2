package com.zaroslikov.fermacompose2.utils

import android.net.Uri
import android.util.Base64
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrPayload
import kotlinx.serialization.json.Json

object QrCodeDecoder {

    fun decodeForUri(uri: Uri): QrPayload? {
        val encoded = uri.getQueryParameter("data") ?: return null
        return decodeForBase64(encoded)
    }

    fun decodeForBase64(string: String): QrPayload {
        val json = String(
            Base64.decode(string, Base64.URL_SAFE),
            Charsets.UTF_8
        )
        return Json.decodeFromString<QrPayload>(json)
    }
}