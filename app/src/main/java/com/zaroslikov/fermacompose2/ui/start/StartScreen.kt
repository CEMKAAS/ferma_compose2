package com.zaroslikov.fermacompose2.ui.start

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.TopAppBarStart2
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.animal.AddIndicatorsBottomSheet
import com.zaroslikov.fermacompose2.ui.animal.AnimalIndicatorsVM
import com.zaroslikov.fermacompose2.ui.incubator.endInc
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.ChoiseProjectDestination
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.start.add.ProjectAddDestination
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit


object StartDestination : NavigationDestination {
    override val route = "Start"
    override val titleRes = R.string.app_name
}

@Composable
fun StartScreen(
    navController: NavController,
    navigateToItemProject: (Int) -> Unit,
    navigateToItemIncubator: (Int) -> Unit,
    navigateToItemProjectArh: (Int) -> Unit,
    navigateToItemIncubatorArh: (Int) -> Unit,
    viewModel: StartScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val projectListArh by viewModel.getAllProjectArh.collectAsState()
    val projectListAct by viewModel.getAllProjectAct.collectAsState()
    val infoBottomSheet = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBarStart2(title = "Мое Хозяйство",
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
            infoBottomSheet = infoBottomSheet
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StartScreenContainer(
    modifier: Modifier,
    onItemClick: (Int) -> Unit,
    navigateToItemIncubator: (Int) -> Unit,
    navigateToItemProjectArh: (Int) -> Unit,
    navigateToItemIncubatorArh: (Int) -> Unit,
    projectListArh: List<ProjectTable>,
    projectListAct: List<ProjectTable>,
    infoBottomSheet: MutableState<Boolean>
) {
    var state by remember { mutableStateOf(0) }
    val titles = listOf("Действующие", "Архив")
    val pagerState = rememberPagerState {
        titles.size
    }

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
                                            }
                                    )
                                } else {
                                    CardFerma(
                                        projectTable = it, modifier = Modifier
                                            .padding(8.dp)
                                            .clickable {
                                                onItemClick(it.id)
                                            }
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
                                            }
                                    )
                                } else {
                                    CardFerma(
                                        projectTable = it, modifier = Modifier
                                            .padding(8.dp)
                                            .clickable {
                                                navigateToItemProjectArh(it.id)
                                            }
                                    )
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
            infoBottomSheet = infoBottomSheet
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(
    infoBottomSheet: MutableState<Boolean>,
) {

    val anonotatedString = buildAnnotatedString {
        append("Дорогой друг\nНезабудь вступить в нашу ")
        pushStringAnnotation(tag = "URL", annotation = "https://vk.com/myfermaapp")
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
            )
        ) {
            append("группу в ВК!")
        }
        pop()
    }


    ModalBottomSheet(onDismissRequest = { infoBottomSheet.value = false }) {

        Column(modifier = Modifier.padding(5.dp, 5.dp)) {

            Text(
                text = "Мое Хозяйство v1.60",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            ClickableText(
                text = anonotatedString,
                onClick = { offset ->
                    anonotatedString.getStringAnnotations(
                        tag = "URL",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {}
                })
            Text(
                text = "С ее помощью Вы сможете следить за обновлениями и оставлять отзывы о нашем приложении! \nБудем совершенствовать Ваше хозяйство вместе!",
//                modifier = Modifier.padding(3.dp)
            )

            Button(
                onClick = {
                    infoBottomSheet.value = false
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)

            ) {
                Text(text = "Спасибо!")
            }
        }
    }
}


@Composable
fun CardIncubator(
    projectTable: ProjectTable, modifier: Modifier = Modifier
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
            modifier = Modifier.size(194.dp)
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
        day = "Идет ${TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)+1} день"

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
fun CardFerma(projectTable: ProjectTable, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors()
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_warehouse_24),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(194.dp)
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
            text = projectTable.data, fontSize = 15.sp,
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

