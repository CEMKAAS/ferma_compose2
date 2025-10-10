package com.zaroslikov.fermacompose2.data

import android.content.Context
import com.zaroslikov.data.room.database.AppDatabase
import com.zaroslikov.fermacompose2.data.Alarm.AlarmMangerRepository
import com.zaroslikov.fermacompose2.data.Alarm.AlarmRepository
import com.zaroslikov.fermacompose2.data.water.WaterRepository
//import com.zaroslikov.fermacompose2.data.water.WorkManagerWaterRepository


interface AppContainer {
//    val waterRepository : WaterRepository
    val alarmRepositiry: AlarmRepository
}


class AppDataContainer(private val context: Context) : AppContainer {
//    override val waterRepository = WorkManagerWaterRepository(context)

    override val alarmRepositiry: AlarmRepository = AlarmMangerRepository(context)
}

