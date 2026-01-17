@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.start.StartScreen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart2
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_11
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.orang_3
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DrawerSheetNew
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextNew
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.InventoryBody
import kotlinx.coroutines.launch


object StartDestination : NavigationDestination {
    override val route = "Start"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    navigateToItemProject: (Long) -> Unit,
    navigateToProject: (Long) -> Unit,
    navigateToAboutApp: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToIncubator: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StartScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(price_green, green_9)
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val drawerClose = {
        scope.launch {
            drawerState.apply {
                if (isClosed) open() else close()
            }
        }
    }


    /*  val bottomSheetClose = {
          scope.launch {
              bottomSheetState.apply {
                  if (isVisible) is
              }
          }
      }*/

    /* var infoBottomSheet by remember { mutableStateOf(false) }
     var showDialogTime by remember { mutableStateOf(false) }
     var arhivBoolean by remember { mutableStateOf(false) }*/


    /*if (showDialogTime) {
        TimePicker(time = if (viewModel.time == "") "20:00" else viewModel.time, showDialog = {
            viewModel.onUpdate(time1 = it)
            showDialogTime = false
        })
    }*/

    /*AlterDialigStart(
        isFirstStart = isFirstStart,
        dialogTitle = "Главный экран",
        dialogText = "Здесь отображаются текущие и архивные проекты. В нижнем правом углу можно добавить новый проект, а в верхнем углу находятся настройки. Перейдем к созданному проекту.\nДля получения дополнительной информации по использованию приложения обращайтесь в нашу группу ВКонтакте.\n" +
                "\nУдачи.",
        textAppMetrica = "Окончание обучения",
        isFirstEndConfig = isFirstEnd
    )*/

    var showBottomSheet by remember { mutableStateOf(false) }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheetNew(
                onProfileClick = { },
                onSettingsClick = { navigateToSettings() },
                onAboutAppClick = { navigateToAboutApp() },
                onCloseClick = { drawerClose() }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBarStart2(
                    title = R.string.start_screen_title,
                    infoBottomSheet = { drawerClose() },
                    archiveButton = {},
                    boolean = true
                )
            },
            floatingActionButton = {
                NeonGlowFab(
                    colors = colors,
                    onClick = { showBottomSheet = true })
            }
        ) { innerPadding ->

            if (state.isLoading)
                CircularProgress(
                    modifier = modifier.padding(innerPadding),
                )
            else
                StartScreenContainer2(
                    modifier = Modifier.modifierScreenLazy(innerPadding),
                    itemList = state.list,
                    brieflyList = state.list,
                    onEditClick = { navigateToProject(it) },
                    onArchiveClick = { viewModel.onIntent(StartScreenIntent.ArchiveClicked(it)) },
                    onDeleteClick = { viewModel.onIntent(StartScreenIntent.DeleteClicked(it)) },
                    onNavigationProject = { navigateToItemProject(it) },
                )

            if (showBottomSheet)
                ChoiceProjectBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    onIncubatorProject = { navigateToIncubator(-1) },
                    onAddProject = { navigateToProject(-1) }
                )
        }
    }
}

@Composable
private fun StartScreenContainer2(
    modifier: Modifier = Modifier,
    itemList: List<DomainProjectTable>,
    brieflyList: List<DomainProjectTable>,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (DomainProjectTable) -> Unit,
    onArchiveClick: (DomainProjectTable) -> Unit,
    onNavigationProject: (Long) -> Unit
) {
    InventoryBody(
        modifier = modifier,
        details = true,
        itemList = itemList,
        searchList = itemList,
        brieflyList = brieflyList,
        onInsertClick = {},
        onEditClick = { },
        onDeleteClick = {},
        onDetailsClick = {},
        detailCard = { index, item ->
            ProjectCard(
                projectTable = item,
                onEditClick = { onEditClick(item.id) },
                onDeleteClick = { onDeleteClick(item) },
                onArchiveClick = { onArchiveClick(item) },
                onNavigationProject = { onNavigationProject(item.id) }
            )
        },
        brieflyCard = {},
        titleRes = R.string.start_screen_positions_message_no_date_title,
        messageRes = R.string.start_screen_positions_message_no_date_message,
        supportRes = R.string.start_screen_positions_message_no_date_support_text,
        buttonRes = R.string.start_screen_positions_button_note_message_no_data
    )
}

