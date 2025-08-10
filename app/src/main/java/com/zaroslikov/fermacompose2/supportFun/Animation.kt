package com.zaroslikov.fermacompose2.supportFun

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun animatedErrorPadding(isError: Boolean): Dp {
    val target = if (isError) 2.dp else 0.dp
    val animatedPadding by animateDpAsState(
        targetValue = target,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = ""
    )
    return animatedPadding.coerceAtLeast(0.dp)
}


@Composable
fun animateDpSpring(
    targetValue: Dp,
    dampingRatio: Float = Spring.DampingRatioMediumBouncy,
    stiffness: Float = Spring.StiffnessLow
): Dp {
    val animatedValue by animateDpAsState(
        targetValue = targetValue,
        animationSpec = spring(
            dampingRatio = dampingRatio,
            stiffness = stiffness
        ),
        label = ""
    )
    return animatedValue.coerceAtLeast(0.dp)
}

@Composable
fun animateDpSpring(
    condition: Boolean,
    trueValue: Dp,
    falseValue: Dp,
    dampingRatio: Float = Spring.DampingRatioMediumBouncy,
    stiffness: Float = Spring.StiffnessLow
): Dp {
    val targetValue = if (condition) trueValue else falseValue
    val animatedValue by animateDpAsState(
        targetValue = targetValue,
        animationSpec = spring(
            dampingRatio = dampingRatio,
            stiffness = stiffness
        ),
        label = ""
    )
    return animatedValue.coerceAtLeast(0.dp)
}