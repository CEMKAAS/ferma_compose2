package com.zaroslikov.fermacompose2.utils

import android.util.Base64
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrPayload
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object QrCodeEncoder {

    fun encode(domainTemplateTable: QrPayload): String {
        val json = Json.encodeToString(domainTemplateTable)
        val base64 = Base64.encodeToString(
            json.toByteArray(),
            Base64.URL_SAFE or Base64.NO_WRAP
        )
        return "myferma://template?data=$base64"
    }
}