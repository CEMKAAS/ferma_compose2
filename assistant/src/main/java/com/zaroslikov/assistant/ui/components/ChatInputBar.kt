package com.zaroslikov.assistant.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.assistant.R
import com.zaroslikov.assistant.speech.SpeechState
import com.zaroslikov.assistant.ui.theme.assistant_accent
import com.zaroslikov.assistant.ui.theme.assistant_accent_light
import com.zaroslikov.assistant.ui.theme.assistant_mic_active
import com.zaroslikov.assistant.ui.theme.black_2
import com.zaroslikov.assistant.ui.theme.gray_6
import com.zaroslikov.assistant.ui.theme.gray_7
import com.zaroslikov.assistant.ui.theme.gray_8
import com.zaroslikov.assistant.ui.theme.green_6
import com.zaroslikov.assistant.ui.theme.green_shamrock
import com.zaroslikov.assistant.ui.theme.textBold_14
import com.zaroslikov.assistant.ui.theme.text_14
import com.zaroslikov.assistant.ui.theme.text_16
import com.zaroslikov.assistant.ui.theme.white

/**
 * Нижняя панель ввода: поле сообщения, микрофон и «Отправить».
 * Во время записи над полем показывается «Слушаю…» с промежуточным текстом,
 * а кнопка микрофона становится красной «Стоп» и пульсирует в такт громкости.
 */
@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onMic: () -> Unit,
    speech: SpeechState,
    speechAvailable: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val listening = speech as? SpeechState.Listening
    val canSend = enabled && value.isNotBlank()

    Surface(modifier = modifier.fillMaxWidth(), color = white, shadowElevation = 8.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // При открытой клавиатуре imePadding родителя уже поглотил эту область
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AnimatedVisibility(
                visible = listening != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ListeningIndicator(partial = listening?.partial.orEmpty())
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    enabled = enabled,
                    placeholder = {
                        Text(text = stringResource(R.string.assistant_input_hint), color = gray_7)
                    },
                    textStyle = text_16,
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = green_6,
                        unfocusedBorderColor = gray_8,
                        cursorColor = green_shamrock,
                        focusedTextColor = black_2,
                        unfocusedTextColor = black_2,
                        focusedContainerColor = white,
                        unfocusedContainerColor = white,
                        disabledContainerColor = gray_6
                    )
                )
                if (speechAvailable)
                    MicButton(
                        listening = listening,
                        enabled = enabled,
                        onClick = onMic
                    )
                FilledIconButton(
                    onClick = onSend,
                    enabled = canSend,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = green_6,
                        contentColor = white,
                        disabledContainerColor = gray_6,
                        disabledContentColor = gray_8
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = stringResource(R.string.assistant_send)
                    )
                }
            }
        }
    }
}

/** Кнопка микрофона; во время записи — красная «Стоп» с пульсирующим ореолом. */
@Composable
private fun MicButton(
    listening: SpeechState.Listening?,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val isListening = listening != null
    val transition = rememberInfiniteTransition(label = "mic")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micPulse"
    )
    // Громкость приходит рывками — сглаживаем, чтобы ореол не дёргался
    val rms by animateFloatAsState(
        targetValue = (listening?.rms ?: 0f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 120),
        label = "micRms"
    )

    Box(
        modifier = Modifier
            .padding(bottom = 4.dp)
            .size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isListening)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .graphicsLayer {
                        val scale = pulse + rms * 0.35f
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(CircleShape)
                    .background(assistant_mic_active.copy(alpha = 0.25f))
            )
        FilledIconButton(
            onClick = onClick,
            enabled = enabled || isListening,
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isListening) assistant_mic_active else assistant_accent_light,
                contentColor = if (isListening) white else assistant_accent,
                disabledContainerColor = gray_6,
                disabledContentColor = gray_8
            )
        ) {
            Icon(
                imageVector = if (isListening) Icons.Rounded.Stop else Icons.Rounded.Mic,
                contentDescription = stringResource(
                    if (isListening) R.string.assistant_mic_stop else R.string.assistant_mic
                )
            )
        }
    }
}

/** Строка «Слушаю…» с промежуточным распознанным текстом. */
@Composable
private fun ListeningIndicator(partial: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(assistant_mic_active)
        )
        Text(
            text = stringResource(R.string.assistant_listening),
            style = textBold_14,
            color = black_2
        )
        if (partial.isNotBlank())
            Text(
                text = partial,
                style = text_14.copy(fontStyle = FontStyle.Italic),
                color = gray_7,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
    }
}
