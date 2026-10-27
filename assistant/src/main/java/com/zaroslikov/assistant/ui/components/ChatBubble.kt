package com.zaroslikov.assistant.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.assistant.R
import com.zaroslikov.assistant.model.ActionCard
import com.zaroslikov.assistant.model.ActionStatus
import com.zaroslikov.assistant.model.AnalysisCard
import com.zaroslikov.assistant.model.CardRow
import com.zaroslikov.assistant.model.ChatAuthor
import com.zaroslikov.assistant.model.ChatMessage
import com.zaroslikov.assistant.ui.theme.assistant_accent
import com.zaroslikov.assistant.ui.theme.assistant_accent_light
import com.zaroslikov.assistant.ui.theme.assistant_bubble_assistant
import com.zaroslikov.assistant.ui.theme.assistant_bubble_user
import com.zaroslikov.assistant.ui.theme.black_2
import com.zaroslikov.assistant.ui.theme.gray_7
import com.zaroslikov.assistant.ui.theme.green_6
import com.zaroslikov.assistant.ui.theme.green_shamrock
import com.zaroslikov.assistant.ui.theme.grey_2
import com.zaroslikov.assistant.ui.theme.marengo
import com.zaroslikov.assistant.ui.theme.red_7
import com.zaroslikov.assistant.ui.theme.textBold_14
import com.zaroslikov.assistant.ui.theme.textBold_16
import com.zaroslikov.assistant.ui.theme.text_12
import com.zaroslikov.assistant.ui.theme.text_14
import com.zaroslikov.assistant.ui.theme.text_16
import com.zaroslikov.assistant.ui.theme.white

// Угол со стороны автора скруглён меньше — «хвостик» пузыря
private val UserBubbleShape = RoundedCornerShape(
    topStart = 18.dp, topEnd = 4.dp, bottomEnd = 18.dp, bottomStart = 18.dp
)
private val AssistantBubbleShape = RoundedCornerShape(
    topStart = 4.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 18.dp
)

/** Доля ширины экрана, которую может занять сообщение. */
private const val BUBBLE_MAX_WIDTH = 0.8f

/**
 * Сообщение чата. Пользовательское — справа зелёным пузырём, ответ помощника — слева
 * с аватаром; под текстом помощника — карточка действия/аналитики и чипы-подсказки.
 */
@Composable
fun ChatBubble(
    message: ChatMessage,
    onSuggestion: (String) -> Unit,
    onConfirm: (Long) -> Unit,
    onCancel: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    onUndo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.author == ChatAuthor.USER
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val maxBubbleWidth = maxWidth * BUBBLE_MAX_WIDTH
        if (isUser)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                MessageText(
                    text = message.text,
                    isUser = true,
                    isError = message.isError,
                    modifier = Modifier.widthIn(max = maxBubbleWidth)
                )
            }
        else
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistantAvatar()
                Column(
                    modifier = Modifier.widthIn(max = maxBubbleWidth),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (message.text.isNotBlank())
                        MessageText(text = message.text, isUser = false, isError = message.isError)
                    message.action?.let { card ->
                        ActionCardView(
                            card = card,
                            onConfirm = { onConfirm(message.id) },
                            onCancel = { onCancel(message.id) },
                            onEdit = { onEdit(message.id) },
                            onUndo = { onUndo(message.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    message.analysis?.let { card ->
                        AnalysisCardView(card = card, modifier = Modifier.fillMaxWidth())
                    }
                    if (message.suggestions.isNotEmpty())
                        SuggestionChips(suggestions = message.suggestions, onSuggestion = onSuggestion)
                }
            }
    }
}

@Composable
private fun MessageText(
    text: String,
    isUser: Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(if (isUser) UserBubbleShape else AssistantBubbleShape)
            .background(if (isUser) assistant_bubble_user else assistant_bubble_assistant)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = text_16,
            color = when {
                isUser -> white
                isError -> red_7
                else -> black_2
            }
        )
    }
}

@Composable
private fun SuggestionChips(suggestions: List<String>, onSuggestion: (String) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        suggestions.forEach { suggestion ->
            SuggestionChip(
                onClick = { onSuggestion(suggestion) },
                label = {
                    Text(
                        text = suggestion,
                        style = text_14,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = assistant_accent_light,
                    labelColor = assistant_accent
                ),
                border = BorderStroke(1.dp, assistant_accent.copy(alpha = 0.3f))
            )
        }
    }
}

