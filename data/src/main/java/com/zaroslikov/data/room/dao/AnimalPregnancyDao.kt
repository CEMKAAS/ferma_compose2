package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.table.animal.AnimalCountTable
import com.zaroslikov.data.room.table.animal.AnimalPregnancyNoteTable
import com.zaroslikov.data.room.table.animal.AnimalPregnancyTable
import com.zaroslikov.data.room.table.animal.AnimalTable
import com.zaroslikov.domain.models.animal.pregnancy.LitterMode
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyBirth
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyDates
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyNorms
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyRules
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Беременности самок. Каждая операция — одна транзакция; нарушение правил бросает
 * [IllegalStateException], и транзакция откатывается.
 *
 * Инварианты: беременность бывает только у самки-особи (sex = 0, is_group = 0) вида-млекопитающего,
 * вид беременности совпадает с видом самки; у самки не больше одной ACTIVE (status = 0) —
 * проверяется здесь, а не уникальным индексом (Room сверяет индексы со схемой).
 * Коды в SQL: status 0 = ACTIVE, 1 = BIRTH, 2 = INTERRUPTED.
 */
@Dao
interface AnimalPregnancyDao {

    // region Запросы

    /** Новые сверху. Дата dd.MM.yyyy → yyyyMMdd для сортировки. */
    @Query(
        "SELECT * FROM animal_pregnancy_table WHERE animal_id = :animalId " +
                "ORDER BY $PREGNANCY_MATING_SORT_KEY DESC, id DESC"
    )
    fun getPregnancies(animalId: Long): Flow<List<AnimalPregnancyTable>>

    @Query("SELECT * FROM animal_pregnancy_table WHERE animal_id = :animalId AND status = 0 ORDER BY id DESC LIMIT 1")
    fun getActivePregnancy(animalId: Long): Flow<AnimalPregnancyTable?>

    @Query("SELECT * FROM animal_pregnancy_table WHERE idPT = :idPT AND status = 0 ORDER BY id DESC")
    fun getActivePregnanciesByProject(idPT: Long): Flow<List<AnimalPregnancyTable>>

    @Query("SELECT * FROM animal_pregnancy_table WHERE id = :id")
    suspend fun getPregnancy(id: Long): AnimalPregnancyTable?

    /** Активные беременности у неархивных самок-особей в неархивных проектах. */
    @Query(
        "SELECT p.* FROM animal_pregnancy_table p " +
                "JOIN animal_table a ON a.id = p.animal_id " +
                "JOIN project_table pr ON pr.id = p.idPT " +
                "WHERE p.status = 0 AND a.archive = 0 AND a.sex = 0 AND a.is_group = 0 AND pr.archive = 0"
    )
    suspend fun getActiveForReminders(): List<AnimalPregnancyTable>

    @Query(
        "SELECT COUNT(*) > 0 FROM animal_pregnancy_table p " +
                "JOIN animal_table a ON a.id = p.animal_id " +
                "JOIN project_table pr ON pr.id = p.idPT " +
                "WHERE p.id = :pregnancyId AND p.mating_date = :matingDate " +
                "AND p.status = 0 AND a.archive = 0 AND a.sex = 0 AND a.is_group = 0 AND pr.archive = 0"
    )
    suspend fun isReminderValid(pregnancyId: Long, matingDate: String): Boolean

    @Query("SELECT id FROM animal_pregnancy_table WHERE animal_id = :animalId AND status = 0 ORDER BY id DESC LIMIT 1")
    suspend fun getActivePregnancyId(animalId: Long): Long?

    /** Самцы-особи того же проекта и вида, включая архивных (они в конце). */
    @Query(
        "SELECT * FROM animal_table WHERE idPT = :idPT AND species = :species AND sex = 1 " +
                "AND is_group = 0 AND id != :excludeId ORDER BY archive ASC, name COLLATE NOCASE"
    )
    fun getSireCandidates(idPT: Long, species: AnimalSpecies, excludeId: Long): Flow<List<AnimalTable>>

    @Query("SELECT * FROM animal_table WHERE mother_id = :animalId OR father_id = :animalId ORDER BY id DESC")
    fun getChildren(animalId: Long): Flow<List<AnimalTable>>

