@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.zaroslikov.fermacompose2.ui.elements

import android.annotation.SuppressLint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.white

@Composable
fun FloatButton(
    onClick: () -> Unit
) {

    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = Color.Transparent,
        modifier = Modifier
            .padding(
                end = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateEndPadding(LocalLayoutDirection.current)
            )
            .drawBehind {
                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF009966), Color(0xFF00A63E)),
                        start = Offset.Zero,
                        end = Offset(size.width, 0f)
                    ),
                    radius = size.minDimension / 2
                )
            }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.item_entry_title),
            tint = white
        )
    }
}

@Composable
fun NeonGlowFab(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Add,
    colors: List<Color>,
    onClick: () -> Unit,
) {
    val glowColor = colors.first()
    val gradient = Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )
    Box(
        modifier = modifier
            .size(64.dp)
            .graphicsLayer {
                // имитация неонового свечения
                shadowElevation = 20f
                shape = CircleShape
                clip = false
                ambientShadowColor = glowColor.copy(alpha = 0.8f)
                spotShadowColor = glowColor.copy(alpha = 0.8f)
            }
            .clip(CircleShape)
            .background(brush = gradient)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Добавить",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("RememberInComposition")
@Composable
fun FabMenu2(
    isListEmpty: Boolean,
    onClick: (AnimalCountVersion) -> Unit
) {
    val items =
        listOf(
            AnimalCountVersion.KILL,
            AnimalCountVersion.EXPENSES,
            AnimalCountVersion.WRITE_OFF,
            AnimalCountVersion.SALE,
            AnimalCountVersion.ADD
        )

    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

    FloatingActionButtonMenu(
        expanded = fabMenuExpanded,
        button = {
            NeonToggleFloatingActionButton(
                checked = fabMenuExpanded,
                onCheckedChange = { fabMenuExpanded = !fabMenuExpanded }
            )
        },
        content = {
            items.forEachIndexed { i, item ->
                AnimatedVisibility(
                    visible = fabMenuExpanded,
                    enter = fadeIn(animationSpec = tween(200, delayMillis = i * 50)) +
                            slideInVertically(
                                animationSpec = tween(300, delayMillis = i * 50),
                                initialOffsetY = { full -> full / 2 }
                            ),
                    exit = fadeOut(animationSpec = tween(150, delayMillis = 20)) +
                            slideOutVertically(
                                animationSpec = tween(200),
                                targetOffsetY = { full -> full / 2 }
                            )
                ) {
                    NeonMenuItem(
                        modifier = Modifier,
                        title = stringResource(item.toResId()),
                        icon = painterResource(item.toDrawRes()),
                        colors = item.toColorList(),
                        onClick = {
                            onClick(item)
                            fabMenuExpanded = false
                        }
                    )
                }

                // Отступ тоже анимируем (исчезает вместе с элементом)
                AnimatedVisibility(
                    visible = fabMenuExpanded,
                    enter = expandVertically(expandFrom = Alignment.Top) +
                            fadeIn(tween(200)),
                    exit = shrinkVertically() + fadeOut(tween(150))
                ) {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    )
}


@SuppressLint("RememberInComposition")
@Composable
fun FabMenu3(
    isListEmpty: Boolean,
    onClick: (Pair<AnimalCountVersion, Boolean>) -> Unit
) {
    val items =
        listOf(
            Triple(R.drawable.icons8__meat60, R.string.button_kill, AnimalCountVersion.KILL),
            Triple(
                R.drawable.baseline_add_shopping_cart_24, R.string.button_expenses,
                AnimalCountVersion.EXPENSES
            ),
            Triple(
                R.drawable.baseline_edit_note_24, R.string.button_write_off,
                AnimalCountVersion.WRITE_OFF
            ),
            Triple(R.drawable.baseline_add_card_24, R.string.button_sale, AnimalCountVersion.SALE),
            Triple(R.drawable.icon_add, R.string.button_add, AnimalCountVersion.ADD)
        )
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val focusRequester = FocusRequester()
    FloatingActionButtonMenu(
        expanded = fabMenuExpanded,
        button = {
            NeonToggleFloatingActionButton(
                checked = fabMenuExpanded,
                onCheckedChange = { fabMenuExpanded = !fabMenuExpanded }
            )
            /* ToggleFloatingActionButton(
                 modifier =
                     Modifier
                         .semantics {
                             traversalIndex = -1f
                             stateDescription =
                                 if (fabMenuExpanded) "Expanded" else "Collapsed"
                             contentDescription = "Toggle menu"
                         }
                         .animateFloatingActionButton(
                             visible = isListEmpty || fabMenuExpanded,
                             alignment = Alignment.BottomEnd,
                         )
                         .focusRequester(focusRequester),
                 checked = fabMenuExpanded,
                 onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
             ) {
                 val imageVector by remember {
                     derivedStateOf {
                         if (checkedProgress > 0.5f) R.drawable.baseline_clear_24 else R.drawable.icon_add

                     }
                 }
                 Icon(
                     painter = painterResource(imageVector),
                     contentDescription = null,
                     modifier = Modifier.animateIcon({ checkedProgress }),
                 )
             }*/
        },
        content = {
            items.forEachIndexed { i, item ->
                FloatingActionButtonMenuItem(
                    modifier =
                        Modifier
                            .semantics {
                                isTraversalGroup = true
                                if (i == items.size - 1) {
                                    customActions =
                                        listOf(
                                            CustomAccessibilityAction(
                                                label = "Close menu",
                                                action = {
                                                    fabMenuExpanded = false
                                                    true
                                                }
                                            )
                                        )
                                }
                            }
                            .then(
                                if (i == 0) {
                                    Modifier.onKeyEvent {
                                        if (
                                            it.type == KeyEventType.KeyDown &&
                                            (it.key == Key.DirectionUp ||
                                                    (it.isShiftPressed && it.key == Key.Tab))
                                        ) {
                                            focusRequester.requestFocus()
                                            return@onKeyEvent true
                                        }
                                        return@onKeyEvent false
                                    }
                                } else Modifier
                            ),
                    onClick = {
                        onClick(item.third to (i == 0))
                        fabMenuExpanded = false
                    },
                    icon = { Icon(painterResource(item.first), contentDescription = null) },
                    text = { Text(stringResource(item.second)) },
                )
            }
        }
    )
}

@Composable
fun NeonToggleFloatingActionButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    iconAdd: ImageVector = Icons.Default.Add,
    iconClose: ImageVector = Icons.Default.Close,
    colorsCollapsed: List<Color> = listOf(Color(0xFF00A63E), Color(0xFF00E280)),
    colorsExpanded: List<Color> = listOf(Color(0xFF4A5565), Color(0xFF314158))
) {
    // анимированный прогресс (0..1)
    val progress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 350, easing = LinearOutSlowInEasing)
    )

    // анимируем размер (явно через lerp Dp)
    val sizeCollapsed: Dp = 64.dp
    val sizeExpanded: Dp = 64.dp
    // lerp для Dp: используем стандартную функцию lerp из Dp (явно с типом)
    val size: Dp = lerp(sizeCollapsed, sizeExpanded, progress)

    // аккуратно формируем анимированные цвета: zip двух списков и для каждой пары делаем lerp(color)
    // явные типы в лямбде убирают проблемы вывода типа
    val animatedColors: List<Color> = colorsCollapsed
        .zip(colorsExpanded) { a: Color, b: Color ->
            androidx.compose.ui.graphics.lerp(a, b, progress)
        }

    val glowColor: Color = animatedColors.firstOrNull() ?: colorsCollapsed.first()

    val gradient = Brush.linearGradient(
        colors = animatedColors,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )

    val icon = if (progress > 0.5f) iconClose else iconAdd

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                shadowElevation = 30f
                shape = CircleShape
                clip = false // тень должна выйти за границы
                ambientShadowColor = glowColor.copy(alpha = 0.9f)
                spotShadowColor = glowColor.copy(alpha = 0.9f)
            }
            .clip(CircleShape)
            .background(brush = gradient, shape = CircleShape)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(lerp(32.dp, 32.dp, progress))
                .graphicsLayer { rotationZ = progress * 180f }
        )
    }
}

@Composable
fun NeonMenuItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: Painter,
    colors: List<Color>,
    onClick: () -> Unit
) {
    val gradient = Brush.linearGradient(colors)

    Box(
        modifier = modifier
            .size(width = 180.dp, height = 48.dp)   // единый размер
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .clickable(onClick = onClick)
            .graphicsLayer {
                shadowElevation = 20f
                shape = RoundedCornerShape(16.dp)
                clip = false
                ambientShadowColor = colors.first().copy(alpha = 0.8f)
                spotShadowColor = colors.last().copy(alpha = 0.8f)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = title,
                color = Color.White,
                style = text_16
            )
        }
    }
}