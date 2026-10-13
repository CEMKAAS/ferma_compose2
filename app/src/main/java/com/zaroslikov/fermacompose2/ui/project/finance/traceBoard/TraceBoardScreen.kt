@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.finance.traceBoard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.project.mainScreen.LocalHideProjectBottomBar
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.runtime.DisposableEffect
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.expandVertically
import androidx.activity.compose.BackHandler
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.DetailSectionBottomSheet
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.violet_2
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.blue_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.gray_8
import com.zaroslikov.fermacompose2.green_2
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.empty_list.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.icon.drawableRes
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.textBold_18
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.white
import kotlinx.coroutines.launch
import java.io.File

object TraceBoardDestination : NavigationDestination {
    override val route = "traceBoard"
    override val titleRes = R.string.trace_board_button
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemProduct"
    const val itemIdArgTree = "itemSuffix"
    val routeWithArgs = "$route/{$itemIdArg}/{$itemIdArgTwo}/{$itemIdArgTree}"
}

private const val MIN_SCALE = 0.25f
private const val MAX_SCALE = 3f
private const val ZOOM_STEP = 1.25f

@Composable
fun TraceBoardScreen(
    navigateBack: () -> Unit,
    navigateToTraceBoard: (Triple<Long, String, Suffix>) -> Unit,
    viewModel: TraceBoardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // «На весь экран»: прячутся заголовок доски и нижний бар проекта
    var isFullScreen by rememberSaveable { mutableStateOf(false) }
    val hideBottomBar = LocalHideProjectBottomBar.current
    LaunchedEffect(isFullScreen) { hideBottomBar?.value = isFullScreen }
    // Уход с доски возвращает бар, иначе он пропал бы и на других экранах проекта
    DisposableEffect(Unit) { onDispose { hideBottomBar?.value = false } }
    // «Назад» в полноэкранном режиме сначала выходит из него
    BackHandler(enabled = isFullScreen) { isFullScreen = false }

    Scaffold(
        // Нижний бар проекта уже учитывает системную панель — свой отступ дал бы белую полосу
        contentWindowInsets = WindowInsets(0),
        topBar = {
            AnimatedVisibility(
                visible = !isFullScreen,
                enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut()
            ) {
                TopAppBarBack(
                    title = stringResource(R.string.trace_board_title_s, state.title),
                    onNavigateBackClick = navigateBack
                )
            }
        }
    ) { innerPadding ->
        val layout = state.layout
        val graph = state.graph
        if (state.isLoading || layout == null || graph == null) {
            CircularProgress(modifier = Modifier.padding(innerPadding))
        } else {
            TraceBoardContent(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                graph = graph,
                layout = layout,
                collapsedHubs = state.collapsedHubs,
                isEmpty = state.isEmpty,
                isFullScreen = isFullScreen,
                onFullScreenClick = { isFullScreen = !isFullScreen },
                onNodeClick = { viewModel.onIntent(TraceBoardIntent.NodeSelected(it)) },
                onHubClick = { viewModel.onIntent(TraceBoardIntent.HubToggled(it)) }
            )
        }

        val record = state.selectedNode?.record
        if (record != null) {
            // Продажа или списание — та же карточка, что в разделах, только для просмотра
            TraceRecordDetailSheet(
                record = record,
                onDismiss = { viewModel.onIntent(TraceBoardIntent.DetailsDismissed) }
            )
        } else state.selectedNode?.let { node ->
            TraceNodeDetailsSheet(
                node = node,
                onOpenBoard = { target ->
                    viewModel.onIntent(TraceBoardIntent.DetailsDismissed)
                    // Продукт, переработанный «сам в себя», уже открыт — новый экран не нужен
                    val isSameProduct = target.first == state.idPT && target.second == state.title
                    if (!isSameProduct) navigateToTraceBoard(target)
                },
                onDismiss = { viewModel.onIntent(TraceBoardIntent.DetailsDismissed) }
            )
        }
    }
}

/**
 * Масштаб и сдвиг доски. Читаются только в `graphicsLayer {}`, поэтому жест
 * не перекомпоновывает карточки.
 */
