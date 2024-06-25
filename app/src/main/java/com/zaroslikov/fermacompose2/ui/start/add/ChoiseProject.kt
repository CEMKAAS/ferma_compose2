package com.zaroslikov.fermacompose2.ui.start.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.zaroslikov.fermacompose2.TopAppBarStart
import androidx.compose.ui.Modifier
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.start.DrawerItems
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.incubator.AddIncubatorDestination

object ChoiseProjectDestination : NavigationDestination {
    override val route = "ChoiseProject"
    override val titleRes = R.string.app_name

}
@Composable
fun ChoiseProject(
    navigateBack: () -> Unit,
    navigateProject: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Выбор проекта", navigateUp = navigateBack)
        }) { innerPadding ->
        ChooiseProjectContainer(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight(),
            navigateProject = navigateProject
        )
    }
}


@Composable
fun ChooiseProjectContainer(
    modifier: Modifier,
    navigateProject: (String) -> Unit
) {

    val drawerItems = listOf(
        DrawerItems(
            R.drawable.chicken, "Инкубатор", AddIncubatorDestination.route
        ),
        DrawerItems(
            R.drawable.baseline_warehouse_24, "Хозяйство", ProjectAddDestination.route
        ),
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = " Выберите интересующий Вас проект",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.Center,
        ) {
            items(drawerItems.size) {
                AddIncubatorCard(
                    drawerItems = drawerItems[it],
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navigateProject(drawerItems[it].route)
                        },
                )
            }
        }
    }
}

@Composable
fun AddIncubatorCard(drawerItems: DrawerItems, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(),
    ) {
        Box(
            modifier = Modifier.height(200.dp)
        ) {
            Image(
                painter = painterResource(id = drawerItems.icon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = drawerItems.text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 5.dp, horizontal = 5.dp)
            )
        }

    }
}