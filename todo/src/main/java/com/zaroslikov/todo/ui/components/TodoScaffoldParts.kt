package com.zaroslikov.todo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.green_6
import com.zaroslikov.todo.ui.theme.textBold_16
import com.zaroslikov.todo.ui.theme.textBold_20
import com.zaroslikov.todo.ui.theme.white

/** Верхняя панель экрана задач: заголовок по центру, «назад» слева, действия справа. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTopBar(
    title: String,
    navigateBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = textBold_20,
                color = black_2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (navigateBack != null)
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.todo_back),
                        tint = black_2
                    )
                }
        },
        actions = actions,
        scrollBehavior = scrollBehavior,
        // Отступ под статус-бар уже даёт внешний Scaffold проекта — иначе он удваивается
        windowInsets = WindowInsets(0),
        colors = TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = white
        )
    )
}

/** Нижний лист с заголовком; содержимое прокручивается. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    ),
    contentHorizontalPadding: Dp = 20.dp,
    headerActions: (@Composable RowScope.() -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    hideFooterOnIme: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = white
    ) {
        val visibleFooter = if (hideFooterOnIme && WindowInsets.ime.getBottom(LocalDensity.current) > 0) {
            null
        } else {
            footer
        }
        Column(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .then(if (visibleFooter != null) Modifier.weight(1f, fill = false) else Modifier)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
                    .then(if (visibleFooter == null) Modifier.navigationBarsPadding() else Modifier),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (headerActions == null)
                    Text(text = title, style = textBold_20, color = black_2, modifier = Modifier.padding(horizontal = 20.dp))
                else
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = title, style = textBold_20, color = black_2, modifier = Modifier.weight(1f))
                        headerActions()
                    }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = contentHorizontalPadding),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = content
                )
            }
            if (visibleFooter != null)
                Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp)) {
                    visibleFooter()
                }
        }
    }
}

/** Плавающая кнопка; с [text] — расширенная. [containerColor] — например, цвет списка. */
@Composable
fun TodoFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Add,
    contentDescription: String? = null,
    text: String? = null,
    containerColor: Color = green_6
) {
    if (text != null)
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            containerColor = containerColor,
            contentColor = white,
            icon = { Icon(imageVector = icon, contentDescription = contentDescription) },
            text = { Text(text = text, style = textBold_16) }
        )
    else
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            containerColor = containerColor,
            contentColor = white
        ) {
            Icon(imageVector = icon, contentDescription = contentDescription)
        }
}