@Stable
private class BoardTransform {
    var scale by mutableFloatStateOf(1f)
    var offset by mutableStateOf(Offset.Zero)

    /** Пока вид не посчитан, доска не рисуется — иначе первый кадр показал бы её левый верхний угол. */
    var isInitialized by mutableStateOf(false)

    /** Масштаб начального вида — к нему возвращает кнопка «По центру». */
    var initialScale = 1f

    /** Центр продукта (px доски) на момент последней раскладки — чтобы вид не прыгал при её смене. */
    var productCenter = Offset.Zero

    /** Масштабирование вокруг точки [centroid] экрана (начало координат слоя — левый верхний угол). */
    fun zoomAt(centroid: Offset, zoom: Float, pan: Offset = Offset.Zero) {
        val newScale = (scale * zoom).coerceIn(MIN_SCALE, MAX_SCALE)
        val k = newScale / scale
        offset = (offset - centroid) * k + centroid + pan
        scale = newScale
    }

    /** Продукт — в центр вьюпорта с начальным масштабом. */
    fun centerOn(productCenterPx: Offset, viewportCenter: Offset) {
        scale = initialScale
        offset = viewportCenter - productCenterPx * initialScale
    }

    /** Раскладка пересчитана: сдвигаем вид на перемещение продукта, чтобы он остался на месте экрана. */
    fun followProduct(newProductCenterPx: Offset) {
        if (isInitialized) offset -= (newProductCenterPx - productCenter) * scale
        productCenter = newProductCenterPx
    }
}

@Composable
private fun TraceBoardContent(
    modifier: Modifier,
    graph: TraceGraph,
    layout: TraceBoardLayout,
    collapsedHubs: Set<String>,
    isEmpty: Boolean,
    isFullScreen: Boolean,
    onFullScreenClick: () -> Unit,
    onNodeClick: (TraceNode) -> Unit,
    onHubClick: (String) -> Unit
) {
    val transform = remember { BoardTransform() }
    var viewport by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    val productCenterPx = with(density) {
        Offset(layout.product.centerX.dp.toPx(), layout.product.centerY.dp.toPx())
    }

    // Смена раскладки (свернули хаб, пришли данные): продукт остаётся на том же месте экрана.
    // SideEffect выполняется до отрисовки кадра — без одного кадра со старым сдвигом
    SideEffect { transform.followProduct(productCenterPx) }

    // Начальный вид: продукт по центру экрана; широкую доску немного отдаляем.
    // Размер читается внутри эффекта: корутина первого запуска стартует уже после onSizeChanged,
    // и захваченный при композиции центр (0,0) был бы устаревшим
    LaunchedEffect(viewport) {
        val size = viewport
        if (transform.isInitialized || size == IntSize.Zero) return@LaunchedEffect
        val layoutWidthPx = with(density) { layout.width.dp.toPx() }
        transform.initialScale = (size.width / layoutWidthPx).coerceIn(0.5f, 1f)
        transform.centerOn(productCenterPx, Offset(size.width / 2f, size.height / 2f))
        transform.isInitialized = true
    }

    Box(modifier = modifier) {
        TraceBoardCanvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    // Вьюпорт вырос или сжался (вход/выход из полноэкранного режима):
                    // то, что было в центре, остаётся в центре
                    if (transform.isInitialized && viewport != IntSize.Zero) {
                        transform.offset += Offset(
                            (size.width - viewport.width) / 2f,
                            (size.height - viewport.height) / 2f
                        )
                    }
                    viewport = size
                },
            layout = layout,
            transform = transform,
            parents = remember(graph) {
                graph.children.flatMap { (parent, kids) -> kids.map { it to parent } }.toMap()
            },
            collapsedHubs = collapsedHubs,
            onNodeClick = onNodeClick,
            onHubClick = onHubClick
        )

        // Сверху — итоги по продукту; под ними подсказка, если операций ещё нет
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TraceSummaryBar(items = graph.summary)
            if (isEmpty) {
                Text(
                    text = stringResource(R.string.trace_board_empty),
                    style = text_12,
                    color = gray_7,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(white)
                        .border(1.dp, grey_2, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                )
            }
        }

        // Слева внизу — легенда одним блоком
        TraceLegend(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            categories = graph.nodes.map { it.category }.distinct().sortedBy { it.ordinal }
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val center = Offset(viewport.width / 2f, viewport.height / 2f)
            ZoomButton(Icons.Default.Add, stringResource(R.string.trace_board_zoom_in)) {
                transform.zoomAt(center, ZOOM_STEP)
            }
            ZoomButton(Icons.Default.Remove, stringResource(R.string.trace_board_zoom_out)) {
                transform.zoomAt(center, 1f / ZOOM_STEP)
            }
            ZoomButton(Icons.Default.CenterFocusStrong, stringResource(R.string.trace_board_center)) {
                transform.centerOn(productCenterPx, center)
            }
            ZoomButton(
                if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                stringResource(
                    if (isFullScreen) R.string.trace_board_full_screen_exit else R.string.trace_board_full_screen
                ),
                onClick = onFullScreenClick
            )
        }
    }
}

