package com.zaroslikov.fermacompose2.data.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable


//data class AnimalWithCountDto(
//    val id: Long,
//    val name: String,
//    val type: String,
//    val date: String,
//    val dateFactory: String?,
//    val group: Boolean,
//    val sex: Boolean,
//    val note: String,
//    val count: String?,
//    val suffix: String?
//)

data class AnimalWithCountDto(
    @Embedded val animal: AnimalTable,
    val id: Long,
    @Relation(
        parentColumn = "id",
        entityColumn = "animalId"
    )
    val counts: List<AnimalCountTable>
)