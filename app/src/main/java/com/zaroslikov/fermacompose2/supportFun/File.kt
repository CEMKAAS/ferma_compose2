package com.zaroslikov.fermacompose2.supportFun

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.zaroslikov.fermacompose2.BuildConfig
import java.io.File

fun file(context: Context, text: String) {
    val fileName = "ferma_data.json"
    val file = File(
        context.cacheDir,
        fileName
    )
    file.writeText(text)

    val uri = FileProvider.getUriForFile(
        context,
        BuildConfig.APPLICATION_ID + ".provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_STREAM, uri)
        .setDataAndType(uri, "application/json")
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    context.startActivity(intent)
}

fun saveFileLauncher(): Intent {
    return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, "ferma_data.txt")
    }
}