@Composable
private fun ProjectCard(
    projectTable: DomainProjectTable,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onNavigationProject: () -> Unit
) {
    CardFieldNew(
        onClick = onNavigationProject
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconTransaction2(
                    image = painterResource(R.drawable.livestock),
                    color = price_green_2,
                    sizeCard = 64.dp,
                    isPainter = true
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = projectTable.titleProject,
                        style = text_16,
                        color = black_2
                    )
                    IconAndTextNew(
                        iconRes = R.drawable.baseline_calendar_month_24,
                        valueString = projectTable.data,
                        iconColor = gray_7
                    )
                }
            }
            DropdownMenuEdit(
                onEditClick = onEditClick,
                onArchiveClick = onArchiveClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun ChoiceProjectBottomSheet(
    onDismissRequest: () -> Unit,
    onAddProject: () -> Unit,
    onIncubatorProject: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.start_screen_create_new_project),
                    style = text_16,
                    color = black_2
                )
                Text(
                    stringResource(R.string.start_screen_choice_type_project),
                    style = text_14,
                    color = gray_7
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChoiceProjectCard(
                    titleRes = R.string.start_screen_common_project,
                    supportText = stringResource(R.string.start_screen_common_project_support),
                    icon = R.drawable.outline_work_24,
                    color = green_8,
                    colorIcon = green_9,
                    borderColor = green_11,
                    containerColor = price_green_2
                ) { onAddProject() }
                ChoiceProjectCard(
                    titleRes = R.string.start_screen_incubator_project,
                    supportText = stringResource(R.string.start_screen_incubator_project_support),
                    icon = R.drawable.outline_egg_24,
                    color = orang_5,
                    colorIcon = orang_6,
                    borderColor = orang_5,
                    containerColor = orang_3
                ) { onIncubatorProject() }
            }
        }
    }
}

@Composable
private fun ChoiceProjectCard(
    @StringRes titleRes: Int,
    supportText: String,
    @DrawableRes icon: Int,
    color: Color,
    colorIcon: Color,
    borderColor: Color,
    containerColor: Color,
    onClick: () -> Unit
) {
    BorderCard(
        borderColor = borderColor,
        containerColor = containerColor,
        padding = PaddingValues(16.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconTransaction2(
                    sizeCard = 48.dp,
                    icon = icon,
                    colorIcon = colorIcon,
                    color = color
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(stringResource(titleRes), style = text_16, color = black_2)
                    Text(supportText, style = text_12, color = gray_7)
                }
            }
            Icon(
                painterResource(R.drawable.baseline_chevron_right_24),
                contentDescription = null,
                tint = grey
            )
        }
    }
}


/*@Composable
private fun ProjectCard(
    projectTable: DomainProjectTable
) {
    CardFieldNew() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        color = if (projectTable.arhive == "0") MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.5f
                        ),
                        shape = CircleShape
                    )
            ) {
                if (projectTable.imageData != null) {
                    *//*    Image(
                            bitmap = projectTable.imageData,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.align(Alignment.Center),
                            colorFilter = if (projectTable.arhive == "0") null else ColorFilter.tint(
                                Color.Gray
                            )
                        )*//*
                } else {
                    Image(
                        painter = painterResource(setImage(projectTable)),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(194.dp),
                        colorFilter = if (projectTable.arhive == "0") null else ColorFilter.tint(
                            Color.Gray
                        )
                    )
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = projectTable.titleProject,
                    modifier = Modifier
                        .padding(horizontal = 3.dp, vertical = 6.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 26.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.icon_date_range), contentDescription = "Дата")
                    Text(
                        text = if (projectTable.arhive == "0") projectTable.data else "Завершен",
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                    )
                }
            }
        }
    }
}*/


/*
@Composable
fun StartScreenContainer(
    modifier: Modifier,
    navigateToItemProject: (Int) -> Unit,
    navigateToItemIncubator: (Int) -> Unit,
    navigateToItemProjectArh: (Int) -> Unit,
    navigateToItemIncubatorArh: (Int) -> Unit,
    projectList: List<ProjectTableStartScreen>,
    navigationToNewYear: () -> Unit,
    navController: NavController,
    arhivBoolean: Boolean
) {

    if (projectList.isNotEmpty()) {
        Column(modifier = modifier) {

            */
