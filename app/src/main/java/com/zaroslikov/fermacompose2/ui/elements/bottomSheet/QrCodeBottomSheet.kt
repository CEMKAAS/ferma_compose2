package com.zaroslikov.fermacompose2.ui.elements.bottomSheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CloseButton
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_16

@Composable
fun QrCodeBottomSheet(
    colors: List<Color>,
    onDismissRequest: () -> Unit,
) {
    BaseBottomSheet(
        title = stringResource(R.string.pattern_bottom_sheet_title),
        onDismissRequest = onDismissRequest,
        contentBottom = {
            ButtonPanel(
                colors = colors,
                onDismissRequest = onDismissRequest,
                onCreateQrCodeClick = {})
        }
    ) {
        BorderCard(
            borderColor = green_8
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_log),
                        contentDescription = null,
                        modifier = Modifier
                            .shadow(
                                elevation = 5.dp,
                                shape = RoundedCornerShape(10.dp),
                                clip = false
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .size(48.dp)
                            .border(
                                1.dp,
                                color = ghostly_white,
                                shape = RoundedCornerShape(10.dp)
                            )
                    )
                    Text(
                        text = stringResource(R.string.app_name),
                        style = text_12,
                        lineHeight = 16.sp,
                        color = black_2,
                        fontWeight = FontWeight.SemiBold
                    )
                    HorizontalDivider(color = green_8)
                }
                Image(
                    painter = painterResource(R.drawable.qrcodesvg),
                    contentDescription = null,
                    modifier = Modifier.size(190.dp)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(color = green_8)
                    Text(
                        "Продажа на рынке",
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp,
                        style = text_16
                    )
                }
            }
        }
    }
}

@Composable
private fun ButtonPanel(
    colors: List<Color>,
    onDismissRequest: () -> Unit,
    onCreateQrCodeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CloseButton(
            text = R.string.button_cancel,
            onClick = onDismissRequest,
            modifier = Modifier.weight(1f)
        )
        GradientButton(
            text = stringResource(R.string.button_create_png),
            onClick = onCreateQrCodeClick,
            prefixIconRes = R.drawable.outline_download_24,
            modifier = Modifier.weight(1f),
            colors = colors
        )
    }
}