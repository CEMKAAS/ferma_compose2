@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.count

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.text_16
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.remember
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import com.zaroslikov.fermacompose2.ui.elements.ExpandableRows
import androidx.compose.ui.semantics.Role
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.GroupSexCountFields
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.animal_2
import com.zaroslikov.fermacompose2.gray_4
import com.zaroslikov.fermacompose2.green_15
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toIndicationStatus
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CountColorGradientCard
import com.zaroslikov.fermacompose2.ui.elements.FabMenuEntries
import com.zaroslikov.fermacompose2.ui.elements.FabMenuEntry
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalEntryBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListIntent
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.empty_list.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.textBold_18
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard.GroupConfirmDialog
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.MergeAnimalsBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.AnimalCountCardNew
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.AnimalCountIntent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.BottomSheetKillAnimal
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.BottomSheetSaleAnimal
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.BottomSheetWriteOffAnimal
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalParameter
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.IconAnimal
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.PregnancyHeartCard
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.PregnancyRingIcon

object AnimalHerdCountDestination : NavigationDestination {
    override val route = "animalHerdCount"
    override val titleRes = R.string.animal_herd_count_title
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

/**
 * Корректировка поголовья сборной группы: состав (добавить, вывести), продажа / списание /
 * забой / приход по выбранным членам (FAB), обезличивание и расформирование, история поголовья.
 */
@Composable
fun AnimalHerdCountScreen(
    navigateBack: () -> Unit,
    onNavigateAnimalCard: (Pair<Long, Long>) -> Unit,
    viewModel: AnimalHerdCountViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var tab by rememberSaveable { mutableIntStateOf(TAB_HEADS) }

    // Срабатывает и когда экран вернулся на вершину стека после закрытия в фоне
    LaunchedEffect(state.isClosed) {
        if (state.isClosed) navigateBack()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.animal_herd_count_title,
                onNavigateBackClick = navigateBack,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            // Отмечен хоть один член — действия над отмеченными; выезжают снизу и уезжают обратно.
            // Число запоминаем, чтобы во время скрытия панель не показывала «Выбрано: 0»
            var shownCount by remember { mutableIntStateOf(0) }
            if (state.selected.isNotEmpty()) shownCount = state.selected.size
            AnimatedVisibility(
                visible = !state.isLoading && !state.isArchive && state.selected.isNotEmpty() && tab == TAB_HEADS,
                enter = slideInVertically(animationSpec = tween(250)) { it } + fadeIn(tween(250)),
                exit = slideOutVertically(animationSpec = tween(200)) { it } + fadeOut(tween(200))
            ) {
                SelectionActionsBar(
                    count = shownCount,
                    enabled = !state.isOperationRunning,
                    onIntent = viewModel::onIntent
                )
            }
        },
        floatingActionButton = {
            if (!state.isLoading && !state.isArchive && state.selected.isEmpty())
                FabMenuEntries(
                    items = listOf(
                        // Новое животное — та же форма, что в списке животных; с ценой — покупка
                        FabMenuEntry(
                            title = stringResource(R.string.button_add),
                            iconRes = AnimalCountVersion.ADD.toDrawRes(),
                            colors = AnimalCountVersion.ADD.toColorList(),
                            onClick = { viewModel.onAnimalEntryIntent(AnimalListIntent.OpenBottomSheetEntry(true)) }
                        ),
                        // Существующие самостоятельные животные
                        FabMenuEntry(
                            title = stringResource(R.string.animal_herd_add_members),
                            iconRes = R.drawable.ic_herd,
                            colors = listOf(animal_1, animal_2),
                            onClick = { viewModel.onIntent(AnimalHerdCountIntent.OpenAddMembersSheet(true)) }
                        )
                    )
                )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(modifier = Modifier.padding(innerPadding))
        else
            Column(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                PrimaryTabRow(selectedTabIndex = tab) {
                    HERD_COUNT_TABS.forEachIndexed { index, titleRes ->
                        Tab(
                            selected = tab == index,
                            onClick = { tab = index },
                            text = { Text(stringResource(titleRes), style = text_14, fontWeight = FontWeight.SemiBold) },
                            selectedContentColor = animal_1,
                            unselectedContentColor = marengo
                        )
                    }
                }
                val contentModifier = Modifier
                    .weight(1f)
                    .modifierScreen(PaddingValues())
                if (tab == TAB_HEADS)
                    AnimalHerdCountContainer(
                        modifier = contentModifier,
                        state = state,
                        onIntent = viewModel::onIntent,
                        onMemberClick = { onNavigateAnimalCard(state.herd.idPT to it) }
                    )
                else
                    Column(modifier = contentModifier) { HerdHistory(state = state) }
            }
    }

    if (state.animalEntry.openBottomSheetEntry)
        AnimalEntryBottomSheet(
            colors = listOf(animal_1, animal_2),
            state = state.animalEntry.currentProduct,
            onIntent = viewModel::onAnimalEntryIntent,
            priceSuffix = state.settings.currencySuffix
        )

    if (state.entry.isOpenEntryBottomSheet)
        HerdEntryBottomSheet(state = state, onIntent = viewModel::onIntent, onEntryIntent = viewModel::onEntryIntent)

    if (state.isOpenLeaveDialog)
        GroupConfirmDialog(
            titleRes = R.string.animal_herd_leave_title,
            iconRes = R.drawable.ic_herd_leave,
            text = stringResource(R.string.animal_herd_leave_text, state.selectedMembers.joinToString { it.name }),
            textButtonRes = R.string.animal_group_button_leave,
            enabled = !state.isOperationRunning,
            onDismissRequest = { viewModel.onIntent(AnimalHerdCountIntent.OpenLeaveDialog(false)) },
            onConfirm = { viewModel.onIntent(AnimalHerdCountIntent.LeaveConfirmed) }
        )
    if (state.merge.isOpen)
        MergeAnimalsBottomSheet(
            state = state.merge,
            colors = listOf(animal_1, animal_2),
            onDismissRequest = { viewModel.onIntent(AnimalHerdCountIntent.OpenAddMembersSheet(false)) },
            onCandidateToggled = { viewModel.onIntent(AnimalHerdCountIntent.MergeCandidateToggled(it)) },
            // Режим «только добавить»: выбор группы и названия скрыт
            onModeChanged = {},
            onGroupChosen = {},
            onNameChanged = {},
            onConfirm = { viewModel.onIntent(AnimalHerdCountIntent.MergeConfirmed) }
        )
}

@Composable
private fun AnimalHerdCountContainer(
    modifier: Modifier,
    state: AnimalHerdCountState,
    onIntent: (AnimalHerdCountIntent) -> Unit,
    onMemberClick: (Long) -> Unit
) {
    val enabled = !state.isArchive && !state.isOperationRunning
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HerdHeadsCard(state = state)
        HerdCountMembers(
            state = state,
            enabled = enabled,
            onSelect = { onIntent(AnimalHerdCountIntent.MemberSelected(it)) },
            onMemberClick = onMemberClick
        )
        // Место под FAB
        Spacer(Modifier.height(64.dp))
    }
}

