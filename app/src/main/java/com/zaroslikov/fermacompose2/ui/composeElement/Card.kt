package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CardField(
    modifier: Modifier,
    content: @Composable () -> Unit
){
    Card(
        modifier = modifier.padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
    ) {
        content()
    }
}





@Composable
fun CardAdd(){

}