package com.zaroslikov.fermacompose2.data

import android.content.Context
import com.zaroslikov.data.room.database.AppDatabase
import com.zaroslikov.data.room.repository.OfflineItemsRepository
import com.zaroslikov.fermacompose2.data.Alarm.AlarmMangerRepository
import com.zaroslikov.fermacompose2.data.Alarm.AlarmRepository
import com.zaroslikov.fermacompose2.data.water.WaterRepository
import com.zaroslikov.fermacompose2.data.water.WorkManagerWaterRepository


interface AppContainer {
    val itemsRepository: ItemsRepository
    val waterRepository : WaterRepository
    val alarmRepositiry: AlarmRepository
}


class AppDataContainer(private val context: Context) : AppContainer {

    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(AppDatabase.getDatabase(context).itemDao())
    }

    override val waterRepository = WorkManagerWaterRepository(context)

    override val alarmRepositiry: AlarmRepository = AlarmMangerRepository(context)
}

