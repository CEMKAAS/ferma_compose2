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
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import com.zaroslikov.fermacompose2.data.converter.CategoryConverter
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesAnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.ferma.NoteTable
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable


@Database(
    entities = [AddTable::class, SaleTable::class, ExpensesTable::class, WriteOffTable::class, ProjectTable::class, Incubator::class, AnimalTable::class, AnimalCountTable::class, AnimalSizeTable::class, AnimalVaccinationTable::class, AnimalWeightTable::class, NoteTable::class, ExpensesAnimalTable::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(CategoryConverter::class)
abstract class InventoryDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        var Instance: InventoryDatabase? = null

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
                db.execSQL("ALTER TABLE MyFerma ADD COLUMN idAnimal INTEGER NOT NULL DEFAULT 0")
            }
        }

         val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE МyINCUBATOR ADD COLUMN imageData BLOB null")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE AnimalWeightTable ADD COLUMN suffix TEXT NOT NULL DEFAULT 'кг.'")
                db.execSQL("ALTER TABLE AnimalCountTable ADD COLUMN suffix TEXT NOT NULL DEFAULT 'ед.'")
                db.execSQL("ALTER TABLE AnimalSizeTable ADD COLUMN suffix TEXT NOT NULL DEFAULT 'м.'")

                db.execSQL("ALTER TABLE AnimalWeightTable ADD COLUMN note TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE AnimalCountTable ADD COLUMN note TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE AnimalSizeTable ADD COLUMN note TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE AnimalVaccinationTable ADD COLUMN note TEXT NOT NULL DEFAULT ''")

                db.execSQL("ALTER TABLE AnimalCountTable ADD COLUMN version INTEGER")

                db.execSQL("ALTER TABLE AnimalTable ADD COLUMN date_factory TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE AnimalTable ADD COLUMN suffix_food_day TEXT NOT NULL DEFAULT ''")


                //==================== Миграция AddTable ====================
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS add_table (
                _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                count REAL NOT NULL,
                day INTEGER NOT NULL,
                month INTEGER NOT NULL,
                year INTEGER NOT NULL,
                price REAL NOT NULL,
                count_suffix TEXT NOT NULL,
                category TEXT NOT NULL,
                animal_id INTEGER,
                note TEXT NOT NULL,
                idPT INTEGER NOT NULL,
                FOREIGN KEY(idPT) REFERENCES ProjectTable(_id) ON DELETE CASCADE,
            )
        """.trimIndent()
                )

                db.execSQL(
                    """
            INSERT INTO add_table (
                _id, title, count, day, month, year, price,
                count_suffix, category, animal_id, note, idPT
            )
            SELECT
                _id, title, disc, DAY, MOUNT, YEAR, PRICE,
                suffix, category, idAnimal, note, idPT
            FROM MyFerma
        """.trimIndent()
                )
                db.execSQL("CREATE INDEX index_add_table_idPT ON add_table(idPT)")
                db.execSQL("DROP TABLE MyFerma")

                //==================== Миграция WriteOffTable ====================
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `MyFermaWRITEOFF_new` (
                `_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `titleWRITEOFF` TEXT NOT NULL,
                `discWRITEOFF` REAL NOT NULL,
                `DAY` INTEGER NOT NULL,
                `MOUNT` INTEGER NOT NULL,
                `YEAR` INTEGER NOT NULL,
                `statusWRITEOFF` INTEGER NOT NULL,
                `priceAll` REAL NOT NULL,
                `suffix` TEXT NOT NULL,
                `note` TEXT NOT NULL,
                `idPT` INTEGER NOT NULL,
                `animal_count_id` INTEGER,
                FOREIGN KEY(`idPT`) REFERENCES `ProjectTable`(`_id`) ON DELETE CASCADE,
                FOREIGN KEY(`animal_count_id`) REFERENCES `AnimalCountTable`(`id`) ON DELETE CASCADE
            )
        """.trimIndent()
                )

                // 2. Копируем данные из старой таблицы
                db.execSQL(
                    """
            INSERT INTO `MyFermaWRITEOFF_new` (
                `_id`, `titleWRITEOFF`, `discWRITEOFF`, `DAY`, `MOUNT`, `YEAR`,
                `statusWRITEOFF`, `priceAll`, `suffix`, `note`, `idPT`
            )

            SELECT `_id`, `titleWRITEOFF`, `discWRITEOFF`, `DAY`, `MOUNT`, `YEAR`,
                   `statusWRITEOFF`, `priceAll`, `suffix`, `note`, `idPT`
            FROM `MyFermaWRITEOFF`
        """.trimIndent()
                )

                // 3. Удаляем старую таблицу
                db.execSQL("DROP TABLE `MyFermaWRITEOFF`")

                // 4. Переименовываем новую таблицу в старое имя
                db.execSQL("ALTER TABLE `MyFermaWRITEOFF_new` RENAME TO `MyFermaWRITEOFF`")

                // 5. Добавляем нужные индексы
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_MyFermaWRITEOFF_idPT` ON `MyFermaWRITEOFF` (`idPT`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_MyFermaWRITEOFF_animal_count_id` ON `MyFermaWRITEOFF` (`animal_count_id`)")

                //==================== Миграция SaleTable ====================
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS sale_table(
                _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                count REAL NOT NULL,
                count_suffix TEXT NOT NULL,
                price REAL NOT NULL,
                price_all REAL,
                day INTEGER NOT NULL,
                month INTEGER NOT NULL,
                year INTEGER NOT NULL,
                category TEXT NOT NULL,
                buyer TEXT NOT NULL,
                note TEXT NOT NULL,
                idPT INTEGER NOT NULL,
                animal_id INTEGER,
                animal_count_id INTEGER,
                FOREIGN KEY(idPT) REFERENCES ProjectTable(_id) ON DELETE CASCADE,
                FOREIGN KEY(animal_id) REFERENCES AnimalTable(id) ON DELETE CASCADE
                FOREIGN KEY(animal_count_id) REFERENCES AnimalCountTable(id) ON DELETE CASCADE
            )
        """.trimIndent()
                )

                db.execSQL(
                    """
            INSERT INTO sale_table(
                _id, title, count, count_suffix, price, price_all,day, month, year,
                category, buyer, note, idPT, animal_id, animal_count_id
            )
            SELECT
                _id, titleSale, discSale, suffix, PRICE, NULL, DAY, MOUNT, YEAR, 
                 category, buyer, note, idPT, NULL, NULL
            FROM MyFermaSale
        """.trimIndent()
                )
                db.execSQL("CREATE INDEX index_sale_table_idPT ON sale_table(idPT)")
                db.execSQL("CREATE INDEX index_sale_table_animalId ON sale_table(animal_id)")
                db.execSQL("CREATE INDEX index_sale_table_animalId ON sale_table(animal_count_id)")
                db.execSQL("DROP TABLE MyFermaSale")

                //==================== Миграция ExpensesTable ====================
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS MyFermaEXPENSES_new (
                _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                count REAL NOT NULL,
                day INTEGER NOT NULL,
                month INTEGER NOT NULL,
                year INTEGER NOT NULL,
                price REAL NOT NULL,
                price_all REAL,
                count_suffix TEXT NOT NULL,
                category TEXT NOT NULL,
                note TEXT NOT NULL,
                is_show_food INTEGER NOT NULL,
                is_show_food_hand INTEGER NOT NULL,
                is_show_warehouse INTEGER NOT NULL,
                is_show_animals INTEGER NOT NULL,
                feed_food REAL,
                feed_food_suffix TEXT,
                count_animal INTEGER,
                food_designed_day INTEGER,
                last_day_food TEXT,
                weight REAL,
                weight_suffix,
                is_auto_weight INTEGER NOT NULL,
                is_auto_price INTEGER NOT NULL,
                idPT INTEGER NOT NULL,
                animalId INTEGER,
                animal_vaccination_id INTEGER,
                animal_count_id INTEGER,
                FOREIGN KEY(idPT) REFERENCES ProjectTable(_id) ON DELETE CASCADE,
                FOREIGN KEY(animalId) REFERENCES AnimalTable(id) ON DELETE CASCADE,
                FOREIGN KEY(animal_vaccination_id) REFERENCES AnimalVaccinationTable(id) ON DELETE CASCADE,
                FOREIGN KEY(animal_count_id) REFERENCES AnimalCountTable(id) ON DELETE CASCADE,
            )""".trimIndent())
                db.execSQL("""
            INSERT INTO MyFermaEXPENSES_new (
                _id, title, count, day, month, year, price, price_all,
                count_suffix, category, note,
                is_show_food, is_show_food_hand, is_show_warehouse, is_show_animals,
                feed_food, feed_food_suffix, count_animal,
                food_designed_day, last_day_food, idPT, animalId, animal_vaccination_id, animal_count_id
            )
            SELECT
                _id, titleEXPENSES, discEXPENSES, DAY, MOUNT, YEAR, countEXPENSES, NULL,
                suffix, category, note,
                showFood, dailyExpensesFoodAndCount, showWarehouse, showAnimals,
                dailyExpensesFood, 'кг.', countAnimal,
                foodDesignedDay, lastDayFood, NULL, NULL, 0, 0, idPT, NULL, NULL, NULL
            FROM MyFermaEXPENSES
        """.trimIndent()
                )
                db.execSQL("CREATE INDEX index_MyFermaEXPENSES_new_idPT ON MyFermaEXPENSES_new(idPT)")
                db.execSQL("CREATE INDEX index_MyFermaEXPENSES_new_animalId ON MyFermaEXPENSES_new(animalId)")
                db.execSQL("CREATE INDEX index_MyFermaEXPENSES_new_animalId ON MyFermaEXPENSES_new(animal_vaccination_id)")
                db.execSQL("CREATE INDEX index_MyFermaEXPENSES_new_animalId ON MyFermaEXPENSES_new(animal_count_id)")
                db.execSQL("DROP TABLE MyFermaEXPENSES")
                db.execSQL("ALTER TABLE MyFermaEXPENSES_new RENAME TO expenses_table")

                //==================== Перенос данных из AnimalTable в ExpensesTable ====================
                db.execSQL(
                    """
            INSERT INTO expenses_table (
                title,
                count,
                day,
                month,
                year,
                price,
                price_all
                count_suffix,
                category,
                note,
                is_show_food,
                is_show_warehouse,
                is_show_animals,
                is_show_food_hand,
                feed_food,
                feed_food_suffix
                count_animal,
                food_designed_day,
                last_day_food,
                weight,
                weight_suffix,
                is_auto_weight,
                is_auto_price
                idPT, 
                animalId
            )
            SELECT
                name AS title,
                CAST((
                    SELECT ac.count
                    FROM AnimalCountTable ac
                    WHERE ac.idAnimal = a.id
                    ORDER BY substr(ac.date, 7, 4) || substr(ac.date, 4, 2) || substr(ac.date, 1, 2) DESC
                    LIMIT 1
                ) AS REAL) AS count,
                CAST(substr(data, 1, 2) AS INTEGER) AS day,
                CAST(substr(data, 4, 2) AS INTEGER) AS month,
                CAST(substr(data, 7, 4) AS INTEGER) AS year,
                price,
                NULL,
                'шт.' AS count_suffix,
                'Покупка животных' AS category,
                note,
                0 AS is_show_food,
                0 AS is_show_warehouse,
                0 AS is_show_animals,
                0 AS is_show_food_hand,
                NULL AS feed_food,
                NULL AS feed_food_suffix,
                NULL AS count_animal,
                NULL AS food_designed_day ,
                NULL AS last_day_food,
                NULL AS weight,
                NULL AS weight_suffix,
                0 AS is_auto_weight, 
                0 AS is_auto_price,
                idPT,
               _id AS animalId
            FROM AnimalTable
            WHERE LENGTH(data) = 10 AND instr(data, '.') = 3
        """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): InventoryDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
