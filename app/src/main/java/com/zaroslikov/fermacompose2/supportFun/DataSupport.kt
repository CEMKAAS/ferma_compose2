package com.zaroslikov.fermacompose2.supportFun


data class TripleData(
    val first: Long, val second: String, val third: String
)

data class PairData(
    val first: String, val second: String
)

data class DataStringListState(val list: List<String> = listOf())

data class DataPairListState(val animalList: List<PairData> = listOf())

data class DataTripleListState(val animalList: List<TripleData> = listOf())