private const val TAB_HEADS = 0
private val HERD_COUNT_TABS = listOf(R.string.animal_herd_tab_heads, R.string.animal_herd_tab_history)

/** «Количество голов» и разбивка: беременные, самки, самцы, пол не известен. */
@Composable
private fun HerdHeadsCard(state: AnimalHerdCountState) {
    CardFieldNew {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AnimalParameter(
                titleParameter = R.string.animal_composite_heads,
                parameter = "${state.herd.totalCount.formatNumber(isGroupingUsed = false)} " +
                        stringResource(state.herd.totalSuffix.toResId()),
                icon = R.drawable.baseline_spoke_24,
                iconColor = animal_1,
                iconColorSecond = green_15
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HeadsStat(
                    modifier = Modifier.weight(1f),
                    value = state.pregnantCount.toString(),
                    labelRes = R.string.animal_herd_stat_pregnant,
                    iconRes = R.drawable.baseline_favorite_24,
                    color = Color(0xFFFF2056)
                )
                HeadsStat(
                    modifier = Modifier.weight(1f),
                    value = state.herd.females.toString(),
                    labelRes = R.string.animal_herd_stat_females,
                    iconRes = R.drawable.baseline_female_24,
                    color = Color(0xFFF6339A)
                )
                HeadsStat(
                    modifier = Modifier.weight(1f),
                    value = state.herd.males.toString(),
                    labelRes = R.string.animal_herd_stat_males,
                    iconRes = R.drawable.baseline_male_24,
                    color = Color(0xFF2B7FFF)
                )
                HeadsStat(
                    modifier = Modifier.weight(1f),
                    value = state.unknownSexCount.formatNumber(isGroupingUsed = false),
                    labelRes = R.string.animal_herd_stat_unknown,
                    iconRes = R.drawable.icon_unknow,
                    color = grey
                )
            }
        }
    }
}

