package com.zaroslikov.fermacompose2.ui.start

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.zaroslikov.fermacompose2.AlterDialigStart
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart2
import com.zaroslikov.fermacompose2.data.water.ProjectTableStartScreen
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.ChoiseProjectDestination
import com.zaroslikov.fermacompose2.ui.add.incubator.TimePicker
import com.zaroslikov.fermacompose2.ui.warehouse.newYearBoolean
import io.appmetrica.analytics.AppMetrica


object StartDestination : NavigationDestination {
    override val route = "Start"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    navController: NavController,
    navigateToItemProject: (Int) -> Unit,
    navigateToItemIncubator: (Int) -> Unit,
    navigateToItemProjectArh: (Int) -> Unit,
    navigateToItemIncubatorArh: (Int) -> Unit,
    navigationToNewYear: (Pair<Boolean, Int>) -> Unit,
    modifier: Modifier = Modifier,
    isFirstStart: Boolean,
    isFirstEnd: () -> Unit,
    viewModel: StartScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val projectList by viewModel.getAllProject.collectAsState()
    var infoBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDialogTime by remember { mutableStateOf(false) }
    var arhivBoolean by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()



    if (showDialogTime) {
        TimePicker(time = if (viewModel.time == "") "20:00" else viewModel.time, showDialog = {
            viewModel.onUpdate(time1 = it)
            showDialogTime = false
        })
    }

    AlterDialigStart(
        isFirstStart = isFirstStart,
        dialogTitle = "Главный экран",
        dialogText = "Здесь отображаются текущие и архивные проекты. В нижнем правом углу можно добавить новый проект, а в верхнем углу находятся настройки. Перейдем к созданному проекту.\nДля получения дополнительной информации по использованию приложения обращайтесь в нашу группу ВКонтакте.\n" +
                "\nУдачи.",
        textAppMetrica = "Окончание обучения",
        isFirstEndConfig = isFirstEnd
    )

    Scaffold(
        topBar = {
            TopAppBarStart2(
                title = "Мое Хозяйство",
                infoBottomSheet = {
                    infoBottomSheet = true
                    AppMetrica.reportEvent("Информация")
                },
                archiveButton = {
                    arhivBoolean = !arhivBoolean
                },
                boolean = arhivBoolean
            )
        }, floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(ChoiseProjectDestination.route) },
                icon = { Icon(Icons.Filled.Add, "Localized description") },
                text = { Text(text = "Добавить") },
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            StartScreenContainer(
                modifier = Modifier.padding(innerPadding),
                projectList = projectList,
                navigateToItemProject = navigateToItemProject,
                navigateToItemIncubator = navigateToItemIncubator,
                navigateToItemProjectArh = navigateToItemProjectArh,
                navigateToItemIncubatorArh = navigateToItemIncubatorArh,
                navigationToNewYear = { navigationToNewYear(Pair(false, 0)) },
                navController = navController,
                arhivBoolean = arhivBoolean
            )
        }

        if (infoBottomSheet) {
            InfoBottomSheet(
                infoBottomSheet = { infoBottomSheet = false },
                saveBottomSheet = {
                    viewModel.saveItem()
                    AppMetrica.reportEvent("УведОбщ - ${viewModel.time}")
                    infoBottomSheet = false
                },
                sheetState = sheetState,
                time = viewModel.time,
                showDialogTime = { showDialogTime = true },
                clearTime = {
                    viewModel.onUpdate("")
                    AppMetrica.reportEvent("УведОбщ - нет")
                },
            )
        }
    }
}


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

            if (newYearBoolean()) {
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
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp)
            ) {
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
                onClick = { navController.navigate(ChoiseProjectDestination.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)

            ) {
                Text(text = "Добавить проект!")
            }
        }
    }
}

@Composable
fun CardFerma(
    projectTable: ProjectTableStartScreen, modifier: Modifier = Modifier,
) {

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors()
    ) {
        Image(
            bitmap = projectTable.imageData,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(194.dp),
            colorFilter = if (projectTable.arhive == "0") null else ColorFilter.tint(Color.Gray)
        )
        Text(
            text = projectTable.titleProject,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 5.dp)
        )
        Text(
            text = if (projectTable.arhive == "0") projectTable.data else "Завершен",
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .padding(bottom = 10.dp)
        )
    }
}

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
                text = "Мое Хозяйство v2.15а",
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
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Удалить")
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
