package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.card

import com.zaroslikov.fermacompose2.base.reduce.BaseReducer

class AnimalHerdCardReduce : BaseReducer<AnimalHerdCardState, AnimalHerdCardIntent>() {
    override fun reducer(state: AnimalHerdCardState, intent: AnimalHerdCardIntent): AnimalHerdCardState {
        return when (intent) {
            // После первой загрузки примечание держим локальным: иначе запоздалая выдача БД
            // откатывала бы текст, который пользователь уже набрал дальше
            is AnimalHerdCardIntent.Loaded -> state.copy(
                herd = if (state.isLoading) intent.herd else intent.herd.copy(note = state.herd.note),
                isArchive = intent.isArchive,
                weight = intent.weight,
                products = intent.products,
                productsByMember = intent.productsByMember,
                // Выбор сохраняем, пока такая продукция есть; иначе — первая по алфавиту
                selectedProduct = state.selectedProduct?.takeIf { it in intent.productsByMember }
                    ?: intent.products.firstOrNull()?.title,
                isLoading = false
            )

            is AnimalHerdCardIntent.ProductSelected -> state.copy(selectedProduct = intent.title)

            AnimalHerdCardIntent.Closed -> state.copy(isClosed = true)

            is AnimalHerdCardIntent.NoteChanged -> state.copy(herd = state.herd.copy(note = intent.value))

            is AnimalHerdCardIntent.OpenDepersonalizeDialog -> state.copy(isOpenDepersonalizeDialog = intent.value)
            is AnimalHerdCardIntent.OpenDisbandDialog -> state.copy(isOpenDisbandDialog = intent.value)
            is AnimalHerdCardIntent.OperationRunningChanged -> state.copy(isOperationRunning = intent.value)
            AnimalHerdCardIntent.DepersonalizeConfirmed,
            AnimalHerdCardIntent.DisbandConfirmed -> state
        }
    }
}
