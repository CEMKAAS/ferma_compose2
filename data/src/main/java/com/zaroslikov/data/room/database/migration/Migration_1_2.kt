package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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