@Composable
private fun HeadsStat(
    modifier: Modifier,
    value: String,
    @StringRes labelRes: Int,
    @DrawableRes iconRes: Int,
    color: Color
) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(painterResource(iconRes), contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Text(value, style = textBold_16, color = color, maxLines = 1)
        Text(
            stringResource(labelRes),
            style = text_12,
            color = marengo,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * «Состав группы»: число членов, «+ Добавить в группу», слева у каждого — флажок: отмеченным
 * доступны продажа, списание, забой и вывод из группы (панель внизу экрана).
 */
@Composable
private fun HerdCountMembers(
    state: AnimalHerdCountState,
    enabled: Boolean,
    onSelect: (Long) -> Unit,
    onMemberClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stringResource(R.string.animal_composite_members_title), style = textBold_18)
                Text(state.herd.members.size.toString(), style = textBold_18, color = grey)
            }
        }
        CardFieldNew(padding = PaddingValues(0.dp)) {
            // Весь состав сразу, без «Показать все»
            state.herd.members.forEachIndexed { index, member ->
                if (index > 0) HorizontalDivider(thickness = 1.dp, color = gray_4)
                val isSelected = member.id in state.selected
                MemberRow(
                    member = member,
                    modifier = Modifier
                        // Отмеченный — строка подсвечена цветом раздела
                        .background(if (isSelected) animal_1.copy(alpha = 0.08f) else Color.Transparent)
                        .clickable { onMemberClick(member.id) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    leading = if (state.isArchive) null else {
                        {
                            RoundCheckbox(
                                checked = isSelected,
                                enabled = enabled,
                                onClick = { onSelect(member.id) }
                            )
                        }
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.outline_keyboard_arrow_right_24),
                        contentDescription = null,
                        tint = grey
                    )
                }
            }
        }
    }
}

/** Круглый флажок: пустой — серое кольцо, отмеченный — залитый круг цвета раздела с галочкой. */
@Composable
private fun RoundCheckbox(checked: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .then(
                if (checked) Modifier.background(animal_1)
                else Modifier.border(1.5.dp, grey.copy(alpha = 0.5f), CircleShape)
            )
            .clickable(enabled = enabled, role = Role.Checkbox, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (checked)
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
    }
}

/** Иконка, имя, пол (индивидуальное животное) или поголовье (обезличенная группа), вид. */
@Composable
private fun MemberRow(
    member: AnimalListUi,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        leading?.invoke()
        // Беременность — кольцо срока вплотную к иконке, как в карточке группы
        PregnancyRingIcon(
            progress = member.pregnancyProgress,
            ringSize = MEMBER_ICON_SIZE + MEMBER_RING_STROKE * 2,
            strokeWidth = MEMBER_RING_STROKE
        ) {
            IconAnimal(
                sizeIcon = MEMBER_ICON_SIZE,
                sex = member.sex,
                imagePath = member.imagePath,
                currentIcon = member.currentIcon
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = member.name,
                    style = textBold_16,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (member.sex != null) {
                    CountColorGradientCard(sex = member.sex)
                    member.pregnancyProgress?.let { PregnancyHeartCard(it) }
                } else Text(
                    "${member.totalCount.formatNumber(isGroupingUsed = false)} ${stringResource(member.suffix.toResId())}",
                    style = text_14,
                    color = marengo
                )
            }
            if (member.type.isNotBlank())
                Text(
                    text = listOfNotNull(member.type, member.breed).joinToString(" · "),
                    style = text_12,
                    color = marengo,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
        }
        trailing()
    }
}

