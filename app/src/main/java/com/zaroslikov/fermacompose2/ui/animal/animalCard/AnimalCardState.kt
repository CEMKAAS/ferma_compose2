//package com.zaroslikov.fermacompose2.ui.animal.animalCard
//
//import android.util.Log
//import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalCard
//import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
//
//data class AnimalCardState(
//    val id: Long = 0,
//    val name: String = "",
//    val type: String = "",
//    val date: String = "11.12.2022",
//    val dateFactory: String? = null,
//    val age: String = "",
//    val group: Boolean = false,
//    val sex: Boolean = false,
//    val note: String = "",
//    val image: String? = null,
//    val archive: Boolean = false,
//    val foodDay: Double = 0.0,
//    val foodDaySuffix: String = "",
//    val idPT: Long = 0,
//    val price: Double? = 0.0,
//    val countAnimal: String? = "",
//    val countAnimalSuffix: String? = "",
//    val size: String? = "",
//    val sizeSuffix: String? = "",
//    val weight: String? = "",
//    val weightSuffix: String? = "",
//    val vaccination: String? = "",
//    val vaccinationDate: String? = "",
//    val addAnimal: AddAnimal = AddAnimal()
//){
//    data class AddAnimal(
//        val count: String = "",
//        val countSuffix: String = "",
//        val isAutoPrice: Boolean = false,
//        val price: String = "",
//        val priceAll: String = "",
//        val note: String = "",
//        val isErrorCount: Boolean = false
//    )
//}
//
//
//
//
//
//
//
//fun AnimalCardState.updateFromDomain(domain: DomainAnimalCard): AnimalCardState {
//    Log.i("animalCard", "updateFromDomain: ${domain.dateFactory}")
//    return this.copy(
//        name = domain.name,
//        type = domain.type,
//        date = domain.date,
//        dateFactory = domain.dateFactory,
//        group = domain.group,
//        sex = domain.sex,
//        note = domain.note,
//        archive = domain.archive,
//        foodDay = domain.foodDay,
//        foodDaySuffix = domain.foodDaySuffix,
//        price = domain.price,
//        countAnimal = domain.countAnimal,
//        countAnimalSuffix = domain.countAnimalSuffix,
//        size = domain.size,
//        sizeSuffix = domain.sizeSuffix,
//        weight = domain.weight,
//        weightSuffix = domain.weightSuffix,
//        vaccination = domain.vaccination,
//        vaccinationDate = domain.vaccinationDate
//    )
//}
//
//fun AnimalCardState.updateNote(note: String): AnimalCardState {
//    return this.copy(note = note)
//}
//
//fun AnimalCardState.updateNote2(note: String): DomainAnimalTable {
//    return DomainAnimalTable(
//        id = id,
//        name = name,
//        type = type,
//        date = date,
//        dateFactory = dateFactory,
//        group = group,
//        sex = sex,
//        note = note,
//        image = image,
//        archive = archive,
//        foodDay = foodDay,
//        foodDaySuffix = foodDaySuffix,
//        idPT = 0
//    )
//}
//
///*fun AnimalCardState.saveAddAnimal() {
//    if (isErrorAddAnimal(count = countAnimal, isErrorCount = { isErrorCount = it })
//    ) {
//        val count =
//            (countAnimal.toConvertOnlyInt().toInt() + countAll.toInt()).toString()
//                .toConvertOnlyInt()
//        onSaveClick(
//            Pair(
//                first = DomainIndicatorsVM(
////                                    weight = count,
//                    weight = countAnimal,
//                    suffix = countSuffix,
//                    date = dateToday(),
//                    idAnimal = idPT,
//                    note = reasonNote,
//                    version = if (priceInDB.isBlank() || priceInDB == "0") 4 else 1
//                ),
//                second = if (priceInDB.isBlank() || priceInDB == "0") null else {
//                    ExpensesTable(
//                        title = title,
//                        count = countAnimal.toConvertDbDouble(),
//                        day = dateTodayArray()[0],
//                        mount = dateTodayArray()[1],
//                        year = dateTodayArray()[2],
//                        price = priceInDB.toConvertDbDouble(),
//                        countSuffix = countSuffix,
//                        category = category,
//                        note = reasonNote,
//                        isShowFood = false,
//                        isShowWarehouse = false,
//                        isShowAnimals = false,
//                        isShowFoodHand = false,
//                        feedFood = 0.0,
//                        countAnimal = 0,
//                        foodDesignedDay = 0,
//                        lastDayFood = "",
//                        idPT = idPT.toLong(),
//                        priceAll = 0.0,
//                        feedFoodSuffix = "",
//                        weight = 0.0,
//                        weightSuffix = "",
//                    )
//                }
//            )
//        )
//    }
//}*/