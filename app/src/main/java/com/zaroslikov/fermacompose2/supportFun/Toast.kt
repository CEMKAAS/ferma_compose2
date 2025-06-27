package com.zaroslikov.fermacompose2.supportFun

import android.content.Context
import android.widget.Toast


fun toastShort(
    context: Context,
    text: String
) {
    Toast.makeText(
        context, text,
        Toast.LENGTH_SHORT
    ).show()
}
