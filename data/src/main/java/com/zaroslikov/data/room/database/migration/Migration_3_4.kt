package com.zaroslikov.data.room.database.migration


import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        //==================== Миграция ProjectTable ====================
        db.execSQL("DROP TABLE IF EXISTS project_table")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS project_table(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                date TEXT NOT NULL,
                date_end TEXT NOT NULL,
                mode INTEGER NOT NULL,
                archive INTEGER NOT NULL,
                imagePath TEXT,
                currentIcon INTEGER
            )
        """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO project_table(
                id, title, date, date_end, mode, archive, imagePath, currentIcon
            )
            SELECT _id, NAME, DATA, DATAEND, mode, 
            CASE
               WHEN arhive = '0' THEN 0
               ELSE 1
            END, NULL, NULL FROM МyINCUBATOR WHERE mode = 1
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO project_table(
              title, date, date_end, mode, archive, imagePath, currentIcon
            )
            SELECT '7bc20e66-fc56-4002-ac33-4cc15dd28213', '05.05.2026', '05.05.2026', 0, 0, NULL, NULL
        """.trimIndent()
        )

        //==================== Миграция IncubatorTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS incubator_table(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                brand TEXT,
                model TEXT,
                capacity INTEGER NOT NULL,
                price REAL,
                note TEXT NOT NULL,
                is_auto_rotation INTEGER NOT NULL,
                is_auto_ventilation INTEGER NOT NULL,
                currency_suffix INTEGER NOT NULL,
                idPT INTEGER NOT NULL,
                FOREIGN KEY(idPT) REFERENCES project_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_incubator_table_idPT ON incubator_table(idPT)")

        db.execSQL(
            """
            INSERT INTO incubator_table(
                title, brand, model, capacity, price, note, is_auto_rotation, is_auto_ventilation,
                currency_suffix, idPT  
            ) 
            SELECT
              'Инкубатор', NULL, NULL,  1000, NULL, '', 0, 0,
               13, id
               FROM project_table WHERE title = '7bc20e66-fc56-4002-ac33-4cc15dd28213'
        """.trimIndent()
        )

        db.execSQL(
            """
            UPDATE project_table 
            SET title = 'Инкубатор'
            WHERE id = (
                SELECT id FROM project_table 
                WHERE title = '7bc20e66-fc56-4002-ac33-4cc15dd28213'
                LIMIT 1
            )""".trimIndent()
        )

        //==================== Миграция BookmarkTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS bookmark_incubator(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                type INTEGER NOT NULL,
                breed TEXT,
                count INTEGER NOT NULL,
                rejected_count INTEGER NOT NULL,
                start_date TEXT NOT NULL,
                end_date TEXT NOT NULL,
                is_early_completion_status INTEGER NOT NULL,
                time TEXT NOT NULL,
                price REAL,
                price_all REAL,
                price_suffix INTEGER,
                chick_price REAL,
                note TEXT NOT NULL,
                is_auto_rotation INTEGER NOT NULL,
                is_auto_ventilation INTEGER NOT NULL,
                is_activity_bookmark INTEGER NOT NULL,
                idPT INTEGER NOT NULL,
                FOREIGN KEY(idPT) REFERENCES incubator_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_bookmark_incubator_idPT ON bookmark_incubator(idPT)")
        db.execSQL(
            """
              INSERT INTO bookmark_incubator(
                   id, title, type, breed, count, rejected_count, start_date, end_date,
                   is_early_completion_status, time, price, price_all, chick_price, note,
                   is_auto_rotation, is_auto_ventilation,  is_activity_bookmark, idPT
              )
                SELECT
                   _id, NAME,
                   CASE
                   WHEN type = 'Курицы' THEN 0
                   WHEN type = 'Гуси' THEN 1
                   WHEN type = 'Перепела' THEN 2
                   WHEN type = 'Утки' THEN 3
                   WHEN type = 'Индюки' THEN 4
                   ELSE 0 END,
                   NULL,
                   EGGALL, 0, DATA, DATAEND,
                   0, '12:00', NULL, NULL, NULL, '',
                   CASE
                   WHEN type = 'false' THEN 0
                   WHEN type = 'true' THEN 1
                   ELSE 0 END,
                   CASE
                   WHEN type = 'false' THEN 0
                   WHEN type = 'true' THEN 1
                   ELSE 0 END,
                   0, 1
                  FROM МyINCUBATOR WHERE mode = 0
          """.trimIndent()
        )

        //==================== Миграция TimeNotificationIncubatorTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS time_notification_incubator_table(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                time TEXT NOT NULL,
                note TEXT,
                bookmark_id INTEGER NOT NULL,
                FOREIGN KEY(bookmark_id) REFERENCES bookmark_incubator(id) ON DELETE CASCADE
                )
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_time_notification_incubator_table_bookmark_id ON time_notification_incubator_table(bookmark_id)")

        db.execSQL(
            """
              INSERT INTO time_notification_incubator_table(
                time, note, bookmark_id
              )
                SELECT '12:00',null,_id FROM МyINCUBATOR WHERE mode = 0
          """.trimIndent()
        )
        //==================== Миграция TimeNotificationTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS time_notification_project_table(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                time TEXT NOT NULL,
                note TEXT,
                project_id INTEGER NOT NULL,
                FOREIGN KEY(project_id) REFERENCES project_table(id) ON DELETE CASCADE
                )
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_time_notification_project_table_bookmark_id ON time_notification_project_table(project_id)")

        //==================== Миграция IncubatorParameters ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS incubator_parameters(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                day INTEGER NOT NULL,
                temp TEXT NOT NULL,
                damp TEXT NOT NULL,
                over TEXT NOT NULL,
                airing TEXT NOT NULL,
                temp_fact TEXT NOT NULL,
                damp_fact TEXT NOT NULL,
                over_fact TEXT NOT NULL,
                airing_fact TEXT NOT NULL,
                note TEXT NOT NULL,
                idPT INTEGER NOT NULL,
                FOREIGN KEY(idPT) REFERENCES bookmark_incubator(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_incubator_parameters_idPT ON incubator_parameters(idPT)")
        db.execSQL(
            """
            INSERT INTO incubator_parameters(
                 id, day, temp, damp, over, airing, 
                 temp_fact, damp_fact, over_fact, airing_fact, note, idPT
            )
            SELECT
               id, day, temp, damp, over, airing, 
               temp, damp, over, airing, '', idPT
               FROM MyIncubator 
        """.trimIndent()
        )

        //==================== Миграция AnimalTable ====================
        db.execSQL("DROP TABLE IF EXISTS animal_table")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS animal_table(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                date TEXT NOT NULL,
                date_factory TEXT,
                is_group INTEGER NOT NULL,
                sex INTEGER NOT NULL,
                note TEXT NOT NULL,
                image_path TEXT,
                icon INTEGER,
                archive INTEGER NOT NULL,
                food_day REAL NOT NULL,
                food_day_suffix INTEGER NOT NULL,
                idPT INTEGER NOT NULL,
                price REAL NOT NULL,
                FOREIGN KEY(idPT) REFERENCES project_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_animal_table_idPT ON animal_table(idPT)")

        db.execSQL(
            """
            INSERT INTO animal_table (
                id, name, type, date, date_factory, is_group, sex,
                note, image_path, icon, archive, food_day, food_day_suffix, price, idPT
            )
            SELECT
                id, name, type, data, NULL, groop, (CASE WHEN sex IS 'Мужской' THEN 0 ELSE 1 END) AS sex, 
                note, NULL, NULL, arhiv, foodDay, 18, price, idPT
            FROM AnimalTable
        """.trimIndent()
        )
        db.execSQL("DROP TABLE AnimalTable")

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
                FOREIGN KEY(animal_id) REFERENCES animal_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_animal_count_table_animal_id ON animal_count_table(animal_id)")
        db.execSQL(
            """
            INSERT INTO animal_count_table(
                id, count, suffix, date, note, version, animal_id
            )
            SELECT
                id, count, 3, date, '', 4, idAnimal
            FROM AnimalCountTable
        """.trimIndent()
        )
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
                FOREIGN KEY(animal_id) REFERENCES animal_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_animal_vaccination_table_animal_id ON animal_vaccination_table(animal_id)")

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
                FOREIGN KEY(animal_id) REFERENCES animal_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_animal_weight_table_animal_id ON animal_weight_table(animal_id)")

        db.execSQL(
            """
            INSERT INTO animal_weight_table(
                id, weight, suffix, date, animal_id, note
            )
            SELECT
                id, weight, 5, date, idAnimal, ''
            FROM AnimalWeightTable
        """.trimIndent()
        )
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
                FOREIGN KEY(animal_id) REFERENCES animal_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_animal_size_table_animal_id ON animal_size_table(animal_id)")

        db.execSQL(
            """
            INSERT INTO animal_size_table(
                id, size, suffix, date, animal_id, note
            )
            SELECT
                id, size, 12, date, idAnimal, ''
            FROM AnimalSizeTable
        """.trimIndent()
        )
        db.execSQL("DROP TABLE AnimalSizeTable")


        //==================== Миграция AddTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS add_table(
                _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                count REAL NOT NULL,
                day INTEGER NOT NULL,
                month INTEGER NOT NULL,
                year INTEGER NOT NULL,
                price REAL NOT NULL,
                count_suffix INTEGER NOT NULL,
                price_suffix INTEGER NOT NULL,
                category TEXT,
                animal_id INTEGER,
                note TEXT NOT NULL,
                idPT INTEGER NOT NULL,
                animal_count_id INTEGER,
                FOREIGN KEY(idPT) REFERENCES project_table(id) ON DELETE CASCADE,
                FOREIGN KEY(animal_id) REFERENCES animal_table(id) ON DELETE SET NULL,
                FOREIGN KEY(animal_count_id) REFERENCES animal_count_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_add_table_idPT ON add_table(idPT)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_add_table_animal_id ON add_table(animal_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_add_table_animal_count_id ON add_table(animal_count_id)")

        db.execSQL(
            """
            INSERT INTO add_table (
                _id, title, count, day, month, year, price,
                count_suffix, price_suffix, category, animal_id, note, idPT
            )
            SELECT
                _id, title, disc, DAY, MOUNT, YEAR, PRICE,
                CASE
                   WHEN suffix = 'Шт.' THEN 1
                   WHEN suffix = 'Кг.' THEN 5
                   WHEN suffix = 'Л.' THEN 8
                   ELSE 1
                END,
                13, 
                CASE 
                   WHEN category IS NULL OR TRIM(category) = '' OR TRIM(category)= 'Без категории'
                   THEN NULL
                   ELSE category 
                END,
               NULLIF(idAnimal, 0), 
               note, idPT
            FROM MyFerma
        """.trimIndent()
        )
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
                price_suffix INTEGER,
                category TEXT,
                day INTEGER NOT NULL,
                month INTEGER NOT NULL,
                year INTEGER NOT NULL,
                status INTEGER NOT NULL,
                note TEXT NOT NULL,
                idPT INTEGER NOT NULL,
                animal_count_id INTEGER,
                FOREIGN KEY(idPT) REFERENCES project_table(id) ON DELETE CASCADE,
                FOREIGN KEY(animal_count_id) REFERENCES animal_count_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )

        // 2. Копируем данные из старой таблицы
        db.execSQL(
            """
            INSERT INTO write_off_table (
                _id, title, count, count_suffix, price, price_all, price_suffix, category, day, month, year,
                status, note, idPT, animal_count_id
            )
            SELECT _id, titleWRITEOFF, discWRITEOFF, 
               CASE
                   WHEN suffix = 'Шт.' THEN 1
                   WHEN suffix = 'Кг.' THEN 5
                   WHEN suffix = 'Л.' THEN 8
                   ELSE 1
               END,
            priceAll, NULL, 
            CASE WHEN priceAll IS NULL THEN NULL ELSE 13 END,
            NULL, DAY, MOUNT, YEAR,
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
                price_suffix INTEGER NOT NULL,
                day INTEGER NOT NULL,
                month INTEGER NOT NULL,
                year INTEGER NOT NULL,
                category TEXT,
                buyer TEXT,
                note TEXT NOT NULL,
                idPT INTEGER NOT NULL,
                animal_id INTEGER,
                animal_count_id INTEGER,
                FOREIGN KEY(idPT) REFERENCES project_table(id) ON DELETE CASCADE,
                FOREIGN KEY(animal_id) REFERENCES animal_table(id) ON DELETE CASCADE
                FOREIGN KEY(animal_count_id) REFERENCES animal_count_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_sale_table_idPT ON sale_table(idPT)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_sale_table_animalId ON sale_table(animal_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_sale_table_animal_count_id ON sale_table(animal_count_id)")

        db.execSQL(
            """
            INSERT INTO sale_table(
                _id, title, count, count_suffix, price, price_all, price_suffix, day, month, year,
                category, buyer, note, idPT, animal_id, animal_count_id
            )
            SELECT
                _id, titleSale, discSale,
                  CASE
                   WHEN suffix = 'Шт.' THEN 1
                   WHEN suffix = 'Кг.' THEN 5
                   WHEN suffix = 'Л.' THEN 8
                   WHEN suffix = 'м3' THEN 9
                   WHEN suffix = 'Тн' THEN 6
                   WHEN suffix = 'М.' THEN 12
                   ELSE 1
                  END,
                PRICE, NULL, 13, DAY, MOUNT, YEAR, 
                CASE 
                   WHEN category IS NULL OR TRIM(category) = '' OR TRIM(category)= 'Без категории'
                   THEN NULL
                   ELSE category 
                END,
                CASE 
                   WHEN buyer IS NULL OR TRIM(buyer) = '' OR buyer = 'Неизвестный'
                   THEN NULL
                   ELSE buyer 
                END,
                note, idPT, NULL, NULL
            FROM MyFermaSale
        """.trimIndent()
        )
        db.execSQL("DROP TABLE MyFermaSale")

        //==================== Миграция ExpensesTable ====================
        db.execSQL("DROP TABLE IF EXISTS MyFermaEXPENSES_new")
        db.execSQL("DROP TABLE IF EXISTS expenses_table")
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
                price_suffix INTEGER NOT NULL,
                category TEXT,
                note TEXT NOT NULL,
                is_food INTEGER NOT NULL,
                is_show_food INTEGER NOT NULL,
                feed_food REAL,
                feed_food_suffix INTEGER,
                count_animal INTEGER,
                food_designed_day INTEGER,
                last_day_food TEXT,
                weight REAL,
                weight_suffix INTEGER,
                idPT INTEGER NOT NULL,
                animalId INTEGER,
                animal_vaccination_id INTEGER,
                animal_count_id INTEGER,
                FOREIGN KEY(idPT) REFERENCES project_table(id) ON DELETE CASCADE,
                FOREIGN KEY(animalId) REFERENCES animal_table(id) ON DELETE CASCADE,
                FOREIGN KEY(animal_vaccination_id) REFERENCES animal_vaccination_table(id) ON DELETE CASCADE, 
                FOREIGN KEY(animal_count_id) REFERENCES animal_count_table(id) ON DELETE CASCADE
            )""".trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO MyFermaEXPENSES_new (
                _id, title, count, day, month, year, price, price_all, price_suffix,
                count_suffix, category, note, is_food,
                is_show_food, feed_food, feed_food_suffix, count_animal,
                food_designed_day, last_day_food, weight,  weight_suffix,idPT, animalId, animal_vaccination_id, animal_count_id
            )
            SELECT
                _id, titleEXPENSES, discEXPENSES, DAY, MOUNT, YEAR, countEXPENSES, NULL, 13,
                CASE
                   WHEN suffix = 'Шт.' THEN 1
                   WHEN suffix = 'Кг.' THEN 5
                   WHEN suffix = 'Л.' THEN 8
                   WHEN suffix = 'м3' THEN 9
                   WHEN suffix = 'Тн' THEN 6
                   WHEN suffix = 'М.' THEN 12
                   ELSE 1
                END,  
                CASE 
                   WHEN category IS NULL OR TRIM(category) = '' OR TRIM(category)= 'Без категории'
                   THEN NULL
                   ELSE category 
                END,
                note,
                showFood, showFood,
                CASE WHEN showFood = 1 THEN dailyExpensesFood ELSE NULL END,
                CASE WHEN showFood = 1 THEN 5 ELSE NULL END,
                CASE WHEN showFood = 1 THEN countAnimal ELSE NULL END,
                CASE WHEN showFood = 1 THEN foodDesignedDay ELSE NULL END,
                CASE WHEN showFood = 1 THEN lastDayFood ELSE NULL END,
                NULL, NULL, idPT, NULL, NULL, NULL
            FROM MyFermaEXPENSES
        """.trimIndent()
        )

        db.execSQL("CREATE INDEX IF NOT EXISTS index_MyFermaEXPENSES_new_idPT ON MyFermaEXPENSES_new(idPT)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_MyFermaEXPENSES_new_animalId ON MyFermaEXPENSES_new(animalId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_MyFermaEXPENSES_new_animal_vaccination_id ON MyFermaEXPENSES_new(animal_vaccination_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_MyFermaEXPENSES_new_animal_count_id ON MyFermaEXPENSES_new(animal_count_id)")
        db.execSQL("DROP TABLE MyFermaEXPENSES")
        db.execSQL("ALTER TABLE MyFermaEXPENSES_new RENAME TO expenses_table")

        db.execSQL("DROP TABLE МyINCUBATOR")
        db.execSQL("DROP TABLE MyIncubator")

        // Создаем временную таблицу
        db.execSQL(
            """
            CREATE TABLE ExpensesAnimalTable_new (
                _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                idExpenses INTEGER NOT NULL,
                idAnimal INTEGER NOT NULL,
                percentExpenses REAL NOT NULL,
                idPT INTEGER NOT NULL,
                FOREIGN KEY(idPT) REFERENCES project_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )

        // Копируем данные
        db.execSQL(
            """
            INSERT INTO ExpensesAnimalTable_new (_id, idExpenses, idAnimal, percentExpenses, idPT)
            SELECT _id, idExpenses, idAnimal, percentExpenses, idPT FROM ExpensesAnimalTable
        """.trimIndent()
        )


        // Удаляем старую и переименовываем новую
        db.execSQL("DROP TABLE ExpensesAnimalTable")
        db.execSQL("ALTER TABLE ExpensesAnimalTable_new RENAME TO ExpensesAnimalTable")

        // Восстанавливаем индекс
        db.execSQL("CREATE INDEX IF NOT EXISTS index_ExpensesAnimalTable_idPT ON ExpensesAnimalTable(idPT)")

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
                price_all,
                price_suffix,
                count_suffix,
                category,
                note,
                is_food,
                is_show_food,
                feed_food,
                feed_food_suffix,
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
                    FROM animal_count_table ac
                    WHERE ac.animal_id = id
                    ORDER BY substr(ac.date, 7, 4) || substr(ac.date, 4, 2) || substr(ac.date, 1, 2) DESC
                    LIMIT 1
                ) AS REAL) AS count,
                CAST(substr(date, 1, 2) AS INTEGER) AS day,
                CAST(substr(date, 4, 2) AS INTEGER) AS month,
                CAST(substr(date, 7, 4) AS INTEGER) AS year,
                price,
                NULL,
                13,
                1 AS count_suffix,
                'Покупка животных' AS category,
                note,
                0 AS isFood,
                0 AS is_show_food,
                NULL AS feed_food,
                NULL AS feed_food_suffix,
                NULL AS count_animal,
                NULL AS food_designed_day ,
                NULL AS last_day_food,
                NULL AS weight,
                NULL AS weight_suffix,
                idPT,
               id AS animalId
            FROM animal_table
            WHERE LENGTH(date) = 10 AND instr(date, '.') = 3
        """.trimIndent()
        )

        db.execSQL("ALTER TABLE animal_table DROP COLUMN price")

        //==================== Миграция NoteTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS note_table(
                _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                note TEXT NOT NULL,
                date TEXT NOT NULL,
                idPT INTEGER NOT NULL,
                FOREIGN KEY(idPT) REFERENCES project_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_note_table_idPT ON note_table(idPT)")
        db.execSQL(
            """
            INSERT INTO note_table(
                _id, title, note, date, idPT
            )
            SELECT
                _id, title, note, date, idPT
            FROM NoteFerma
        """.trimIndent()
        )

        db.execSQL("DROP TABLE NoteFerma")

        //==================== Миграция SettingsTable ====================
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS settings_table(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                currency_suffix INTEGER NOT NULL,
                weight_suffix INTEGER NOT NULL,
                volume_suffix INTEGER NOT NULL,
                linear_suffix INTEGER NOT NULL,
                idPT INTEGER NOT NULL,
                FOREIGN KEY(idPT) REFERENCES project_table(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )

        db.execSQL(
            """ CREATE INDEX IF NOT EXISTS index_settings_table_idPT ON settings_table(idPT)"""
                .trimIndent()
        )


        db.execSQL(
            """
            INSERT INTO settings_table(currency_suffix, weight_suffix, volume_suffix, linear_suffix, idPT) 
            Select 13, 5, 8, 12, id FROM project_table WHERE mode = 1
        """.trimIndent()
        )

        //==================== Создание ProfileTable ====================

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS profile_table(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO profile_table(name) 
            VALUES ('Фермер')
        """.trimIndent()
        )

        //==================== Миграция AppSettingsTable ====================

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS app_settings_table(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                last_version_app TEXT,
                current_version_app TEXT NOT NULL,
                is_first_launch INTEGER NOT NULL
                )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO app_settings_table(last_version_app, current_version_app, is_first_launch) 
            VALUES (NULL, '1', 1)
        """.trimIndent()
        )

//        db.execSQL("CREATE INDEX IF NOT EXISTS index_settings_table_idPT ON settings_table(idPT)")
//        db.execSQL("CREATE INDEX IF NOT EXISTS index_ExpensesAnimalTable_idPT ON ExpensesAnimalTable(idPT)")


    }
}