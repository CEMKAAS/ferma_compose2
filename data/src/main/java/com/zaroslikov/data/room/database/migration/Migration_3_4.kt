package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE AnimalCountTable ADD COLUMN suffix TEXT NOT NULL DEFAULT 'ед.'")

        db.execSQL("ALTER TABLE AnimalCountTable ADD COLUMN note TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE AnimalCountTable ADD COLUMN version INTEGER")

        //==================== Миграция AnimalCountTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS animal_count_table(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                count TEXT NOT NULL,
                suffix INTEGER NOT NULL,
                date TEXT NOT NULL,
                note TEXT NOT NULL,
                version INTEGER,
                animal_id INTEGER NOT NULL,
                FOREIGN KEY(animal_id) REFERENCES animal_table(id) ON DELETE CASCADE,
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO animal_vaccination_table(
                id, count, suffix, date, note, version, animal_id
            )
            SELECT
                id, count, 'ед.', date, '', NULL, animal_id
            FROM AnimalCountTable
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX index_animal_count_table_animal_id ON animal_count_table(animal_id")
        db.execSQL("DROP TABLE AnimalCountTable")

        //==================== Миграция AnimalVaccinationTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS animal_vaccination_table(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                vaccination TEXT NOT NULL,
                count_vaccination INTEGER NOT NULL,
                date TEXT NOT NULL,
                next_vaccination TEXT,
                animal_id INTEGER NOT NULL,
                note TEXT NOT NULL,
                FOREIGN KEY(animal_id) REFERENCES animal_table(id) ON DELETE CASCADE,
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO animal_vaccination_table(
                id, vaccination, count_vaccination, date, next_vaccination, animal_id, note
            )
            SELECT
                id, vaccination, 1, date, nextVaccination, idAnimal, ''
            FROM AnimalVaccinationTable
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX index_animal_vaccination_table_animal_id ON animal_vaccination_table(animal_id")
        db.execSQL("DROP TABLE AnimalVaccinationTable")

        //==================== Миграция AnimalWeightTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS animal_weight_table(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                weight TEXT NOT NULL,
                suffix INTEGER NOT NULL,
                date TEXT NOT NULL,
                animal_id INTEGER NOT NULL,
                note TEXT NOT NULL,
                FOREIGN KEY(animal_id) REFERENCES animal_table(id) ON DELETE CASCADE,
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO animal_weight_table(
                id, size, suffix, date, animal_id, note
            )
            SELECT
                id, size, 'кг.', date, idAnimal, ''
            FROM AnimalWeightTable
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX index_animal_weight_table_animal_id ON animal_weight_table(animal_id")
        db.execSQL("DROP TABLE AnimalWeightTable")

        //==================== Миграция AnimalSizeTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS animal_size_table(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                size TEXT NOT NULL,
                suffix INTEGER NOT NULL,
                date TEXT NOT NULL,
                animal_id INTEGER NOT NULL,
                note TEXT NOT NULL,
                FOREIGN KEY(animal_id) REFERENCES animal_table(id) ON DELETE CASCADE,
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO animal_size_table(
                id, size, suffix, date, animal_id, note
            )
            SELECT
                id, size, 'м.', date, idAnimal, ''
            FROM AnimalSizeTable
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX index_animal_size_table_animal_id ON animal_size_table(animal_id")
        db.execSQL("DROP TABLE AnimalSizeTable")

        //==================== Миграция AnimalTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS animal_table(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                date TEXT NOT NULL,
                date_factory TEXT,
                group INTEGER NOT NULL,
                sex INTEGER NOT NULL,
                note TEXT NOT NULL,
                image TEXT,
                archive INTEGER NOT NULL,
                food_day REAL,
                food_day_suffix REAL,
                idPT INTEGER NOT NULL,
                FOREIGN KEY(idPT) REFERENCES ProjectTable(_id) ON DELETE CASCADE,
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO animal_table (
                id, name, type, date, date_factory, group, sex,
                note, image, archive, food_day, food_day_suffix, idPT
            )
            SELECT
                id, name, type, data, NULL, groop, (CASE WHEN sex IS 'Мужской' THEN 0 ELSE 1 END) AS sex, 
                note, NULL, arhiv, foodDay, NULL, idPT
            FROM AnimalTable
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX index_animal_table_idPT ON animal_table(idPT)")
        db.execSQL("DROP TABLE AnimalTable")

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
                count_suffix INTEGER NOT NULL,
                category TEXT NOT NULL,
                animal_id INTEGER,
                note TEXT NOT NULL,
                idPT INTEGER NOT NULL,
                animal_count_id INTEGER,
                FOREIGN KEY(idPT) REFERENCES ProjectTable(_id) ON DELETE CASCADE,
                FOREIGN KEY(animal_id) REFERENCES animal_table(_id) ON DELETE SET NULL,
                FOREIGN KEY(animal_count_id) REFERENCES animal_count_table(id) ON DELETE CASCADE
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
        db.execSQL("CREATE INDEX IF NOT EXISTS index_add_table_idPT ON add_table(idPT)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_add_table_animal_id ON add_table(animal_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_add_table_animal_count_id ON add_table(animal_count_id)")
        db.execSQL("DROP TABLE MyFerma")

        //==================== Миграция WriteOffTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS write_off_table (
                _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                count REAL NOT NULL,
                count_suffix INTEGER NOT NULL,
                price REAL,
                price_all REAL,
                category NOT NULL,
                day INTEGER NOT NULL,
                month INTEGER NOT NULL,
                year INTEGER NOT NULL,
                status INTEGER NOT NULL,
                note TEXT NOT NULL,
                idPT INTEGER NOT NULL,
                animal_count_id INTEGER,
                FOREIGN KEY(idPT) REFERENCES ProjectTable(_id) ON DELETE CASCADE,
                FOREIGN KEY(animal_count_id) REFERENCES animal_count_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )

        // 2. Копируем данные из старой таблицы
        db.execSQL(
            """
            INSERT INTO write_off_table (
                _id, title, count, count_suffix, price, price_all, category, day, month, year,
                status, note, idPT, animal_count_id
            )
            SELECT _id, titleWRITEOFF, discWRITEOFF, suffix, priceAll, 'Прочее' NULL, DAY, MOUNT, YEAR,
                statusWRITEOFF, note, idPT, NUll
            FROM MyFermaWRITEOFF
        """.trimIndent()
        )

        // 3. Удаляем старую таблицу
        db.execSQL("DROP TABLE MyFermaWRITEOFF")

        // 5. Добавляем нужные индексы
        db.execSQL("CREATE INDEX IF NOT EXISTS index_write_off_table_idPT ON write_off_table (idPT)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_write_off_table_animal_count_id ON write_off_table (animal_count_id)")

        //==================== Миграция SaleTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS sale_table(
                _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                count REAL NOT NULL,
                count_suffix INTEGER NOT NULL,
                price REAL NOT NULL,
                price_all REAL,
                day INTEGER NOT NULL,
                month INTEGER NOT NULL,
                year INTEGER NOT NULL,
                category TEXT NOT NULL,
                buyer TEXT,
                note TEXT NOT NULL,
                idPT INTEGER NOT NULL,
                animal_id INTEGER,
                animal_count_id INTEGER,
                FOREIGN KEY(idPT) REFERENCES ProjectTable(_id) ON DELETE CASCADE,
                FOREIGN KEY(animal_id) REFERENCES animal_table(id) ON DELETE CASCADE
                FOREIGN KEY(animal_count_id) REFERENCES animal_count_table(id) ON DELETE CASCADE
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
        db.execSQL("CREATE INDEX index_sale_table_animal_count_id ON sale_table(animal_count_id)")
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
                count_suffix INTEGER NOT NULL,
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
                idPT INTEGER NOT NULL,
                animalId INTEGER,
                animal_vaccination_id INTEGER,
                animal_count_id INTEGER,
                FOREIGN KEY(idPT) REFERENCES ProjectTable(_id) ON DELETE CASCADE,
                FOREIGN KEY(animalId) REFERENCES AnimalTable(id) ON DELETE CASCADE,
                FOREIGN KEY(animal_vaccination_id) REFERENCES AnimalVaccinationTable(id) ON DELETE CASCADE,
                FOREIGN KEY(animal_count_id) REFERENCES animal_count_table(id) ON DELETE CASCADE,
            )""".trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO MyFermaEXPENSES_new (
                _id, title, count, day, month, year, price, price_all,
                count_suffix, category, note,
                is_show_food, is_show_food_hand, is_show_warehouse, is_show_animals,
                feed_food, feed_food_suffix, count_animal,
                food_designed_day, last_day_food, weight,  weight_suffix,idPT, animalId, animal_vaccination_id, animal_count_id
            )
            SELECT
                _id, titleEXPENSES, discEXPENSES, DAY, MOUNT, YEAR, countEXPENSES, NULL,
                suffix, category, note,
                showFood, dailyExpensesFoodAndCount, showWarehouse, showAnimals,
                dailyExpensesFood, 'кг.', countAnimal,
                foodDesignedDay, lastDayFood, NULL, NULL, idPT, NULL, NULL, NULL
            FROM MyFermaEXPENSES
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX index_MyFermaEXPENSES_new_idPT ON MyFermaEXPENSES_new(idPT)")
        db.execSQL("CREATE INDEX index_MyFermaEXPENSES_new_animalId ON MyFermaEXPENSES_new(animalId)")
        db.execSQL("CREATE INDEX index_MyFermaEXPENSES_new_animal_vaccination_id ON MyFermaEXPENSES_new(animal_vaccination_id)")
        db.execSQL("CREATE INDEX index_MyFermaEXPENSES_new_animal_count_id ON MyFermaEXPENSES_new(animal_count_id)")
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
                idPT,
               _id AS animalId
            FROM AnimalTable
            WHERE LENGTH(data) = 10 AND instr(data, '.') = 3
        """.trimIndent()
        )
    }
}