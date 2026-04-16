package com.zaroslikov.fermacompose2.ui.elements


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp


//Text
/*
val textBold_28: TextStyle
    @Composable get() = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
    )
val text_36: TextStyle
    @Composable get() = TextStyle(
        fontSize = 36.sp,
    )
val text_30: TextStyle
    @Composable get() = TextStyle(
        fontSize = 30.sp,
    )

val text_24: TextStyle
    @Composable get() = TextStyle(
        fontSize = 24.sp,
    )

val textBold_20: TextStyle
    @Composable get() = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
    )


val textBold_18: TextStyle
    @Composable get() = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
    )

val textBold_16: TextStyle
    @Composable get() = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    )
val textBold_14: TextStyle
    @Composable get() = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )


val text_20_center: TextStyle
    @Composable get() = TextStyle(
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
    )

val text_20_justify: TextStyle
    @Composable get() = TextStyle(
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
    )

val text_20: TextStyle
    @Composable get() = TextStyle(
        fontSize = 20.sp,
    )

val text_18: TextStyle
    @Composable get() = TextStyle(
        fontSize = 18.sp
    )

val text_16: TextStyle
    @Composable get() = TextStyle(
        fontSize = 16.sp
    )

val text_14: TextStyle
    @Composable get() = TextStyle(
        fontSize = 14.sp
    )

val text_12: TextStyle
    @Composable get() = TextStyle(
        fontSize = 12.sp
    )

val text_10: TextStyle
    @Composable get() = TextStyle(
        fontSize = 10.sp
    )
*/

// LARGE

val text_36: TextStyle
    @Composable get() = MaterialTheme.typography.displaySmall.copy(
        fontSize = 36.sp
    )

val text_30: TextStyle
    @Composable get() = MaterialTheme.typography.headlineLarge.copy(
        fontSize = 30.sp
    )

val text_24: TextStyle
    @Composable get() = MaterialTheme.typography.headlineMedium.copy(
        fontSize = 24.sp
    )

val textBold_28: TextStyle
    @Composable get() = MaterialTheme.typography.headlineLarge.copy(
        fontSize = 28.sp,
        fontWeight = FontWeight.SemiBold
    )

// MEDIUM

val text_20: TextStyle
    @Composable get() = MaterialTheme.typography.titleMedium.copy(
        fontSize = 20.sp
    )

val textBold_20: TextStyle
    @Composable get() = MaterialTheme.typography.titleMedium.copy(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    )

val text_18: TextStyle
    @Composable get() = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 18.sp
    )

val textBold_18: TextStyle
    @Composable get() = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    )

// BASE

val text_16: TextStyle
    @Composable get() = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 16.sp
    )

val textBold_16: TextStyle
    @Composable get() = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    )

val text_14: TextStyle
    @Composable get() = MaterialTheme.typography.bodySmall.copy(
        fontSize = 14.sp
    )

// SMALL

val text_12: TextStyle
    @Composable get() = MaterialTheme.typography.labelMedium.copy(
        fontSize = 12.sp
    )

val text_10: TextStyle
    @Composable get() = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp
    )

// ALIGN

val text_20_center: TextStyle
    @Composable get() = MaterialTheme.typography.titleMedium.copy(
        fontSize = 20.sp,
        textAlign = TextAlign.Center
    )

val text_20_justify: TextStyle
    @Composable get() = MaterialTheme.typography.titleMedium.copy(
        fontSize = 20.sp,
        textAlign = TextAlign.Justify
    )