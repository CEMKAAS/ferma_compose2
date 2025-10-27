package com.zaroslikov.fermacompose2.ui.elements


import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black
import com.zaroslikov.fermacompose2.gray_4
import com.zaroslikov.fermacompose2.white

@Composable
fun ButtonRefresh(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.toButton()
    ) {
        Icon(
            modifier = Modifier.padding(end = 3.dp),
            painter = painterResource(R.drawable.baseline_create_24),
            contentDescription = stringResource(R.string.button_refresh)
        )
        Text(text = stringResource(R.string.button_refresh))
    }
}

@Composable
fun ButtonArchive(
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick, modifier = Modifier.toButton()
    ) {
        Icon(
            modifier = Modifier.padding(end = 3.dp),
            painter = painterResource(R.drawable.baseline_archive_24),
            contentDescription = stringResource(R.string.button_archive)
        )
        Text(text = stringResource(R.string.button_archive))
    }
}


@Composable
fun ButtonDelete(
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.toButton(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.error)
    ) {
        Icon(
            modifier = Modifier.padding(end = 3.dp),
            painter = painterResource(R.drawable.baseline_delete_24),
            contentDescription = stringResource(R.string.button_delete)
        )
        Text(text = stringResource(R.string.button_delete))
    }
}

@Composable
fun ButtonStandart(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @StringRes intRes: Int
) {
    Button(
        onClick = onClick, modifier = modifier.toButton()
    ) {
        Text(text = stringResource(intRes))
    }
}

@Composable
fun ButtonCustom(
    modifier: Modifier = Modifier,
    @StringRes intRes: Int,
    drawableRes: Int? = null,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick, modifier = modifier.toButton()
    ) {
        drawableRes?.let {
            Icon(
                modifier = Modifier.padding(end = 3.dp),
                painter = painterResource(it),
                contentDescription = stringResource(intRes)
            )
        }
        Text(text = stringResource(intRes))
    }
}

@Composable
fun GradientMaterialButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF009966), Color(0xFF00A63E)),
        start = Offset(0f, 0f),
        end = Offset(300f, 0f)
    )
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(brush = gradient)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            color = Color.White,
            style = text_14,
        )
    }
}

@Composable
fun GradientButton(
    modifier: Modifier = Modifier,
    text: String,
    enable: Boolean,
    onClick: () -> Unit,
) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF009966), Color(0xFF00A63E)),
        start = Offset(0f, 0f),
        end = Offset(300f, 0f)
    )
    val alpha = if (enable) 0.5f else 1f

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(brush = gradient, alpha = alpha)
            .then(
                if (enable) Modifier else Modifier.clickable(onClick = onClick)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = text,
            color = Color.White,
            style = text_14
        )
    }
}

@Composable
fun CloseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, gray_4, RoundedCornerShape(14.dp))
            .background(white)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = text,
            color = black,
            style = text_14
        )
    }
}