/** Строка карточки: подпись слева, значение справа. */
@Composable
private fun CardRowView(row: CardRow, compact: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = row.label,
            style = if (compact) text_12 else text_14,
            color = gray_7,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = row.value,
            style = if (compact) text_14 else textBold_14,
            color = black_2,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Карточка команды записи: распознанные поля и, в зависимости от статуса,
 * кнопки «Записать / Изменить / Отмена», отметка «Записано» с отменой или итоговый статус.
 */
@Composable
fun ActionCardView(
    card: ActionCard,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onEdit: () -> Unit,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier
) {
    AssistantCard(modifier = modifier) {
        Text(text = card.title, style = textBold_16, color = black_2)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            card.rows.forEach { CardRowView(row = it) }
        }
        when (card.status) {
            ActionStatus.PENDING -> {
                Text(
                    text = stringResource(R.string.assistant_card_hint),
                    style = text_12,
                    color = gray_7
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = CardButtonPadding,
                        colors = ButtonDefaults.buttonColors(containerColor = green_6, contentColor = white)
                    ) {
                        Text(text = stringResource(R.string.assistant_card_confirm), style = textBold_14)
                    }
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = CardButtonPadding,
                        border = BorderStroke(1.dp, green_6),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = green_shamrock)
                    ) {
                        Text(text = stringResource(R.string.assistant_card_edit), style = textBold_14)
                    }
                    TextButton(onClick = onCancel, contentPadding = CardButtonPadding) {
                        Text(
                            text = stringResource(R.string.assistant_card_cancel),
                            style = textBold_14,
                            color = marengo
                        )
                    }
                }
            }

            ActionStatus.SAVED -> Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = green_6,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.assistant_card_saved),
                    style = textBold_14,
                    color = green_shamrock,
                    modifier = Modifier.weight(1f)
                )
                if (card.saved != null)
                    TextButton(onClick = onUndo, contentPadding = CardButtonPadding) {
                        Text(
                            text = stringResource(R.string.assistant_card_undo),
                            style = text_14,
                            color = marengo
                        )
                    }
            }

            ActionStatus.SAVING, ActionStatus.UNDOING -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = green_6
                )
                Text(
                    text = stringResource(
                        if (card.status == ActionStatus.SAVING) R.string.assistant_card_saving else R.string.assistant_card_undoing
                    ),
                    style = textBold_14,
                    color = gray_7
                )
            }

            ActionStatus.CANCELLED -> Text(
                text = stringResource(R.string.assistant_card_cancelled),
                style = textBold_14,
                color = gray_7
            )

            ActionStatus.UNDONE -> Text(
                text = stringResource(R.string.assistant_card_undone),
                style = textBold_14,
                color = gray_7
            )
        }
    }
}

private val CardButtonPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)

/** Карточка ответа аналитики: заголовок, подзаголовок, показатели и необязательный список. */
@Composable
fun AnalysisCardView(card: AnalysisCard, modifier: Modifier = Modifier) {
    AssistantCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = card.title, style = textBold_16, color = black_2)
            if (!card.subtitle.isNullOrBlank())
                Text(text = card.subtitle, style = text_12, color = gray_7)
        }
        if (card.rows.isNotEmpty())
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                card.rows.forEach { CardRowView(row = it) }
            }
        if (card.list.isNotEmpty()) {
            HorizontalDivider(thickness = 1.dp, color = grey_2)
            if (!card.listTitle.isNullOrBlank())
                Text(text = card.listTitle, style = textBold_14, color = black_2)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                card.list.forEachIndexed { index, row ->
                    if (index > 0) HorizontalDivider(thickness = 0.5.dp, color = grey_2)
                    CardRowView(row = row, compact = true)
                }
            }
        }
    }
}

/** «Помощник печатает»: пузырь с тремя мигающими точками. */
@Composable
fun ThinkingIndicator(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "thinking")
    val description = stringResource(R.string.assistant_thinking)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistantAvatar()
        Row(
            modifier = Modifier
                .clip(AssistantBubbleShape)
                .background(assistant_bubble_assistant)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                val dotAlpha by transition.animateFloat(
                    initialValue = 0.25f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 500),
                        repeatMode = RepeatMode.Reverse,
                        initialStartOffset = StartOffset(index * 160)
                    ),
                    label = "dot$index"
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(dotAlpha)
                        .clip(CircleShape)
                        .background(gray_7)
                )
            }
            Text(
                text = description,
                style = text_12,
                color = gray_7,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
