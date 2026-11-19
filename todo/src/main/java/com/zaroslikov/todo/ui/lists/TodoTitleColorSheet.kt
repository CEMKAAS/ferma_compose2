package com.zaroslikov.todo.ui.lists

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.TodoListIcon
import com.zaroslikov.todo.ui.components.TodoBottomSheet
import com.zaroslikov.todo.ui.components.TodoColorPickerRow
import com.zaroslikov.todo.ui.components.TodoListBadge
import com.zaroslikov.todo.ui.components.TodoTextField
import com.zaroslikov.todo.ui.components.imageVector
import com.zaroslikov.todo.ui.components.titleRes
import com.zaroslikov.todo.ui.theme.gray_6
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.green_6
import com.zaroslikov.todo.ui.theme.marengo
import com.zaroslikov.todo.ui.theme.textBold_14
import com.zaroslikov.todo.ui.theme.textBold_16
import com.zaroslikov.todo.ui.theme.text_12
import com.zaroslikov.todo.ui.theme.todoColor
import com.zaroslikov.todo.ui.theme.white

/** Значков в ряду сетки выбора. */
private const val ICONS_PER_ROW = 6

/**
 * Форма «название + цвет» в шторке — общая для списков, статусов и тегов; [icon] — значок,
 * выбирается только у списков. [id] = 0 — создание. [titleEditable] = false у дефолтного
 * статуса: меняется только цвет.
 */
data class TodoTitleColorForm(
    val id: Long = 0,
    val title: String = "",
    val colorIndex: Int = 0,
    val icon: TodoListIcon = TodoListIcon.DEFAULT,
    val titleError: Boolean = false,
    val titleEditable: Boolean = true
) {
    val isNew: Boolean get() = id == 0L

    /** Название обязательно, если его можно редактировать. */
    val isValid: Boolean get() = !titleEditable || title.isNotBlank()

    fun withTitle(title: String): TodoTitleColorForm = copy(title = title, titleError = false)

    /** Результат нажатия «Сохранить»: валидная форма закрывается (null), иначе — ошибка поля. */
    fun submitted(): TodoTitleColorForm? = if (isValid) null else copy(titleError = true)
}

/**
 * Шторка с формой [form]. [lockedTitle] — что показать в поле, когда название не редактируется
 * (у дефолтного статуса — строка из ресурсов, а не сохранённый запасной `title`).
 * [onIconChange] не null — шторка списка: сверху превью (круг цвета со значком), под цветами —
 * сетка значков.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTitleColorSheet(
    sheetTitle: String,
    form: TodoTitleColorForm,
    onTitleChange: (String) -> Unit,
    onColorChange: (Int) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    lockedTitle: String = form.title,
    onIconChange: ((TodoListIcon) -> Unit)? = null
) {
    val color by animateColorAsState(todoColor(form.colorIndex), label = "listColor")
    TodoBottomSheet(title = sheetTitle, onDismiss = onDismiss) {
        if (onIconChange != null)
            TodoListBadge(
                icon = form.icon,
                color = color,
                size = 64.dp,
                iconSize = 36.dp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        TodoTextField(
            value = if (form.titleEditable) form.title else lockedTitle,
            onValueChange = onTitleChange,
            label = stringResource(R.string.todo_field_title),
            readOnly = !form.titleEditable,
            isError = form.titleError,
            errorText = stringResource(R.string.todo_error_title_empty)
        )
        if (!form.titleEditable)
            Text(text = stringResource(R.string.todo_form_title_locked), style = text_12, color = gray_7)
        Text(text = stringResource(R.string.todo_color), style = textBold_14, color = marengo)
        TodoColorPickerRow(selectedIndex = form.colorIndex, onSelect = onColorChange)
        if (onIconChange != null) {
            Text(text = stringResource(R.string.todo_list_icon), style = textBold_14, color = marengo)
            TodoListIconGrid(selected = form.icon, accent = color, onSelect = onIconChange)
        }
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = green_6, contentColor = white)
        ) {
            Text(
                text = stringResource(if (form.isNew) R.string.todo_create else R.string.todo_save),
                style = textBold_16
            )
        }
    }
}

/** Сетка значков по [ICONS_PER_ROW] в ряд; выбранный — белым на цвете списка [accent]. */
@Composable
private fun TodoListIconGrid(selected: TodoListIcon, accent: Color, onSelect: (TodoListIcon) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TodoListIcon.entries.chunked(ICONS_PER_ROW).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { icon ->
                    val isSelected = icon == selected
                    val description = stringResource(icon.titleRes())
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(if (isSelected) accent else gray_6)
                            .clickable(role = Role.RadioButton) { onSelect(icon) }
                            .semantics {
                                this.selected = isSelected
                                contentDescription = description
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon.imageVector(),
                            contentDescription = null,
                            tint = if (isSelected) white else marengo,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                repeat(ICONS_PER_ROW - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
