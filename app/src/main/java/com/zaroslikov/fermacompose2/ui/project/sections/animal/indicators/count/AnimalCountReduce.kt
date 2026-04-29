package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count


import android.util.Log
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.suffixWeightList
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountZero
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble2
import com.zaroslikov.fermacompose2.supportFun.formatNumber


class AnimalCountReduce : BaseReducer<AnimalCountState, AnimalCountIntent>() {
    override fun reducer(
        state: AnimalCountState,
        intent: AnimalCountIntent
    ): AnimalCountState {
        return when (intent) {
            is AnimalCountIntent.RefreshEntryBottomSheetState -> state.updateEntryBottomSheet(
                intent.isOpen,
                intent.state,
                intent.isSaveStateForBottomSheet,
                intent.saveAnimalCountVersion
            ).updateValid().let { state ->
                if (intent.saveAnimalCountVersion == AnimalCountVersion.KILL && intent.isOpen)
                    state.updateTotalWeight() else state
            }
            // Animal Card
            is AnimalCountIntent.OpenSoloDialogClicked -> state.updateOpenSoloDialog(intent.value)
            is AnimalCountIntent.SexClicked -> state.updateSex(intent.value)

            // Update Count Animal
            is AnimalCountIntent.CountChanged -> state.updateCountActionAnimal(intent.value)
                .updatePriceAllActionAnimal().updateValid().let { state ->
                    if (state.currentProduct.version == AnimalCountVersion.KILL)
                        state.updateTotalWeight() else state
                }

            is AnimalCountIntent.PriceChanged ->
                state.updatePriceActionAnimal(intent.value).updatePriceAllActionAnimal()
                    .updateValid()

            is AnimalCountIntent.AutoPriceClicked ->
                state.updateAutoPriceActionAnimal(intent.value).updatePriceAllActionAnimal()

            is AnimalCountIntent.DateClicked -> state.updateDateActionAnimal(intent.value)
            is AnimalCountIntent.NoteChanged -> state.updateNoteActionAnimal(intent.value)

            // Sale Count Animal
            is AnimalCountIntent.BuyerSaleChanged -> state.updateBuyerSale(intent.value)

            // Kill Count Animal
            is AnimalCountIntent.OpenWeightAlertDialogClicked ->
                state.updateOpenWeightDialog(intent.value)

            is AnimalCountIntent.WeightChanged ->
                state.updateWeight(intent.value)

            is AnimalCountIntent.TitleProductKillChanged ->
                state.updateTitleProductKill(intent.value).updateValidKill()

            is AnimalCountIntent.TitleAndSuffixKillClicked ->
                state.updateTitleAndSuffixProductKill(intent.pair).updateValidKill()

            is AnimalCountIntent.CountProductKillChanged ->
                state.updateCountProductKill(intent.value).updateValidKill()

            is AnimalCountIntent.SuffixProductKillChanged -> state.updateSuffixProductKill(intent.value)
            AnimalCountIntent.AddProductKillChanged -> state.addProductKill().updateFinalWeight()
            is AnimalCountIntent.ChoiceProductKillChanged -> state.choiceProductKill(intent.index)
            is AnimalCountIntent.CancelProductKillChanged -> state.cancelProductKill()
            is AnimalCountIntent.EditProductKillChanged -> state.editProductKill()
                .updateFinalWeight()

            is AnimalCountIntent.RemoveProductKillChanged ->
                state.removeProductKill(intent.index).updateFinalWeight()

            else -> state
        }
    }

    private fun AnimalCountState.updateValid(): AnimalCountState {
        val version = currentProduct.version
        val count = currentProduct.count
        val price = currentProduct.price

        val baseValid = when (version) {
            AnimalCountVersion.SALE, AnimalCountVersion.EXPENSES ->
                count.isNotBlank() && price.isNotBlank() && !isAnimalCountZero(count)/* isErrorCountZero*/

            AnimalCountVersion.KILL, AnimalCountVersion.WRITE_OFF, AnimalCountVersion.ADD, AnimalCountVersion.INCUBATOR  ->
                count.isNotBlank() && !isAnimalCountZero(count)
        }

        return copy(
            currentProduct = currentProduct.copy(
                hasAnyError = baseValid
            )
        )
    }