    @Query("SELECT * FROM animal_table WHERE id = :id")
    fun getAnimalOrNull(id: Long): Flow<AnimalTable?>

    @Query("SELECT * FROM animal_table WHERE id = :id")
    suspend fun getAnimalRow(id: Long): AnimalTable?

    @Query("SELECT COUNT(*) FROM animal_pregnancy_table WHERE animal_id = :animalId AND status = 0")
    suspend fun countActive(animalId: Long): Int

    @Query("SELECT * FROM animal_pregnancy_table")
    fun getAllForExport(): Flow<List<AnimalPregnancyTable>>

    /** Заметки всех беременностей самки: новые сверху (по дате, времени — без времени ниже, затем по id). */
    @Query(
        "SELECT n.* FROM animal_pregnancy_note_table n " +
                "JOIN animal_pregnancy_table p ON p.id = n.pregnancy_id " +
                "WHERE p.animal_id = :animalId " +
                "ORDER BY $PREGNANCY_NOTE_SORT_KEY DESC, n.time DESC, n.id DESC"
    )
    fun getNotesByAnimal(animalId: Long): Flow<List<AnimalPregnancyNoteTable>>

    @Query("SELECT * FROM animal_pregnancy_note_table WHERE id = :id")
    suspend fun getNote(id: Long): AnimalPregnancyNoteTable?

    @Query("SELECT * FROM animal_pregnancy_note_table")
    fun getAllNotesForExport(): Flow<List<AnimalPregnancyNoteTable>>

    // endregion

    // region Изменения (только внутри транзакций ниже)

    @Insert
    suspend fun insertPregnancy(pregnancy: AnimalPregnancyTable): Long

    @Insert
    suspend fun insertAnimal(animal: AnimalTable): Long

    @Insert
    suspend fun insertCount(count: AnimalCountTable): Long

    @Query(
        "UPDATE animal_pregnancy_table SET mating_date = :matingDate, sire_id = :sireId, " +
                "sire_name = :sireName, note = :note WHERE id = :id AND status = 0"
    )
    suspend fun updateActive(id: Long, matingDate: String, sireId: Long?, sireName: String?, note: String): Int

    @Query("UPDATE animal_pregnancy_table SET note = :note WHERE id = :id")
    suspend fun updateNote(id: Long, note: String): Int

    @Query(
        "UPDATE animal_pregnancy_table SET status = 2, end_date = :date, " +
                "interrupt_reason = :reason, note = :note WHERE id = :id AND status = 0"
    )
    suspend fun closeAsInterrupted(id: Long, date: String, reason: PregnancyInterruptReason, note: String): Int

    @Query(
        "UPDATE animal_pregnancy_table SET status = 1, end_date = :date, born_female = :female, " +
                "born_male = :male, stillborn = :stillborn, note = :note WHERE id = :id AND status = 0"
    )
    suspend fun closeAsBirth(id: Long, date: String, female: Int, male: Int, stillborn: Int, note: String): Int

    @Query("DELETE FROM animal_pregnancy_table WHERE id = :id")
    suspend fun deletePregnancy(id: Long)

    @Query("UPDATE animal_table SET archive = 1 WHERE id = :id")
    suspend fun archiveAnimal(id: Long): Int

    @Upsert
    suspend fun insertAll(list: List<AnimalPregnancyTable>)

    @Query("DELETE FROM animal_pregnancy_table")
    suspend fun deleteAll()

    @Insert
    suspend fun insertNoteRow(note: AnimalPregnancyNoteTable): Long

    @Query("UPDATE animal_pregnancy_note_table SET date = :date, time = :time, note = :note WHERE id = :id")
    suspend fun updateNoteRow(id: Long, date: String, time: String?, note: String): Int

    @Query("DELETE FROM animal_pregnancy_note_table WHERE id = :id")
    suspend fun deleteNote(id: Long)

    @Upsert
    suspend fun insertAllNotes(list: List<AnimalPregnancyNoteTable>)

    @Query("DELETE FROM animal_pregnancy_note_table")
    suspend fun deleteAllNotes()

    // endregion

    // region Операции