/** История поголовья: записи членов с меткой группы, только просмотр. */
@Composable
private fun HerdHistory(state: AnimalHerdCountState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(stringResource(R.string.animal_herd_history_title), style = textBold_18)
        if (state.history.isEmpty())
            Text(stringResource(R.string.animal_herd_history_empty), style = text_14, color = marengo)
        state.history.forEach { item ->
            val record = item.record
            val version = record.version ?: AnimalCountVersion.ADD
            AnimalCountCardNew(
                icon = version.toDrawRes(),
                colors = version.toColorList(),
                value = record.count,
                suffix = record.suffix,
                price = record.priceAll ?: record.price,
                currencyPriceSuffix = state.settings.currencySuffix,
                date = record.date,
                // Кого коснулась запись — первой строкой примечания
                note = listOf(item.animalName, record.note).filter { it.isNotBlank() }.joinToString("\n"),
                // Записи меняются в карточке животного: здесь только история
                isArchive = true,
                onEditClick = {},
                onDeleteClick = {},
                indicationStatus = version.toIndicationStatus(),
                productKill = record.productKill,
                femalesCount = record.femalesCount,
                malesCount = record.malesCount
            )
        }
        // Место под FAB
        Spacer(Modifier.height(64.dp))
    }
}

/** Лист операции: поля счётчика животного, вместо поля количества — выбор членов. */
@Composable
private fun HerdEntryBottomSheet(
    state: AnimalHerdCountState,
    onIntent: (AnimalHerdCountIntent) -> Unit,
    onEntryIntent: (AnimalCountIntent) -> Unit
) {
    // Кнопка «Сохранить» доступна, только если выбор членов корректен
    val item = state.entry.currentProduct.let { it.copy(hasAnyError = it.hasAnyError && state.isPicksValid) }
    val total = state.herd.totalCount.formatNumber(isGroupingUsed = false)
    val picker: @Composable () -> Unit = { HerdMemberPicker(state = state, onIntent = onIntent) }
    val currency = state.settings.currencySuffix
    when (item.version) {
        AnimalCountVersion.SALE -> BottomSheetSaleAnimal(
            countContent = picker,
            state = item,
            onIntent = onEntryIntent,
            countAllAnimal = total,
            currencyPrice = currency
        )

        AnimalCountVersion.WRITE_OFF -> BottomSheetWriteOffAnimal(
            countContent = picker,
            state = item,
            countAllAnimal = total,
            countSuffix = state.herd.totalSuffix,
            onIntent = onEntryIntent,
            currencyPrice = currency
        )

        AnimalCountVersion.KILL -> BottomSheetKillAnimal(
            countContent = picker,
            state = item,
            onIntent = onEntryIntent,
            countAnimalAll = total,
            countSuffix = state.herd.totalSuffix
        )

        else -> Unit
    }
}

/**
 * Члены в листе операции. Убыль — отмеченные в составе (убрать можно, добавить нельзя),
 * у обезличенной группы — сколько голов (не больше её поголовья). Приход — количество
 * у обезличенных групп и новые животные. Внизу — итог.
 */
