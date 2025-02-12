/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaroslikov.fermacompose2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesAnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.ferma.NoteTable
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable


@Database(entities = [AddTable::class, SaleTable::class, ExpensesTable::class, WriteOffTable::class, ProjectTable::class, Incubator::class, AnimalTable::class, AnimalCountTable::class, AnimalSizeTable::class, AnimalVaccinationTable::class, AnimalWeightTable::class, NoteTable::class, ExpensesAnimalTable::class], version = 2, exportSchema = false)
abstract class InventoryDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var Instance: InventoryDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS NoteFerma (_id INTEGER PRIMARY KEY NOT NULL, title TEXT NOT NULL, note TEXT NOT NULL, date TEXT NOT NULL, idPT INTEGER NOT NULL, FOREIGN KEY (idPT) REFERENCES МyINCUBATOR (_id) ON DELETE CASCADE)")
                db.execSQL("CREATE TABLE IF NOT EXISTS ExpensesAnimalTable (_id INTEGER PRIMARY KEY NOT NULL, idExpenses INTEGER NOT NULL, idAnimal INTEGER NOT NULL,  percentExpenses REAL NOT NULL, idPT INTEGER NOT NULL, FOREIGN KEY (idPT) REFERENCES МyINCUBATOR (_id) ON DELETE CASCADE)")
                db.execSQL("ALTER TABLE AnimalTable ADD COLUMN price REAL NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE AnimalTable ADD COLUMN foodDay REAL NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE MyFermaEXPENSES ADD COLUMN showFood INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE MyFermaEXPENSES ADD COLUMN showWarehouse INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE MyFermaEXPENSES ADD COLUMN showAnimals INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE MyFermaEXPENSES ADD COLUMN dailyExpensesFoodAndCount INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE MyFermaEXPENSES ADD COLUMN dailyExpensesFood REAL NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE MyFermaEXPENSES ADD COLUMN countAnimal INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE MyFermaEXPENSES ADD COLUMN foodDesignedDay INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE MyFermaEXPENSES ADD COLUMN lastDayFood TEXT NOT NULL DEFAULT '0'")
                db.execSQL("ALTER TABLE MyFerma ADD COLUMN idAnimal INTEGER NOT NULL DEFAULT 0") }
                //update
        }
        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE МyINCUBATOR ADD COLUMN imageData BLOG null") }
        }


        fun getDatabase(context: Context): InventoryDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
