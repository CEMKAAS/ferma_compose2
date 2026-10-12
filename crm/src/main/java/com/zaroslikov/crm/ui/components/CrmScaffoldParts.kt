package com.zaroslikov.crm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.crm.R
import com.zaroslikov.crm.ui.theme.black_2
import com.zaroslikov.crm.ui.theme.green_6
import com.zaroslikov.crm.ui.theme.textBold_16
import com.zaroslikov.crm.ui.theme.textBold_20
import com.zaroslikov.crm.ui.theme.white

/** Верхняя панель экрана CRM: заголовок по центру, «назад» слева, действия справа. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmTopBar(
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
                        contentDescription = stringResource(R.string.crm_back),
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
fun CrmBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    ),
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = title, style = textBold_20, color = black_2)
            content()
        }
    }
}

/** Плавающая кнопка; с [text] — расширенная. */
@Composable
fun CrmFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Add,
    contentDescription: String? = null,
    text: String? = null
) {
    if (text != null)
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            containerColor = green_6,
            contentColor = white,
            icon = { Icon(imageVector = icon, contentDescription = contentDescription) },
            text = { Text(text = text, style = textBold_16) }
        )
    else
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            containerColor = green_6,
            contentColor = white
        ) {
            Icon(imageVector = icon, contentDescription = contentDescription)
        }
}