/*  if (newYearBoolean()) {
                  Button(
                      onClick = {
                          navigationToNewYear()
                          AppMetrica.reportEvent("Итоги года общий")
                      },
                      modifier = Modifier
                          .fillMaxWidth()
                          .padding(vertical = 5.dp, horizontal = 15.dp)
                  ) {
                      Text(text = "Итоги года!")
                  }
              }*//*


            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                items(items = projectList, key = { it.id }) {
                    if (arhivBoolean && it.arhive == "1" || !arhivBoolean && it.arhive == "0") {
                        CardFerma(
                            projectTable = it, modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    when (it.arhive) {
                                        "1" -> {
                                            if (it.mode == 0) navigateToItemIncubatorArh(it.id)
                                            else navigateToItemProjectArh(it.id)
                                        }

                                        "0" -> {
                                            if (it.mode == 0) navigateToItemIncubator(it.id)
                                            else navigateToItemProject(it.id)
                                        }
                                    }
                                }
                        )
                    }
                }
            }
        }
    } else {
        Column(modifier = modifier.padding(10.dp)) {
            Text(
                text = "Добро пожаловать!\nМое Хозяйство 2",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                fontSize = 20.sp,
            )
            Text(
                text = "Здесь Вы сможете увидеть все Ваши проекты (инкубаторы и хозяйства). У Вас есть возможность создавать несколько проектов, каждый из которых будет иметь свою уникальную экономику и склад и д.р., что позволит эффективно управлять каждым Вашим бизнесом!",
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                fontSize = 20.sp,
            )
            Text(
                text = "Сейчас нет проектов:(\nНажмите + чтобы добавить\nили ",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
            )
            Button(
                onClick = {
                    navController.navigate(
//                    ChoiseProjectDestination.route
                        ProjectAddDestination.route
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)

            ) {
                Text(text = "Добавить проект!")
            }
        }
    }
}
*/
/*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(
    infoBottomSheet: () -> Unit,
    sheetState: SheetState,
    time: String,
    showDialogTime: () -> Unit,
    saveBottomSheet: () -> Unit,
    clearTime: () -> Unit
) {

//    val anonotatedString = buildAnnotatedString {
//        pushStringAnnotation(tag = "URL", annotation = "https://vk.com/myfermaapp")
//        withStyle(
//            style = SpanStyle(
//                color = Color.Blue,
//                fontSize = 20.sp
//            )
//        ) {
//            append("Присоединиться!")
//        }
//        pop()
//    }

    ModalBottomSheet(
        onDismissRequest = infoBottomSheet,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(
                text = "Мое Хозяйство v2.17a",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Привет, дорогой друг!",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Присоединяйcя к нашей группе ВКонтакте! Это отличный способ оставаться в курсе новостей, делиться впечатлениями и предлагать идеи для улучшения приложения. Давайте вместе сделаем Ваше хозяйство еще лучше!",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 15.sp,
                textAlign = TextAlign.Justify
            )

//            val uriHandler = LocalUriHandler.current
//            ClickableText(
//                text = anonotatedString,
//                style = TextStyle(
//                    textAlign = TextAlign.Justify
//                ),
//                onClick = { offset ->
//                    val uri = anonotatedString.getStringAnnotations(
//                        tag = "URL",
//                        start = offset,
//                        end = offset
//                    ).firstOrNull()?.item
//                    if (uri != null) {
//                        uriHandler.openUri(uri)
//                    }
//                },
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .padding(vertical = 15.dp),
//            )
            val context = LocalContext.current
            TextButton(
                onClick = {
                    AppMetrica.reportEvent("Переход в группу из инфо")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/myfermaapp"))
                    context.startActivity(intent)
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Присоединиться!", fontSize = 20.sp)
            }

            OutlinedTextField(
                value = time,
                onValueChange = {},
                readOnly = true,
                label = { Text("Уведомление") },
                supportingText = {
                    Text("Чтобы отключить ежедневные уведомления, нажмите на \"Х\" и \"Спасибо!\"")
                },
                leadingIcon = {
                    IconButton(onClick = showDialogTime) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_access_time_24),
                            contentDescription = "Показать меню"
                        )
                    }
                },
                trailingIcon = {
                    IconButton(onClick = clearTime) {
                        Icon(
                            painterResource(R.drawable.baseline_clear_24),
                            contentDescription = "Удалить"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDialogTime() }
                    .padding(vertical = 5.dp)
            )


            Button(
                onClick = saveBottomSheet, modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)

            ) {
                Text(text = "Спасибо!")
            }
        }
    }
}


fun setImage(projectTable: ProjectTableStartScreen): Int {
    return if (projectTable.mode == 0) {
        when (projectTable.type) {
            "Курицы" -> R.drawable.chicken
            "Гуси" -> R.drawable.external_goose_birds_icongeek26_outline_icongeek26
            "Перепела" -> R.drawable.quail
            "Утки" -> R.drawable.duck
            "Индюки" -> R.drawable.turkeycock
            else -> R.drawable.chicken
        }
    } else {
        return R.drawable.livestock
    }
}*/
