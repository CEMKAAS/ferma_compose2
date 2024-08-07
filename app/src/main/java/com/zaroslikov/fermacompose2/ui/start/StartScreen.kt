package com.zaroslikov.fermacompose2.ui.start

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart2
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.ChoiseProjectDestination
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit


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
    modifier: Modifier = Modifier,
    viewModel: StartScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val projectListArh by viewModel.getAllProjectArh.collectAsState()
    val projectListAct by viewModel.getAllProjectAct.collectAsState()
    val infoBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    Scaffold(
        topBar = {
            TopAppBarStart2(
                title = "Мое Хозяйство",
                infoBottomSheet = infoBottomSheet
            )
        }, floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(ChoiseProjectDestination.route) },
                icon = { Icon(Icons.Filled.Add, "Localized description") },
                text = { Text(text = "Добавить") },
            )
        }
    ) { innerPadding ->
        StartScreenContainer(
            modifier = Modifier.padding(innerPadding),
            onItemClick = navigateToItemProject,
            projectListArh = projectListArh.projectList,
            projectListAct = projectListAct.projectList,
            navigateToItemIncubator = navigateToItemIncubator,
            navigateToItemProjectArh = navigateToItemProjectArh,
            navigateToItemIncubatorArh = navigateToItemIncubatorArh,
            infoBottomSheet = infoBottomSheet,
            sheetState = sheetState
        )
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StartScreenContainer(
    modifier: Modifier,
    onItemClick: (Int) -> Unit,
    navigateToItemIncubator: (Int) -> Unit,
    navigateToItemProjectArh: (Int) -> Unit,
    navigateToItemIncubatorArh: (Int) -> Unit,
    projectListArh: List<ProjectTable>,
    projectListAct: List<ProjectTable>,
    infoBottomSheet: MutableState<Boolean>,
    sheetState: SheetState
) {
    var state by remember { mutableStateOf(0) }
    val titles = listOf("Действующие", "Архив")
    val pagerState = rememberPagerState {
        titles.size
    }

    if (projectListAct.isEmpty() && projectListArh.isEmpty()) {
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
                text = "Здесь Вы сможете увидеть все Ваши проекты (инкубаторы и хозяйства). Теперь у Вас есть возможность создавать несколько проектов, каждый из которых будет иметь свою уникальную экономику и склад и д.р., что позволит эффективно управлять каждым вашим бизнесом!",
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                fontSize = 20.sp,
            )
            Text(
                text = "Сейчас нет проектов:(\nНажмите + чтобы добавить",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
            )
        }
    } else {
        Column(modifier = modifier) {

            LaunchedEffect(key1 = state) {
                pagerState.animateScrollToPage(state)
            }
            LaunchedEffect(key1 = pagerState.currentPage) {
                state = pagerState.currentPage
            }
            Column {

                TabRow(selectedTabIndex = state) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp)
                    ) {

                        when (state) {
                            0 -> {
                                items(items = projectListAct, key = { it.id }) {
                                    if (it.mode == 0) {
                                        CardIncubator(
                                            projectTable = it, modifier = Modifier
                                                .padding(8.dp)
                                                .clickable {
                                                    navigateToItemIncubator(it.id)
                                                },
                                            colorFilter = null
                                        )
                                    } else {
                                        CardFerma(
                                            projectTable = it, modifier = Modifier
                                                .padding(8.dp)
                                                .clickable {
                                                    onItemClick(it.id)
                                                },
                                            colorFilter = null
                                        )
                                    }
                                }
                            }

                            1 -> {
                                items(items = projectListArh, key = { it.id }) {
                                    if (it.mode == 0) {
                                        CardIncubator(
                                            projectTable = it, modifier = Modifier
                                                .padding(8.dp)
                                                .clickable {
                                                    navigateToItemIncubatorArh(it.id)
                                                },
                                            colorFilter = ColorFilter.tint(Color.Gray)
                                        )
                                    } else {
                                        CardFerma(
                                            projectTable = it, modifier = Modifier
                                                .padding(8.dp)
                                                .clickable {
                                                    navigateToItemProjectArh(it.id)
                                                },
                                            colorFilter = ColorFilter.tint(Color.Gray)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
    if (infoBottomSheet.value) {
        InfoBottomSheet(
            infoBottomSheet = infoBottomSheet,
            sheetState = sheetState
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(
    infoBottomSheet: MutableState<Boolean>,
    sheetState: SheetState
) {

    val anonotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = 20.sp,
            )
        ) {
            append("Не упусти возможность присоединиться к нашей ")
        }
        pushStringAnnotation(tag = "URL", annotation = "https://vk.com/myfermaapp")
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                fontSize = 20.sp,

                )
        ) {
            append("группе в ВКонтакте!")
        }
        pop()
    }


    ModalBottomSheet(
        onDismissRequest = { infoBottomSheet.value = false },
        sheetState = sheetState
    ) {

        Column(modifier = Modifier.padding(15.dp)) {

            Text(
                text = "Мое Хозяйство v2.00",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Привет, дорогой друг!",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            val uriHandler = LocalUriHandler.current

            ClickableText(
                text = anonotatedString,
                style = TextStyle(
                    textAlign = TextAlign.Justify
                ),
                onClick = { offset ->
                    val uri = anonotatedString.getStringAnnotations(
                        tag = "URL",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.item
                    if (uri != null) {
                        uriHandler.openUri(uri)
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Text(
                text = "Это отличный способ быть в курсе всех новостей, делиться своими впечатлениями и предлагать идеи для улучшения приложения. Давайте вместе сделаем Ваше хозяйство ещё лучше!",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                textAlign = TextAlign.Justify
            )

            Button(
                onClick = {
                    infoBottomSheet.value = false
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 35.dp)

            ) {
                Text(text = "Спасибо!")
            }
        }
    }
}


@Composable
fun CardIncubator(
    projectTable: ProjectTable, modifier: Modifier = Modifier,
    colorFilter: ColorFilter?
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors()
    ) {
        val imcubCard = setImageIncubatorCard(projectTable)

        Image(
            painter = painterResource(id = imcubCard.image),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(194.dp),
            colorFilter = colorFilter
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
            text = imcubCard.day, fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .padding(bottom = 10.dp)
        )
    }
}

fun setImageIncubatorCard(projectTable: ProjectTable): IncubatorCardImage {
    var day = "Идет 0 день "
    var image = R.drawable.chicken
    when (projectTable.type) {
        "Курицы" -> {
            image = R.drawable.chicken
        }

        "Гуси" -> {
            image = R.drawable.external_goose_birds_icongeek26_outline_icongeek26
        }

        "Перепела" -> {
            image = R.drawable.quail
        }

        "Утки" -> {
            image = R.drawable.duck
        }

        "Индюки" -> {
            image = R.drawable.turkeycock
        }
    }
    if (projectTable.arhive == "0") {
        var diff: Long = 0
        val calendar: Calendar = Calendar.getInstance()
        val dateBefore22: String = projectTable.data
        val dateBefore222: String =
            (calendar.get(Calendar.DAY_OF_MONTH)).toString() + "." + (calendar.get(
                Calendar.MONTH
            ) + 1) + "." + calendar.get(Calendar.YEAR)
        val myFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
        val date1: Date = myFormat.parse(dateBefore22)
        val date2: Date = myFormat.parse(dateBefore222)
        diff = date2.time - date1.time
        day = "Идет ${TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1} день"

    } else {
        day = "Завершён"
    }
    return IncubatorCardImage(image, day)
}

data class IncubatorCardImage(
    val image: Int,
    val day: String
)


@Composable
fun CardFerma(
    projectTable: ProjectTable, modifier: Modifier = Modifier,
    colorFilter: ColorFilter?
) {

    val date = if (colorFilter == null) {
        projectTable.data
    } else {
        "Завершен"
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors()
    ) {
        Image(
            painter = painterResource(id = R.drawable.livestock),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(194.dp),
            colorFilter = colorFilter
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
            text = date, fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .padding(bottom = 10.dp)
        )
    }
}

//fun byteArrayToBitmap(data: ByteArray): Bitmap {
//    return BitmapFactory.decodeByteArray(data, 0, data.size)
//}

//@Preview(showBackground = true)
//@Composable
//fun StartScreenContainerPrewie(
//    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
//    scope: CoroutineScope = rememberCoroutineScope(),
//    navController: NavHostController = rememberNavController(),
//) {
//    StartScreenContainer(
//        modifier = Modifier.fillMaxSize(),
//        navController = navController,
//    )
//}


//@Preview(showBackground = true)
//@Composable
//fun StartScreenPrewie(
//    navController: NavHostController = rememberNavController(),
//    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
//    scope: CoroutineScope = rememberCoroutineScope(),
//
//
//    ) {
//    StartScreen(navController)
//}

//@Preview(showBackground = true)
//@Composable
//fun StartScreenPrewie() {
//
//
//
//}


