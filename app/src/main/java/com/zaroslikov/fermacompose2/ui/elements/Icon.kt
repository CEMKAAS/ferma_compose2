package com.zaroslikov.fermacompose2.ui.elements

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IconDone() {
    Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(20.dp))
}