    private fun AnimalCountState.updateEntryBottomSheet(
        isOpenEntryBottomSheet: Boolean,
        countItem: CountItem,
        isSaveStateForEntry: Boolean,
        saveAnimalCountVersion: AnimalCountVersion?
    ): AnimalCountState {
        return copy(
            isOpenEntryBottomSheet = isOpenEntryBottomSheet,
            currentProduct = countItem,
            isSaveStateForEntry = isSaveStateForEntry,
            saveAnimalCountVersion = saveAnimalCountVersion
        )
    }


    private fun AnimalCountState.updateWeightDialog(value: Boolean): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                isOpenWeightDialog = value
            )
        )
    }

    // Action Animal
    private fun AnimalCountState.updateCountActionAnimal(countAnimal: String): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                count = countAnimal,
                error = currentProduct.error.copy(
                    isErrorCount = countAnimal.isBlank(),
                    isErrorCountZero = isAnimalCountZero(countAnimal)
                )
            )
        )
    }

    private fun AnimalCountState.updatePriceActionAnimal(price: String): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                price = price,
                error = currentProduct.error.copy(
                    isErrorPrice = price.isBlank()
                )
            )
        )
    }

    private fun AnimalCountState.updateAutoPriceActionAnimal(isAutoPrice: Boolean): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                isAutoCalculate = isAutoPrice
            )
        )
    }

    private fun AnimalCountState.updateDateActionAnimal(date: String): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                date = date
            )
        )
    }

    private fun AnimalCountState.updateNoteActionAnimal(note: String): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                note = note
            )
        )
    }

    private fun AnimalCountState.updatePriceAllActionAnimal(): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                priceAll = if (currentProduct.isAutoCalculate) (currentProduct.price.toConvertZeroDouble() *
                        currentProduct.count.toConvertZeroDouble()).formatNumber()
                else "0"
            )
        )
    }

    // Sale Count Animal
    private fun AnimalCountState.updateBuyerSale(buyer: String): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                buyer = buyer
            )
        )
    }

    // Kill Animal Count
    private fun AnimalCountState.updateOpenWeightDialog(isOpenWeightAlertDialog: Boolean): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                isOpenWeightDialog = isOpenWeightAlertDialog
            )
        )
    }

    private fun AnimalCountState.updateWeight(weight: Double): AnimalCountState {
        Log.i("product_kill", "updateWeight: $weight ")
        return copy(
            currentProduct = currentProduct.copy(
                totalWeight = weight
            )
        )
    }

    private fun AnimalCountState.updateValidKill(): AnimalCountState {
        val baseValid =
            currentProduct.currentProduct.title.isNotBlank() && currentProduct.currentProduct.countProduct.isNotBlank()
        return copy(
            currentProduct = currentProduct.copy(
                currentProduct = currentProduct.currentProduct.copy(
                    hasAnyError = baseValid
                )
            )
        )
    }

    private fun AnimalCountState.updateTitleProductKill(newTitle: String): AnimalCountState {
        return copy(
//              val warehouseList = updateWarehouseUiStateSync(newTitle)
            currentProduct = currentProduct.copy(
                currentProduct = currentProduct.currentProduct.copy(
                    title = newTitle,
                    error = currentProduct.currentProduct.error.copy(
                        isError = newTitle.isBlank(),
                        isErrorSlash = newTitle.isSlash()
                    ),
//                    warehouseList = warehouseList
                )
            )
        )
    }

    private fun AnimalCountState.updateTitleAndSuffixProductKill(
        newTitleAndSuffix: Pair<String, Suffix>
    ): AnimalCountState {
//        val warehouseList = updateWarehouseUiStateSync(newTitleAndSuffix.first)
        return copy(
            currentProduct = currentProduct.copy(
                currentProduct = currentProduct.currentProduct.copy(
                    title = newTitleAndSuffix.first,
                    suffixProduct = newTitleAndSuffix.second,
//                    warehouseList = warehouseList
                )
            )
        )
    }

    private fun AnimalCountState.updateCountProductKill(newCount: String): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                currentProduct = currentProduct.currentProduct.copy(
                    countProduct = newCount,
                    error = currentProduct.currentProduct.error.copy(
                        isErrorCount = newCount.isBlank(),
                    )
                )
            )
        )
    }

    private fun AnimalCountState.updateSuffixProductKill(newSuffix: Suffix): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                currentProduct = currentProduct.currentProduct.copy(
                    suffixProduct = newSuffix
                )
            )
        )
    }

    private fun AnimalCountState.addProductKill(): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                productKillList = currentProduct.productKillList + currentProduct.currentProduct.copy(
                    isEntry = false
                ),
                currentProduct = ProductKill(
                    suffixProduct = weight?.suffix ?: Suffix.KILOGRAM
                )
            )
        )
    }

    private fun AnimalCountState.choiceProductKill(index: Int): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                productKillList = currentProduct.productKillList.mapIndexed { i, item ->
                    if (i == index) item.copy(isEntry = true)
                    else item.copy(isEntry = false)
                },
                currentProduct = currentProduct.productKillList[index],
                indexProductKill = index
            )
        )
    }

    private fun AnimalCountState.cancelProductKill(): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                productKillList = currentProduct.productKillList.mapIndexed { i, item ->
                    if (i == currentProduct.indexProductKill) item.copy(isEntry = false)
                    else item
                },
                currentProduct = ProductKill(),
            )
        )
    }

    private fun AnimalCountState.editProductKill(): AnimalCountState {
        return copy(
            currentProduct = currentProduct.copy(
                productKillList = currentProduct.productKillList.mapIndexed { i, item ->
                    if (i == currentProduct.indexProductKill) currentProduct.currentProduct
                    else item
                },
                currentProduct = ProductKill()
            )
        )
    }

    private fun AnimalCountState.removeProductKill(index: Int): AnimalCountState {

        Log.i(
            "product_kill",
            "removeProductKill: ${
                currentProduct.productKillList.mapIndexed { i, item ->
                    if (i == index) item.copy(isVisibility = false) else item.copy(isEntry = false)
                }
            }"
        )

        return copy(
            currentProduct = currentProduct.copy(
                productKillList = currentProduct.productKillList.mapIndexed { i, item ->
                    if (i == index) item.copy(isVisibility = false)
                    else item.copy(isEntry = false)
                },
                currentProduct = ProductKill()
            )
        )
    }

    private fun AnimalCountState.updateFinalWeight(): AnimalCountState {
        val weightAnimal = currentProduct.weightSuffix
        val finalWeight = currentProduct.productKillList.filter { it.isVisibility }
            .sumOf { product ->
                if (product.suffixProduct in suffixWeightList)
                    product.countProduct.toConvertZeroDouble()
                        .convertWeight(
                            from = product.suffixProduct,
                            to = weightAnimal
                        ) else 0.0
            }

        return copy(
            currentProduct = currentProduct.copy(
                finalWeight = finalWeight
            )
        )
    }

    private fun AnimalCountState.updateTotalWeight(): AnimalCountState {
        val countAnimal = currentProduct.count.toConvertZeroDouble2()
        val weightAnimal = weight

        val (weight, weightSuffix) = weightAnimal?.let {
            when {
                countAnimal > 0 -> weightAnimal.weight to weightAnimal.suffix
                else -> null to weightAnimal.suffix
            }
        } ?: (null to Suffix.KILOGRAM)

        val totalWeight = weight?.let { weight.toConvertZeroDouble2() * countAnimal }

        return copy(
            currentProduct = currentProduct.copy(
                totalWeight = totalWeight,
                weightSuffix = weightSuffix
            )
        )
    }

    // Animal Card
    private fun AnimalCountState.updateOpenSoloDialog(openDialog: Boolean): AnimalCountState {
        return copy(
            openSoloDialog = openDialog
        )
    }

    private fun AnimalCountState.updateSex(sex: Boolean): AnimalCountState {
        return copy(
            animal = animal.copy(
                sex = sex
            )
        )
    }
}