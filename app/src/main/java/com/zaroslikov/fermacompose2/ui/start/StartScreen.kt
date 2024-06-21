package com.zaroslikov.fermacompose2.ui.start

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.TopAppBarStart2
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.ChoiseProjectDestination
import com.zaroslikov.fermacompose2.ui.start.add.ProjectAddDestination


object StartDestination : NavigationDestination {
    override val route = "Start"
    override val titleRes = R.string.app_name
}

@Composable
fun StartScreen(
    navController: NavController,
    navigateToItemProject: (Int) -> Unit,
    navigateToItemIncubator: (Int) -> Unit,
    viewModel: StartScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val projectListArh by viewModel.getAllProjectArh.collectAsState()
    val projectListAct by viewModel.getAllProjectAct.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarStart2(title = "Мое Хозяйство")
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
            navigateToItemIncubator = navigateToItemIncubator
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StartScreenContainer(
    modifier: Modifier,
    onItemClick: (Int) -> Unit,
    navigateToItemIncubator: (Int) -> Unit,
    projectListArh: List<ProjectTable>,
    projectListAct: List<ProjectTable>
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
                        items(items = projectListArh, key = { it.id }) {
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
                }
                }
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

//        Image(
//            painter = painterResource(id = R.drawable.chicken),
//            contentDescription = null,
//            contentScale = ContentScale.Fit,
//            modifier = Modifier.size(194.dp)
//        )
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

@Composable
fun CardFerma(projectTable: ProjectTable, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors()
    ) {


//        Image(
//            bitmap = byteArrayToBitmap(picture).asImageBitmap(),
//            contentDescription = null,
//            contentScale = ContentScale.Fit,
//            modifier = Modifier.size(194.dp)
//        )
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

