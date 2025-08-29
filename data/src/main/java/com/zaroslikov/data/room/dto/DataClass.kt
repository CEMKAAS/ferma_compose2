package com.zaroslikov.data.room.dto

import androidx.compose.ui.graphics.ImageBitmap
import com.zaroslikov.data.room.table.ferma.AddTable
import com.zaroslikov.data.room.table.ferma.ExpensesTable
import com.zaroslikov.data.room.table.ferma.SaleTable
import com.zaroslikov.data.room.table.ferma.WriteOffTable

data class BrieflyItemPrice(
    var title: String,
    var count :Double,
    var price :Double,
    var suffix: String
)



//data class BrieflyUiState(val itemList: List<BrieflyItemCount> = listOf())
data class BrieflyPriceUiState(val itemList: List<BrieflyItemPrice> = listOf())

data class HomeUiState(val itemList: List<AddTable> = listOf())
data class SaleUiState(val itemList: List<SaleTable> = listOf())
data class ExpensesUiState(val itemList: List<ExpensesTable> = listOf())
data class WriteOffUiState(val itemList: List<WriteOffTable> = listOf())

data class ProjectTableStartScreen(
    val id: Int = 0,
    val titleProject: String, // название
    val type:String,
    val data: String,  // Дата начала проекта
    var arhive: String,  //не архив = 0, Архив = 1
    val dateEnd: String,  // Конец проекта
    val mode: Int, //Инкубатор = 0, Хозяйство = 1
    val imageData: ImageBitmap?, // Изображение
)