    /** Отмечает случку. Вид и проект берутся у самки. Возвращает id беременности. */
    @Transaction
    suspend fun startPregnancy(pregnancy: AnimalPregnancyTable): Long {
        val female = requireFemale(pregnancy.animalId)
        checkMatingDate(pregnancy.matingDate, female)
        check(!female.archive) { "Самка ${female.id} в архиве" }
        check(countActive(female.id) == 0) { "У самки ${female.id} уже есть активная беременность" }
        val (sireId, sireName) = resolveSire(female, pregnancy.sireId, pregnancy.sireName)
        return insertPregnancy(
            pregnancy.copy(
                id = 0,
                species = female.species,
                sireId = sireId,
                sireName = sireName,
                status = PregnancyStatus.ACTIVE,
                endDate = null,
                interruptReason = null,
                bornFemale = 0,
                bornMale = 0,
                stillborn = 0,
                note = pregnancy.note.trim(),
                idPT = female.idPT
            )
        )
    }

    /**
     * ACTIVE: дата случки, самец и заметка (новый самец проверяется заново, имя — снимок);
     * завершённая: только заметка.
     */
    @Transaction
    suspend fun updatePregnancy(pregnancy: AnimalPregnancyTable) {
        val current = checkNotNull(getPregnancy(pregnancy.id)) { "Беременность ${pregnancy.id} не найдена" }
        val note = pregnancy.note.trim()
        if (current.status != PregnancyStatus.ACTIVE) {
            check(updateNote(current.id, note) == 1) { "Беременность ${current.id} не обновлена" }
            return
        }
        val female = requireFemale(current.animalId)
        checkMatingDate(pregnancy.matingDate, female)
        check(female.species == current.species) { "Вид самки ${female.id} изменился" }
        val (sireId, sireName) = if (pregnancy.sireId != null && pregnancy.sireId == current.sireId) {
            // Прежний самец допустим, даже если больше не подходит; снимок имени — актуальный
            pregnancy.sireId to (getAnimalRow(pregnancy.sireId)?.name ?: current.sireName)
        } else {
            resolveSire(female, pregnancy.sireId, pregnancy.sireName)
        }
        check(updateActive(current.id, pregnancy.matingDate, sireId, sireName, note) == 1) {
            "Беременность ${current.id} уже завершена"
        }
    }

    /** Причина — из норм вида; дата — от случки до сегодня. */
    @Transaction
    suspend fun interrupt(id: Long, date: String, reason: PregnancyInterruptReason, note: String) {
        val pregnancy = checkNotNull(getPregnancy(id)) { "Беременность $id не найдена" }
        check(pregnancy.status == PregnancyStatus.ACTIVE) { "Беременность $id уже завершена" }
        checkEndDate(date, pregnancy.matingDate)
        val norms = checkNotNull(PregnancyNorms.of(pregnancy.species)) { "Нет норм вида ${pregnancy.species}" }
        check(reason in norms.interruptReasons) { "Причина $reason недопустима для ${pregnancy.species}" }
        check(closeAsInterrupted(id, date, reason, note.trim()) == 1) {
            "Беременность $id не найдена или уже завершена"
        }
    }

    /** Гибель, забой, продажа самки: прерывание беременности и архивация самки — атомарно. */
    @Transaction
    suspend fun interruptAndArchive(
        pregnancyId: Long,
        reason: PregnancyInterruptReason,
        date: String,
        note: String,
        animalId: Long
    ) {
        val pregnancy = checkNotNull(getPregnancy(pregnancyId)) { "Беременность $pregnancyId не найдена" }
        check(pregnancy.animalId == animalId) { "Беременность $pregnancyId не принадлежит животному $animalId" }
        interrupt(pregnancyId, date, reason, note)
        check(archiveAnimal(animalId) == 1) { "Животное $animalId не найдено" }
    }

