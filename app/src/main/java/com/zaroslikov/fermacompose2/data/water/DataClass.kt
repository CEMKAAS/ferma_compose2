package com.zaroslikov.fermacompose2.data.water

import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable

data class BrieflyItemPrice(
    var title: String,
    var count :Double,
    var price :Double,
    var suffix: String
)

data class BrieflyItemCount(
    var title: String,
    var count :Double,
    var suffix: String
)

data class BrieflyUiState(val itemList: List<BrieflyItemCount> = listOf())
data class BrieflyPriceUiState(val itemList: List<BrieflyItemPrice> = listOf())

data class HomeUiState(val itemList: List<AddTable> = listOf())
data class SaleUiState(val itemList: List<SaleTable> = listOf())
data class ExpensesUiState(val itemList: List<ExpensesTable> = listOf())
data class WriteOffUiState(val itemList: List<WriteOffTable> = listOf())

data class ProjectTableStartScreen(
    val id: Int = 0,
    val titleProject: String, // название
    val data: String,  // Дата начала проекта
    var arhive: String,  //не архив = 0, Архив = 1
    val dateEnd: String,  // Конец проекта
    val mode: Int, //Инкубатор = 0, Хозяйство = 1
    val imageData: ImageBitmap, // Изображение
)