/** Бесконечная доска: точечная сетка, нити и карточки узлов, жесты сдвига и масштаба. */
@Composable
private fun TraceBoardCanvas(
    modifier: Modifier,
    layout: TraceBoardLayout,
    transform: BoardTransform,
    /** Родитель каждого узла: из него выезжают раскрытые карточки и в него уходят свёрнутые. */
    parents: Map<String, String>,
    collapsedHubs: Set<String>,
    onNodeClick: (TraceNode) -> Unit,
    onHubClick: (String) -> Unit
) {
    Box(
        modifier = modifier
            .clipToBounds()
            .background(ghostly_white)
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    transform.zoomAt(centroid, zoom, pan)
                }
            }
    ) {
        val boardWidthPx = with(LocalDensity.current) { layout.width.dp.roundToPx() }
        val boardHeightPx = with(LocalDensity.current) { layout.height.dp.roundToPx() }
        Box(
            modifier = Modifier
                // Слой доски измеряется ровно под её размер и кладётся в (0,0) вьюпорта.
                // Так ни wrapContentSize, ни requiredSize не сдвигают его «по центру» при переполнении
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(Constraints.fixed(boardWidthPx, boardHeightPx))
                    layout(constraints.maxWidth, constraints.maxHeight) { placeable.place(0, 0) }
                }
                .graphicsLayer {
                    translationX = transform.offset.x
                    translationY = transform.offset.y
                    scaleX = transform.scale
                    scaleY = transform.scale
                    transformOrigin = TransformOrigin(0f, 0f)
                    // До расчёта начального вида доска невидима — без скачка из левого верхнего угла
                    alpha = if (transform.isInitialized) 1f else 0f
                }
        ) {
            // Раскрытие и сворачивание анимируются: позиции и прозрачность — из аниматора
            val animator = remember { TraceBoardAnimator() }
            val animationScope = rememberCoroutineScope()
            LaunchedEffect(layout) { animator.update(layout, parents, animationScope) }

            DotGrid(Modifier.fillMaxSize())
            val productCenter = Offset(layout.product.centerX, layout.product.centerY)
            TraceEdges(Modifier.fillMaxSize(), animator, productCenter, transform)
            animator.nodes.values.toList().forEach { animated ->
                val placed = animated.placed
                key(placed.node.id) {
                    TraceNodeCard(
                        modifier = Modifier
                            .offset {
                                val topLeft = animator.boardTopLeft(animated, productCenter)
                                IntOffset(topLeft.x.dp.roundToPx(), topLeft.y.dp.roundToPx())
                            }
                            .graphicsLayer { alpha = animated.alpha.value }
                            .requiredSize(placed.width.dp, placed.height.dp),
                        node = placed.node,
                        isCollapsed = placed.node.id in collapsedHubs,
                        onClick = {
                            // Уходящая карточка уже не принадлежит доске — нажатия игнорируются
                            if (animated.isLeaving) return@TraceNodeCard
                            if (placed.node.kind in TraceBoardLayoutEngine.COLLAPSIBLE) onHubClick(placed.node.id)
                            else onNodeClick(placed.node)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DotGrid(modifier: Modifier) {
    Canvas(modifier = modifier) {
        val step = 24.dp.toPx()
        val width = size.width
        val height = size.height
        val points = buildList {
            var y = step
            while (y < height) {
                var x = step
                while (x < width) {
                    add(Offset(x, y))
                    x += step
                }
                y += step
            }
        }
        drawPoints(
            points = points,
            pointMode = PointMode.Points,
            color = gray_8,
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

/** Нити: кубическая кривая Безье от правого края источника к левому краю приёмника. */
@Composable
private fun TraceEdges(
    modifier: Modifier,
    animator: TraceBoardAnimator,
    productCenter: Offset,
    transform: BoardTransform
) {
    // Один Path на всё рисование: концы нитей следуют за анимированными карточками
    val path = remember { Path() }
    Canvas(modifier = modifier) {
        // При сильном отдалении нити не должны превращаться в волоски: толщину компенсируем масштабом
        val compensation = 1f / transform.scale.coerceAtMost(1f)
        val stroke = Stroke(width = 2.dp.toPx() * compensation, cap = StrokeCap.Round)
        val dot = 3.dp.toPx() * compensation
        animator.edges.forEach { edge ->
            val from = animator.nodes[edge.from] ?: return@forEach
            val to = animator.nodes[edge.to] ?: return@forEach
            val fromTopLeft = animator.boardTopLeft(from, productCenter)
            val toTopLeft = animator.boardTopLeft(to, productCenter)
            // От правой середины источника к левой середине приёмника (dp → px)
            val start = Offset(
                (fromTopLeft.x + from.placed.width).dp.toPx(),
                (fromTopLeft.y + from.placed.height / 2f).dp.toPx()
            )
            val end = Offset(toTopLeft.x.dp.toPx(), (toTopLeft.y + to.placed.height / 2f).dp.toPx())
            val dx = end.x - start.x
            path.reset()
            path.moveTo(start.x, start.y)
            path.cubicTo(start.x + dx * 0.5f, start.y, end.x - dx * 0.5f, end.y, end.x, end.y)
            val color = edge.category.color
            val alpha = minOf(from.alpha.value, to.alpha.value)
            drawPath(path, color = color, alpha = alpha, style = stroke)
            drawCircle(color = color, alpha = alpha, radius = dot, center = start)
            drawCircle(color = color, alpha = alpha, radius = dot, center = end)
        }
    }
}

// ================= Карточки узлов =================

private val CardShape = RoundedCornerShape(12.dp)

private val TitleStyle: TextStyle
    @Composable get() = text_14.copy(fontWeight = FontWeight.SemiBold)

@Composable
private fun TraceNodeCard(
    modifier: Modifier,
    node: TraceNode,
    isCollapsed: Boolean,
    onClick: () -> Unit
) {
    when (node.kind) {
        TraceNodeKind.PRODUCT -> ProductCard(modifier, node, onClick)
        TraceNodeKind.HUB -> HubCard(modifier, node, isCollapsed, onClick)
        TraceNodeKind.CATEGORY -> CategoryCard(modifier, node, isCollapsed, onClick)
        TraceNodeKind.SOURCE -> SourceCard(modifier, node, onClick)
        TraceNodeKind.OPERATION,
        TraceNodeKind.RESULT,
        TraceNodeKind.INGREDIENT -> CompactCard(modifier, node, onClick)
    }
}

@Composable
private fun ProductCard(modifier: Modifier, node: TraceNode, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(CardShape)
            .background(Brush.linearGradient(listOf(price_green, green_2)))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Иконка и название продукта — в одной строке
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_add_product),
                    contentDescription = null,
                    tint = white,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = node.title,
                style = textBold_18,
                color = white,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(text = node.value, style = text_14, color = white, maxLines = 1, overflow = TextOverflow.Ellipsis)
        node.subtitle?.let {
            Text(
                text = it,
                style = text_12,
                color = white.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HubCard(modifier: Modifier, node: TraceNode, isCollapsed: Boolean, onClick: () -> Unit) {
    val category = node.category
    Row(
        modifier = modifier
            .clip(CardShape)
            .background(white)
            .border(1.dp, category.color, CardShape)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CategoryIcon(category)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = node.title,
                style = textBold_16,
                color = black_2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = node.value, style = text_14, color = black_2, maxLines = 1, overflow = TextOverflow.Ellipsis)
            node.subtitle?.let {
                Text(text = it, style = text_12, color = gray_7, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.weight(1f))
            node.caption?.let {
                Text(text = it, style = text_12, color = gray_7, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        CollapseChevron(isCollapsed = isCollapsed, color = category.color)
    }
}

/**
 * Стрелка сбоку карточки: у свёрнутой смотрит вправо — туда, где раскроются операции,
 * у развёрнутой — влево. Поворот анимирован.
 */
@Composable
private fun RowScope.CollapseChevron(isCollapsed: Boolean, color: Color) {
    val rotation by animateFloatAsState(
        targetValue = if (isCollapsed) 90f else -90f,
        label = "collapseChevron"
    )
    Box(
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .size(24.dp)
            .clip(CircleShape)
            .background(if (isCollapsed) color else color.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_keyboard_arrow_up),
            contentDescription = null,
            tint = if (isCollapsed) white else color,
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer { rotationZ = rotation }
        )
    }
}

/** Категория операций внутри хаба: название, количество, сумма и число операций. */
@Composable
private fun CategoryCard(modifier: Modifier, node: TraceNode, isCollapsed: Boolean, onClick: () -> Unit) {
    val category = node.category
    Row(
        modifier = modifier
            .clip(CardShape)
            .background(category.tint)
            .border(1.dp, category.color.copy(alpha = 0.5f), CardShape)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(white),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_list),
                contentDescription = null,
                tint = category.color,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = node.title,
                style = TitleStyle,
                color = black_2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = node.value, style = text_14, color = black_2, maxLines = 1, overflow = TextOverflow.Ellipsis)
            node.subtitle?.let {
                Text(text = it, style = text_12, color = gray_7, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            node.caption?.let {
                Text(text = it, style = text_12, color = gray_7, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        // Категорию тоже можно свернуть — стрелка как у хаба
        CollapseChevron(isCollapsed = isCollapsed, color = category.color)
    }
}

@Composable
private fun SourceCard(modifier: Modifier, node: TraceNode, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clip(CardShape)
            .background(white)
            .border(1.dp, grey_2, CardShape)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val imagePath = node.imagePath
        val icon = node.icon
        val animalKind = node.animalKind
        when {
            // Фото животного — в кольце цвета его вида
            imagePath != null -> Image(
                painter = rememberAsyncImagePainter(File(imagePath)),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .then(
                        if (animalKind != null)
                            Modifier.border(2.dp, Brush.linearGradient(animalKind.colors), CircleShape)
                        else Modifier
                    )
            )

            // Значок как в списке животных: градиент по виду, белая иконка
            icon != null && animalKind != null -> Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(animalKind.colors)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon.drawableRes),
                    contentDescription = null,
                    tint = white,
                    modifier = Modifier.size(20.dp)
                )
            }

            icon != null -> Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(node.category.tint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon.drawableRes),
                    contentDescription = null,
                    tint = node.category.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            else -> CategoryIcon(node.category)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = node.title,
                style = TitleStyle,
                color = black_2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            node.subtitle?.let {
                Text(text = it, style = text_12, color = gray_7, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(text = node.value, style = text_14, color = black_2, maxLines = 1, overflow = TextOverflow.Ellipsis)
            node.caption?.let {
                Text(text = it, style = text_12, color = gray_7, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

/** Операция, результат или ингредиент переработки: компактная карточка с цветной полосой. */
@Composable
private fun CompactCard(modifier: Modifier, node: TraceNode, onClick: () -> Unit) {
    val category = node.category
    Row(
        modifier = modifier
            .clip(CardShape)
            .background(white)
            .border(1.dp, grey_2, CardShape)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(category.color)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(
                text = node.title,
                style = TitleStyle,
                color = black_2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = node.value, style = text_14, color = black_2, maxLines = 1, overflow = TextOverflow.Ellipsis)
            // Сумма — всегда отдельной строкой под количеством, как у хабов и категорий
            node.subtitle?.let {
                Text(text = it, style = text_12, color = gray_7, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            val openBoard = if (node.targetProduct != null) stringResource(R.string.trace_board_open_board_short) else null
            if (node.kind == TraceNodeKind.INGREDIENT) {
                // У ингредиента высота меньше — подпись и ссылка в одну строку
                Row(verticalAlignment = Alignment.CenterVertically) {
                    node.caption?.let {
                        Text(
                            text = it,
                            style = text_12,
                            color = gray_7,
                            maxLines = 1,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                    openBoard?.let {
                        Spacer(Modifier.width(6.dp))
                        Text(text = it, style = text_12, color = category.color, maxLines = 1)
                    }
                }
            } else {
                node.caption?.let {
                    Text(text = it, style = text_12, color = gray_7, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                openBoard?.let {
                    Text(text = it, style = text_12, color = category.color, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun CategoryIcon(category: TraceCategory) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(category.tint),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(category.iconRes),
            contentDescription = null,
            tint = category.color,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ================= Оверлей =================

/** Итоги по продукту: цветная точка, подпись и величина; не поместившиеся переносятся на следующую строку. */
@Composable
private fun TraceSummaryBar(items: List<TraceSummaryItem>) {
    if (items.isEmpty()) return
    var expanded by rememberSaveable { mutableStateOf(true) }
    CollapsiblePanel(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(R.string.trace_board_summary_title),
        expanded = expanded,
        onToggle = { expanded = !expanded }
    ) {
        SummaryGrid(items)
    }
}

/** Ровная сетка по [SUMMARY_COLUMNS] ячейки одинаковой ширины; пустые ячейки добивают последнюю строку. */
@Composable
private fun SummaryGrid(items: List<TraceSummaryItem>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.chunked(SUMMARY_COLUMNS).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { item -> SummaryCell(item, Modifier.weight(1f)) }
                repeat(SUMMARY_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

private const val SUMMARY_COLUMNS = 3

@Composable
private fun SummaryCell(item: TraceSummaryItem, modifier: Modifier) {
            Column(modifier = modifier) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(item.category.color)
                    )
                    Text(
                        text = stringResource(item.category.summaryTitleRes),
                        style = text_12,
                        color = gray_7,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = item.value,
                    style = TitleStyle,
                    color = black_2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
}

/** Легенда одним блоком: заголовок и строки «точка — категория»; сворачивается до заголовка. */
@Composable
private fun TraceLegend(modifier: Modifier, categories: List<TraceCategory>) {
    if (categories.isEmpty()) return
    var expanded by rememberSaveable { mutableStateOf(true) }
    CollapsiblePanel(
        modifier = modifier,
        title = stringResource(R.string.trace_board_legend_title),
        expanded = expanded,
        onToggle = { expanded = !expanded }
    ) {
        LegendRows(categories)
    }
}

@Composable
private fun LegendRows(categories: List<TraceCategory>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        categories.forEach { category ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(category.color)
                )
                Text(text = stringResource(category.titleRes), style = text_12, color = black_2)
            }
        }
    }
}

/**
 * Белая панель поверх доски с заголовком-переключателем. Содержимое раскрывается и
 * сворачивается с анимацией высоты и прозрачности, стрелка в заголовке поворачивается.
 */
@Composable
private fun CollapsiblePanel(
    modifier: Modifier,
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 0f else 180f,
        label = "panelArrow"
    )
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(white)
            .border(1.dp, grey_2, RoundedCornerShape(12.dp))
            // Вся панель — переключатель: нажатие в любом месте сворачивает и разворачивает её
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = title, style = text_12, color = gray_7)
            Icon(
                painter = painterResource(R.drawable.icon_keyboard_arrow_up),
                contentDescription = null,
                tint = gray_7,
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer { rotationZ = arrowRotation }
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandIn(expandFrom = Alignment.TopStart) + fadeIn(),
            exit = shrinkOut(shrinkTowards = Alignment.TopStart) + fadeOut()
        ) {
            Box(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 10.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun ZoomButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(white)
            .border(1.dp, grey_2, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = description, tint = black_2, modifier = Modifier.size(20.dp))
    }
}

// ================= Детали =================

/**
 * Детали продажи или списания — тот же лист, что открывается по нажатию на позицию в разделе.
 * `isArchive = true` прячет кнопки изменения и удаления: доска только для просмотра.
 */
@Composable
private fun TraceRecordDetailSheet(record: TraceRecord, onDismiss: () -> Unit) {
    when (record) {
        is TraceRecord.Sale -> {
            val sale = record.sale
            DetailSectionBottomSheet(
                title = sale.title,
                count = sale.count,
                countSuffix = sale.countSuffix,
                price = sale.price,
                priceAll = sale.priceAll,
                priceSuffix = sale.priceSuffix,
                category = sale.category,
                productOrigin = sale.productOrigin,
                buyer = sale.buyer ?: stringResource(R.string.animal_card_screen_sale_note_no_buyer),
                date = dateBuilder(sale.day, stringResource(monthToResString(sale.month)), sale.year),
                note = sale.note,
                animalCountId = sale.animalCountId,
                iconColor = blue_1,
                boxColor = Color(0xFFEFF6FF),
                colors = listOf(blue_1, blue_2),
                isArchive = true,
                onUpdateClick = {},
                onDeleteClick = {},
                onDismissRequest = onDismiss
            )
        }

        is TraceRecord.WriteOff -> {
            val writeOff = record.writeOff
            DetailSectionBottomSheet(
                title = writeOff.title,
                count = writeOff.count,
                countSuffix = writeOff.countSuffix,
                price = writeOff.price,
                priceAll = writeOff.priceAll,
                priceSuffix = writeOff.priceSuffix ?: Suffix.RUBLE,
                category = writeOff.category,
                productOrigin = writeOff.productOrigin,
                date = dateBuilder(writeOff.day, stringResource(monthToResString(writeOff.month)), writeOff.year),
                note = writeOff.note,
                // У сырья переработки статус «свои нужды/утиль» не имеет смысла
                statusWriteOff = writeOff.status.takeIf { writeOff.recyclingId == null },
                animalCountId = writeOff.animalCountId,
                iconColor = violet_1,
                boxColor = Color(0xFFFAF5FF),
                colors = listOf(violet_1, violet_2),
                isArchive = true,
                onUpdateClick = {},
                onDeleteClick = {},
                onDismissRequest = onDismiss
            )
        }
    }
}

@Composable
private fun TraceNodeDetailsSheet(
    node: TraceNode,
    onOpenBoard: (Triple<Long, String, Suffix>) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryIcon(node.category)
                Column {
                    Text(text = node.title, style = textBold_18, color = black_2)
                    Text(text = node.value, style = text_14, color = gray_7)
                }
            }
            if (node.details.isNotEmpty()) {
                HorizontalDivider(color = grey_2)
                node.details.forEach { detail ->
                    Column {
                        Text(text = detail.label, style = text_12, color = gray_7)
                        Text(text = detail.value, style = text_14, color = black_2)
                    }
                }
            }
            node.targetProduct?.let { target ->
                Spacer(Modifier.height(4.dp))
                GradientButton(
                    modifier = Modifier.fillMaxWidth(),
                    colors = listOf(node.category.color, node.category.color),
                    text = stringResource(R.string.trace_board_open_board),
                    postfixIconRes = R.drawable.baseline_arrow_outward_24,
                    paddingValues = PaddingValues(vertical = 14.dp),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onOpenBoard(target) }
                    }
                )
            }
        }
    }
}
