package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseEditScreen

import android.util.Log
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.NotificationParameters

class WarehouseEditReduce : BaseReducer<WarehouseEditState, WarehouseEditIntent>() {
    override fun reducer(
        state: WarehouseEditState,
        intent: WarehouseEditIntent
    ): WarehouseEditState {
        return when (intent) {
            is WarehouseEditIntent.NameProjectChanged -> state.updateTitle(intent.value)
                .updateValid()

            is WarehouseEditIntent.IconClicked -> state.updateIcon(intent.value)
            is WarehouseEditIntent.ImagePathClicked -> state.updateImagePath(intent.value)
            is WarehouseEditIntent.DateClicked -> state.updateDateProject(intent.value)
            is WarehouseEditIntent.CurrencyClicked -> state.updateCurrentCurrency(intent.value)
            is WarehouseEditIntent.WeightClicked -> state.updateCurrentWeight(intent.value)
            is WarehouseEditIntent.VolumeClicked -> state.updateCurrentVolume(intent.value)
            is WarehouseEditIntent.LinearClicked -> state.updateCurrentLinear(intent.value)

            //Notification
            is WarehouseEditIntent.ShowNotificationClicked -> state.updateShowNotification(intent.value)
            is WarehouseEditIntent.TimeNotificationChanged -> state.updateTimeNotification(intent.value)
            is WarehouseEditIntent.NoteNotificationChanged -> state.updateNoteNotification(intent.value)
            is WarehouseEditIntent.ChoiceNotificationClicked ->
                state.updateChoiceNotification(intent.index)

            is WarehouseEditIntent.RemoveNotificationClicked ->
                state.updateRemoveNotification(intent.index)

            WarehouseEditIntent.CancelNotificationClicked -> state.updateCancelNotification()
            WarehouseEditIntent.AddNotificationClicked -> state.updateAddNotification()
            WarehouseEditIntent.EditNotificationClicked -> state.updateEditNotification()
            else -> state
        }
    }

    private fun WarehouseEditState.updateValid(): WarehouseEditState {
        return copy(
            hasAnyError = currentProject.title.isNotBlank()
        )
    }

    private fun WarehouseEditState.updateTitle(nameProject: String): WarehouseEditState {
        return copy(currentProject = currentProject.copy(title = nameProject))
    }

    private fun WarehouseEditState.updateIcon(currentIcon: Int): WarehouseEditState {
        return copy(currentIcon = currentIcon)
    }

    private fun WarehouseEditState.updateImagePath(imagePath: String?): WarehouseEditState {
        return copy(imagePath = imagePath)
    }

    private fun WarehouseEditState.updateDateProject(dateProject: String): WarehouseEditState {
        return copy(currentProject = currentProject.copy(date = dateProject))
    }

    private fun WarehouseEditState.updateCurrentCurrency(currency: Suffix): WarehouseEditState {
        return copy(currentSettings = currentSettings.copy(currencySuffix = currency))
    }

    private fun WarehouseEditState.updateCurrentWeight(weight: Suffix): WarehouseEditState {
        return copy(currentSettings = currentSettings.copy(weightSuffix = weight))
    }

    private fun WarehouseEditState.updateCurrentVolume(volume: Suffix): WarehouseEditState {
        return copy(currentSettings = currentSettings.copy(volumeSuffix = volume))
    }

    private fun WarehouseEditState.updateCurrentLinear(linear: Suffix): WarehouseEditState {
        return copy(currentSettings = currentSettings.copy(linearSuffix = linear))
    }

    private fun WarehouseEditState.updateShowNotification(isShowNotification: Boolean): WarehouseEditState {
        Log.i("warehouse", "updateShowNotification: $isShowNotification ")
        return copy(
            isShowNotification = isShowNotification
        )
    }

    private fun WarehouseEditState.updateTimeNotification(time: String): WarehouseEditState {
        return copy(
            currentNotification = currentNotification.copy(
                time = time
            )
        )
    }


    private fun WarehouseEditState.updateNoteNotification(note: String): WarehouseEditState {
        return copy(
            currentNotification = currentNotification.copy(
                note = note
            )
        )
    }

    private fun WarehouseEditState.updateAddNotification(): WarehouseEditState {
        return copy(
            notificationList = notificationList + currentNotification.copy(
                isEntry = false
            ),
            currentNotification = NotificationParameters()
        )
    }

    private fun WarehouseEditState.updateEditNotification(): WarehouseEditState {
        return copy(
            notificationList = notificationList.mapIndexed { i, item ->
                if (item.localId == indexNotification) currentNotification
                else item
            },
            currentNotification = NotificationParameters()
        )
    }


    private fun WarehouseEditState.updateRemoveNotification(index: Long): WarehouseEditState {
        return copy(
            notificationList = notificationList.mapIndexed { i, notification ->
                if (notification.localId == index) notification.copy(isVisibility = false) else notification.copy(
                    isEntry = false
                )
            },
            currentNotification = NotificationParameters()
        )
    }


    private fun WarehouseEditState.updateChoiceNotification(index: Long): WarehouseEditState {
        return copy(
            notificationList = notificationList.mapIndexed { i, item ->
                if (item.localId == index) {
                    if (item.isEntry) item.copy(isEntry = false) else item.copy(isEntry = true)
                } else item.copy(isEntry = false)
            },
            currentNotification = if (notificationList.find { it.localId == index }?.isEntry ?: true)
                NotificationParameters() else notificationList.find { it.localId == index }
                ?: NotificationParameters(),
            indexNotification = index
        )
    }


    private fun WarehouseEditState.updateCancelNotification(): WarehouseEditState {
        return copy(
            notificationList = notificationList.map { item ->
                if (item.localId == indexNotification) item.copy(isEntry = false)
                else item
            },
            currentNotification = NotificationParameters()
        )
    }
}