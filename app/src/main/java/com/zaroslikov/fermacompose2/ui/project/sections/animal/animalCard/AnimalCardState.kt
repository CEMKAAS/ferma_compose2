package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.dto.add.DomainAnimalCountSuffix

import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalCardState(
    val isOpenArchiveDialog: Boolean = false,
    val animal: DomainAnimalTable = DomainAnimalTable(),
    val weight: DomainAnimalWeight? = DomainAnimalWeight(),
    val size: DomainAnimalSize? = DomainAnimalSize(),
    val countAnimal: DomainAnimalCount = DomainAnimalCount(),
    val vaccination: DomainAnimalVaccination? = DomainAnimalVaccination(),
    val age: String = "",
    val price: Double? = 0.0,
    val buyerList: List<String> = emptyList(),
    val itemIdPT: Long = 0,
    val itemId: Long = 0,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    val productList: List<DomainAnimalCountSuffix> = emptyList()
) : BaseState