    /**
     * Роды: создаёт живой приплод (особями или группой-помётом) со стартовой записью счётчика ADD
     * и закрывает беременность. Приплод не входит в группу матери. Возвращает id созданных животных.
     */
    @Transaction
    suspend fun registerBirth(birth: PregnancyBirth): List<Long> {
        check(birth.female >= 0 && birth.male >= 0 && birth.stillborn >= 0) { "Отрицательное количество" }
        check(birth.alive + birth.stillborn >= 1) { "Не указан ни один детёныш" }
        check(birth.alive <= PregnancyRules.MAX_LITTER) {
            "Нельзя записать больше ${PregnancyRules.MAX_LITTER} детёнышей за раз"
        }
        val pregnancy = checkNotNull(getPregnancy(birth.pregnancyId)) {
            "Беременность ${birth.pregnancyId} не найдена"
        }
        check(pregnancy.status == PregnancyStatus.ACTIVE) { "Беременность ${pregnancy.id} уже завершена" }
        checkEndDate(birth.date, pregnancy.matingDate)
        val mother = requireFemale(pregnancy.animalId)
        check(!mother.archive) { "Самка ${mother.id} в архиве" }
        check(mother.species == pregnancy.species) { "Вид самки ${mother.id} изменился" }

        val ids = when {
            birth.alive == 0 -> emptyList()
            birth.mode == LitterMode.INDIVIDUALS -> {
                check(birth.names.size == birth.alive) { "Имён ${birth.names.size}, детёнышей ${birth.alive}" }
                check(birth.names.none { it.isBlank() }) { "У детёныша пустое имя" }
                birth.names.mapIndexed { i, name ->
                    val sex = i >= birth.female
                    insertYoung(mother, pregnancy, birth, name.trim(), isGroup = false, sex = sex, count = 1)
                }
            }
            else -> {
                check(birth.groupName.isNotBlank()) { "Пустое имя группы-помёта" }
                listOf(
                    insertYoung(
                        mother, pregnancy, birth, birth.groupName.trim(),
                        isGroup = true, sex = false, count = birth.alive
                    )
                )
            }
        }
        check(
            closeAsBirth(
                pregnancy.id, birth.date, birth.female, birth.male, birth.stillborn, birth.note.trim()
            ) == 1
        ) { "Беременность ${pregnancy.id} уже завершена" }
        return ids
    }

    @Transaction
    suspend fun clearAndInsertForImport(list: List<AnimalPregnancyTable>) {
        deleteAll()
        insertAll(list)
    }

    /** Заметка за день: дата — от случки до сегодня и не позже родов/прерывания. Возвращает id. */
    @Transaction
    suspend fun insertDayNote(note: AnimalPregnancyNoteTable): Long {
        val text = note.note.trim()
        check(text.isNotEmpty()) { "Пустая заметка" }
        checkNoteDate(note.date, requirePregnancy(note.pregnancyId))
        return insertNoteRow(note.copy(id = 0, time = PregnancyDates.normalizeTime(note.time), note = text))
    }

    /** Меняются день, время и текст; беременность заметки — прежняя. */
    @Transaction
    suspend fun updateDayNote(note: AnimalPregnancyNoteTable) {
        val current = checkNotNull(getNote(note.id)) { "Заметка ${note.id} не найдена" }
        val text = note.note.trim()
        check(text.isNotEmpty()) { "Пустая заметка" }
        checkNoteDate(note.date, requirePregnancy(current.pregnancyId))
        check(updateNoteRow(current.id, note.date, PregnancyDates.normalizeTime(note.time), text) == 1) {
            "Заметка ${current.id} не обновлена"
        }
    }

    @Transaction
    suspend fun clearAndInsertNotesForImport(list: List<AnimalPregnancyNoteTable>) {
        deleteAllNotes()
        insertAllNotes(list)
    }

    // endregion
}

/** dd.MM.yyyy → yyyyMMdd: строковая сортировка совпадает с хронологической. */
private const val PREGNANCY_MATING_SORT_KEY =
    "substr(mating_date, 7, 4) || substr(mating_date, 4, 2) || substr(mating_date, 1, 2)"

private const val PREGNANCY_NOTE_SORT_KEY =
    "substr(n.date, 7, 4) || substr(n.date, 4, 2) || substr(n.date, 1, 2)"

private suspend fun AnimalPregnancyDao.requirePregnancy(id: Long): AnimalPregnancyTable =
    checkNotNull(getPregnancy(id)) { "Беременность $id не найдена" }

