package com.zaroslikov.fermacompose2.ui.composeElement


import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


//Text
val textBold_18: TextStyle
    @Composable get() = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    )


val textBold_16: TextStyle
    @Composable get() = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    )