@Composable
private fun HerdMemberPicker(
    state: AnimalHerdCountState,
    onIntent: (AnimalHerdCountIntent) -> Unit
) {
    val members = state.pickable
    // Карточка в стиле остальных блоков листа (как «Продукция» в забое): рамка, заголовок с иконкой
    BorderCard(modifier = Modifier.fillMaxWidth()) {
        // Приход в группу без обезличенных групп — только новые животные, заголовок «Куда» не нужен
        if (members.isNotEmpty() || state.version.sign < 0)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_herd),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = gray_7
                )
                Text(
                    stringResource(
                        R.string.animal_herd_pick_title,
                        state.pickedTotal.formatNumber(isGroupingUsed = false),
                        stringResource(state.herd.totalSuffix.toResId())
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = text_16,
                    color = dark
                )
            }
        if (members.isEmpty() && state.version.sign < 0)
            Text(stringResource(R.string.animal_herd_pick_empty), style = text_14, color = marengo)
        val pickRow: @Composable (AnimalListUi) -> Unit = { member ->
            MemberRow(member = member) {
                if (member.sex == null) {
                    val isError = state.isPickError(member)
                    OutlinedTextField(
                        modifier = Modifier.width(112.dp),
                        value = state.picks[member.id].orEmpty(),
                        onValueChange = { value ->
                            // Забой — целые головы: лист забоя считает их целым числом
                            val allowed: (Char) -> Boolean = if (state.version == AnimalCountVersion.KILL) Char::isDigit
                            else { c -> c.isDigit() || c == '.' || c == ',' }
                            onIntent(AnimalHerdCountIntent.PickCountChanged(member.id, value.filter(allowed)))
                        },
                        label = { Text(stringResource(R.string.animal_herd_pick_label), style = text_12) },
                        supportingText = {
                            Text(
                                if (isError) stringResource(R.string.animal_herd_pick_error)
                                else if (state.version.sign < 0) stringResource(
                                    R.string.animal_herd_pick_of,
                                    member.totalCount.formatNumber(isGroupingUsed = false)
                                ) else "",
                                style = text_12
                            )
                        },
                        isError = isError,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                // Убыль — только отмеченные: добавить в листе нельзя, можно убрать из списка
                if (state.version.sign < 0)
                    IconButton(onClick = { onIntent(AnimalHerdCountIntent.PickRemoved(member.id)) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.animal_herd_new_remove),
                            tint = grey
                        )
                    }
            }
        }
        // Продажа, списание, забой: первые три выбранных, остальные — по кнопке
        // Между животными — разделители
        if (members.isNotEmpty())
            Column(modifier = Modifier.fillMaxWidth()) {
                val dividedRow: @Composable (Int, AnimalListUi) -> Unit = { index, member ->
                    if (index > 0) HorizontalDivider(thickness = 1.dp, color = grey_2)
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        pickRow(member)
                        // Обезличенная группа: сколько из уходящих самок и самцов
                        if (member.sex == null) {
                            val (females, males) = state.pickSex[member.id] ?: ("" to "")
                            GroupSexCountFields(
                                females = females,
                                males = males,
                                onFemalesChange = { onIntent(AnimalHerdCountIntent.PickSexChanged(member.id, it, males)) },
                                onMalesChange = { onIntent(AnimalHerdCountIntent.PickSexChanged(member.id, females, it)) },
                                isError = state.isPickSexError(member),
                                infoRes = R.string.animal_group_sex_op_info,
                                errorRes = R.string.animal_group_sex_op_error
                            )
                        }
                    }
                }
                ExpandableRows(items = members, spacing = 0.dp, row = dividedRow)
            }
    }
}


/** Панель отмеченных членов: сколько выбрано, снять выбор; продать, списать, забой, вывести. */
@Composable
private fun SelectionActionsBar(
    count: Int,
    enabled: Boolean,
    onIntent: (AnimalHerdCountIntent) -> Unit
) {
    Surface(shadowElevation = 8.dp, color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.animal_herd_selected, count),
                    style = textBold_16,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { onIntent(AnimalHerdCountIntent.SelectionCleared) }) {
                    Text(stringResource(R.string.animal_herd_selection_clear), style = text_14, color = marengo)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(AnimalCountVersion.SALE, AnimalCountVersion.WRITE_OFF, AnimalCountVersion.KILL).forEach { version ->
                    SelectionAction(
                        modifier = Modifier.weight(1f),
                        iconRes = version.toDrawRes(),
                        text = stringResource(version.toResId()),
                        color = version.toColorList().first(),
                        enabled = enabled
                    ) { onIntent(AnimalHerdCountIntent.SelectionActionClicked(version)) }
                }
                SelectionAction(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.ic_herd_leave,
                    text = stringResource(R.string.animal_herd_selection_leave),
                    color = grey,
                    enabled = enabled
                ) { onIntent(AnimalHerdCountIntent.OpenLeaveDialog(true)) }
            }
        }
    }
}

@Composable
private fun SelectionAction(
    modifier: Modifier,
    @DrawableRes iconRes: Int,
    text: String,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(painterResource(iconRes), contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        Text(text, style = text_12, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private val MEMBER_ICON_SIZE = 40.dp
private val MEMBER_RING_STROKE = 3.dp