/**
 * Дата заметки ([PregnancyRules.noteDateError]). Нечитаемые даты случки/окончания (старые данные)
 * не мешают — тогда проверяется только то, что читается.
 */
private fun checkNoteDate(date: String, pregnancy: AnimalPregnancyTable) {
    val day = parseDate(date)
    val error = PregnancyRules.noteDateError(
        date = day,
        mating = PregnancyDates.parseOrNull(pregnancy.matingDate) ?: day,
        end = pregnancy.endDate?.let { PregnancyDates.parseOrNull(it) },
        today = LocalDate.now()
    )
    check(error == null) { "Дата заметки «$date»: $error" }
}

private fun parseDate(date: String): LocalDate =
    checkNotNull(PregnancyDates.parseOrNull(date)) { "Неверная дата «$date»" }

/** Дата случки: читается, не в будущем, не раньше рождения самки ([PregnancyRules.matingDateError]). */
private fun checkMatingDate(date: String, female: AnimalTable) {
    val error = PregnancyRules.matingDateError(
        parseDate(date), PregnancyDates.parseOrNull(female.date), LocalDate.now()
    )
    check(error == null) { "Дата случки «$date»: $error" }
}

/**
 * Дата родов/прерывания: от случки до сегодня ([PregnancyRules.endDateError]). Нечитаемая дата
 * случки (старые данные) не мешает закрыть запись — тогда проверяется только «не в будущем».
 */
private fun checkEndDate(date: String, matingDate: String) {
    val end = parseDate(date)
    val mating = PregnancyDates.parseOrNull(matingDate) ?: end
    val error = PregnancyRules.endDateError(end, mating, LocalDate.now())
    check(error == null) { "Дата «$date»: $error" }
}

/** Самка-особь вида-млекопитающего (архив проверяет вызывающий). */
private suspend fun AnimalPregnancyDao.requireFemale(animalId: Long): AnimalTable {
    val female = checkNotNull(getAnimalRow(animalId)) { "Животное $animalId не найдено" }
    check(!female.isGroup) { "Животное $animalId — группа" }
    check(!female.sex) { "Животное $animalId — самец" }
    check(female.species.isMammal) { "Вид животного $animalId не млекопитающее" }
    return female
}

/**
 * Самец из хозяйства проверяется ([PregnancyRules.isValidSire]) и даёт снимок имени;
 * без него — имя чужого производителя / ИО (пустое → null).
 */
private suspend fun AnimalPregnancyDao.resolveSire(
    female: AnimalTable,
    sireId: Long?,
    sireName: String?
): Pair<Long?, String?> {
    if (sireId == null) return null to sireName?.trim()?.takeIf { it.isNotEmpty() }
    val sire = checkNotNull(getAnimalRow(sireId)) { "Самец $sireId не найден" }
    check(PregnancyRules.isValidSire(female.toDomainMap(), sire.toDomainMap())) {
        "Животное $sireId не может быть отцом"
    }
    return sire.id to sire.name
}

private suspend fun AnimalPregnancyDao.insertYoung(
    mother: AnimalTable,
    pregnancy: AnimalPregnancyTable,
    birth: PregnancyBirth,
    name: String,
    isGroup: Boolean,
    sex: Boolean,
    count: Int
): Long {
    val id = insertAnimal(
        AnimalTable(
            name = name,
            type = mother.species.nameFor(isGroup = isGroup, sex = sex),
            species = mother.species,
            // Порода наследуется от матери (NULL, если у неё не указана)
            breed = mother.breed,
            date = birth.date,
            dateFactory = null,
            isGroup = isGroup,
            sex = sex,
            note = "",
            imagePath = null,
            currentIcon = mother.currentIcon,
            archive = false,
            foodDay = 0.0,
            foodDaySuffix = mother.foodDaySuffix,
            idPT = mother.idPT,
            motherId = mother.id,
            motherName = mother.name,
            fatherId = pregnancy.sireId,
            fatherName = pregnancy.sireName
        )
    )
    insertCount(
        AnimalCountTable(
            count = count.toString(),
            suffix = Suffix.PIECES,
            date = birth.date,
            note = birth.countNote,
            version = AnimalCountVersion.ADD,
            animalId = id
        )
    )
    return id
}
