package com.zaroslikov.crm.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zaroslikov.crm.data.room.converter.CrmConverters
import com.zaroslikov.crm.data.room.dao.CrmClientDao
import com.zaroslikov.crm.data.room.dao.CrmContactDao
import com.zaroslikov.crm.data.room.dao.CrmInteractionDao
import com.zaroslikov.crm.data.room.dao.CrmTaskDao
import com.zaroslikov.crm.data.room.table.CrmClientTable
import com.zaroslikov.crm.data.room.table.CrmContactTable
import com.zaroslikov.crm.data.room.table.CrmInteractionTable
import com.zaroslikov.crm.data.room.table.CrmTaskTable

/**
 * Отдельная от `AppDatabase` база CRM (`crm_database`). Связь с проектом — колонка `idPT`
 * без внешнего ключа между базами.
 */
@Database(
    entities = [
        CrmClientTable::class,
        CrmContactTable::class,
        CrmInteractionTable::class,
        CrmTaskTable::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(CrmConverters::class)
abstract class CrmDatabase : RoomDatabase() {
    abstract fun crmClientDao(): CrmClientDao
    abstract fun crmContactDao(): CrmContactDao
    abstract fun crmInteractionDao(): CrmInteractionDao
    abstract fun crmTaskDao(): CrmTaskDao
}
