package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.dto.AnimalWithCountDto
import com.zaroslikov.data.room.dto.animal.HerdCountRecordDto
import com.zaroslikov.data.room.dto.animal.HerdProductDto
import com.zaroslikov.data.room.mapper.toAddRoomMap
import com.zaroslikov.data.room.mapper.toExpensesRoomMap
import com.zaroslikov.data.room.mapper.table.toRoomMap
import com.zaroslikov.data.room.table.ferma.AddTable
import com.zaroslikov.data.room.table.ferma.ExpensesTable
import com.zaroslikov.data.room.table.ferma.SaleTable
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.animal.group.DomainHerdOperation
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.data.room.table.animal.AnimalCountTable
import com.zaroslikov.data.room.table.animal.AnimalHerdTable
import com.zaroslikov.data.room.table.animal.AnimalTable
import com.zaroslikov.data.room.table.animal.AnimalWeightTable
import com.zaroslikov.domain.models.animal.group.MAX_SPLIT
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Сборные группы (animal_herd_table) и их состав (animal_table.herd_id). Каждая операция —
 * одна транзакция; нарушение инварианта бросает [IllegalStateException], и транзакция откатывается.
 *
 * Инварианты: член — индивидуальное животное или обезличенная группа того же idPT; вложенных
 * сборных групп нет (herd_id — одна ссылка). Сборная группа без единого члена удаляется.
 * Перевод поголовья учитывается парными записями TRANSFER_IN / TRANSFER_OUT, сумма не меняется.
 */
@Dao
interface AnimalHerdDao {

    // region Запросы

    @Query("SELECT * FROM animal_herd_table WHERE id = :id")
    fun getHerdFlow(id: Long): Flow<AnimalHerdTable?>

    @Query("SELECT * FROM animal_herd_table WHERE id = :id")
    suspend fun getHerd(id: Long): AnimalHerdTable?

    /** Сборные группы проекта, в которых есть хотя бы один неархивный член. */
    @Query(
        "SELECT * FROM animal_herd_table h WHERE h.idPT = :idPT AND EXISTS " +
                "(SELECT 1 FROM animal_table a WHERE a.herd_id = h.id AND a.archive = 0) " +
                "ORDER BY h.id DESC"
    )
    fun getActiveHerds(idPT: Long): Flow<List<AnimalHerdTable>>

    @Query("$ANIMAL_WITH_HERD_SELECT_BY_HERD WHERE an.herd_id = :herdId ORDER BY an.id DESC")
    fun getHerdMembers(herdId: Long): Flow<List<AnimalWithCountDto>>

    @Query(
        "$ANIMAL_WITH_HERD_SELECT_BY_PROJECT WHERE an.idPT = :idPT " +
                "AND an.archive = 0 AND an.herd_id IS NULL ORDER BY an.id DESC"
    )
    fun getStandaloneCandidates(idPT: Long): Flow<List<AnimalWithCountDto>>

    @Query("SELECT * FROM animal_table WHERE id = :id")
    suspend fun getAnimalRow(id: Long): AnimalTable?

    @Query("SELECT * FROM animal_table WHERE id = :id AND is_group = 1 AND archive = 0")
    suspend fun getActiveImpersonalGroup(id: Long): AnimalTable?

    @Query("SELECT * FROM animal_table WHERE herd_id = :herdId AND archive = 0 ORDER BY id")
    suspend fun getActiveMemberRows(herdId: Long): List<AnimalTable>

    @Query("SELECT COUNT(*) FROM animal_table WHERE herd_id = :herdId")
    suspend fun countMembers(herdId: Long): Int

    @Query("WITH $ANIMAL_OWN_CTE_SINGLE SELECT COALESCE((SELECT own_count FROM own WHERE animal_id = :animalId), 0.0)")
    suspend fun getOwnCount(animalId: Long): Double

    /** Код суффикса первой записи счётчика; null — у животного нет истории счётчика. */
    @Query("SELECT suffix FROM animal_count_table WHERE animal_id = :animalId ORDER BY id LIMIT 1")
    suspend fun getFirstCountSuffixCode(animalId: Long): Int?

    /** true — последняя запись счётчика (порядок как в own) без версии; null — записей нет. */
    @Query(
        "SELECT version IS NULL FROM animal_count_table WHERE animal_id = :animalId " +
                "ORDER BY $ANIMAL_COUNT_LAST_FIRST_ORDER LIMIT 1"
    )
    suspend fun isLastCountLegacy(animalId: Long): Boolean?

    @Query(
        "SELECT date FROM animal_count_table WHERE animal_id = :animalId " +
                "ORDER BY $ANIMAL_COUNT_LAST_FIRST_ORDER LIMIT 1"
    )
    suspend fun getLastCountDate(animalId: Long): String?

    /** Сумма записей со знаком версии (записи без версии дают 0) — то, что посчитает SUM-ветка own. */
    @Query(
        "SELECT CAST(COALESCE(SUM($ANIMAL_SIGNED_COUNT), 0) AS REAL) " +
                "FROM animal_count_table WHERE animal_id = :animalId"
    )
    suspend fun getSignedCountSum(animalId: Long): Double

    /** Все взвешивания неархивных членов сборной группы; последнее по каждому выбирает вызывающий. */
    @Query(
        "SELECT w.* FROM animal_weight_table w JOIN animal_table a ON a.id = w.animal_id " +
                "WHERE a.herd_id = :herdId AND a.archive = 0"
    )
    fun getHerdWeights(herdId: Long): Flow<List<AnimalWeightTable>>

    /** Продукция, записанная на членов сборной группы, включая архивных. */
    @Query(
        "SELECT p.animal_id AS animalId, p.title AS title, p.count AS count, " +
                "p.count_suffix AS suffix, p.price AS price " +
                "FROM add_table p JOIN animal_table a ON a.id = p.animal_id " +
                "WHERE a.herd_id = :herdId"
    )
    fun getHerdProducts(herdId: Long): Flow<List<HerdProductDto>>

    /**
     * История поголовья сборной группы: записи счётчиков с её меткой (и архивных, и вышедших
     * членов), с ценой и покупателем связанной продажи/расхода/списания. Новые — первыми.
     */
    @Query(
        "SELECT c.id AS id, c.count AS count, c.suffix AS suffix, c.date AS date, c.note AS note, " +
                "c.version AS version, c.animal_id AS animalId, a.name AS animalName, a.is_group AS is_group, " +
                "c.females_count AS females_count, c.males_count AS males_count, " +
                "CASE WHEN c.version = 0 THEN (SELECT s.price FROM sale_table s WHERE s.animal_count_id = c.id) " +
                "WHEN c.version IN (1, 5) THEN (SELECT e.price FROM expenses_table e WHERE e.animal_count_id = c.id) " +
                "WHEN c.version IN (2, 3) THEN (SELECT w.price FROM write_off_table w WHERE w.animal_count_id = c.id) " +
                "END AS price, " +
                "CASE WHEN c.version = 0 THEN (SELECT s.price_all FROM sale_table s WHERE s.animal_count_id = c.id) " +
                "WHEN c.version IN (1, 5) THEN (SELECT e.price_all FROM expenses_table e WHERE e.animal_count_id = c.id) " +
                "WHEN c.version IN (2, 3) THEN (SELECT w.price_all FROM write_off_table w WHERE w.animal_count_id = c.id) " +
                "END AS price_all, " +
                "CASE WHEN c.version = 0 THEN (SELECT s.buyer FROM sale_table s WHERE s.animal_count_id = c.id) " +
                "END AS buyer " +
                "FROM animal_count_table c JOIN animal_table a ON a.id = c.animal_id " +
                "WHERE c.herd_id = :herdId " +
                "ORDER BY DATE(printf('%04d-%02d-%02d', substr(c.date, 7, 4), substr(c.date, 4, 2), substr(c.date, 1, 2))) DESC, " +
                "c.id DESC"
    )
    fun getHerdHistory(herdId: Long): Flow<List<HerdCountRecordDto>>

    @Query("SELECT * FROM animal_herd_table")
    fun getAllForExport(): Flow<List<AnimalHerdTable>>

    // endregion

    // region Изменения (только внутри транзакций ниже)

    @Insert
    suspend fun insertHerd(herd: AnimalHerdTable): Long

    @Update
    suspend fun updateHerd(herd: AnimalHerdTable)

    @Query("DELETE FROM animal_herd_table WHERE id = :id")
    suspend fun deleteHerd(id: Long)

    @Upsert
    suspend fun insertAllHerds(herds: List<AnimalHerdTable>)

    @Query("DELETE FROM animal_herd_table")
    suspend fun deleteAllHerds()

    @Insert
    suspend fun insertAnimal(animal: AnimalTable): Long

    @Insert
    suspend fun insertCount(count: AnimalCountTable): Long

    @Insert
    suspend fun insertSale(sale: SaleTable): Long

    @Insert
    suspend fun insertWriteOff(writeOff: WriteOffTable): Long

    @Insert
    suspend fun insertExpenses(expenses: ExpensesTable): Long

    @Insert
    suspend fun insertAdd(add: AddTable): Long

    @Query(
        "UPDATE animal_table SET herd_id = :herdId " +
                "WHERE id IN (:ids) AND archive = 0 AND herd_id IS NULL AND idPT = :idPT"
    )
    suspend fun attachMembers(herdId: Long, idPT: Long, ids: List<Long>): Int

    @Query("UPDATE animal_table SET herd_id = :herdId WHERE id = :id")
    suspend fun setHerd(id: Long, herdId: Long)

    @Query("UPDATE animal_table SET herd_id = NULL WHERE id = :memberId AND herd_id IS NOT NULL")
    suspend fun detachMember(memberId: Long): Int

    @Query("UPDATE animal_table SET herd_id = NULL WHERE herd_id = :herdId")
    suspend fun detachAllMembers(herdId: Long): Int

    /** Самки и самцы обезличенной группы: прибавить (отрицательное — убавить, не ниже 0). */
    @Query(
        "UPDATE animal_table SET females_count = MAX(females_count + :females, 0), " +
                "males_count = MAX(males_count + :males, 0) WHERE id = :id"
    )
    suspend fun addSexCounts(id: Long, females: Int, males: Int)

    @Query("UPDATE animal_table SET archive = :archive WHERE id = :id")
    suspend fun setArchive(id: Long, archive: Boolean)

    @Query("DELETE FROM animal_table WHERE id = :id")
    suspend fun deleteAnimalRow(id: Long)

    /** ACTIVE беременности животных [ids] → INTERRUPTED (status = 2) с причиной [reason]. */
    @Query(
        "UPDATE animal_pregnancy_table SET status = 2, end_date = :date, interrupt_reason = :reason " +
                "WHERE status = 0 AND animal_id IN (:ids)"
    )
    suspend fun interruptActivePregnancies(ids: List<Long>, date: String, reason: PregnancyInterruptReason): Int

    // endregion

    // region Операции

    /** Создаёт сборную группу и добавляет в неё самостоятельных животных и обезличенные группы. */
    @Transaction
    suspend fun createHerdWithMembers(herd: AnimalHerdTable, memberIds: List<Long>): Long {
        val ids = memberIds.distinct()
        check(ids.isNotEmpty()) { "Не выбраны животные для сборной группы" }
        val herdId = insertHerd(herd.copy(id = 0))
        val updated = attachMembersChunked(herdId, herd.idPT, ids)
        check(updated == ids.size) {
            "Объединить можно только самостоятельных неархивных животных этого хозяйства ($updated из ${ids.size})"
        }
        val date = today()
        ids.forEach { recordMembership(it, herdId, AnimalCountVersion.HERD_JOIN, date) }
        return herdId
    }

    @Transaction
    suspend fun addMembers(herdId: Long, memberIds: List<Long>) {
        val herd = requireHerd(herdId)
        val ids = memberIds.distinct()
        check(ids.isNotEmpty()) { "Не выбраны животные для сборной группы" }
        val updated = attachMembersChunked(herd.id, herd.idPT, ids)
        check(updated == ids.size) {
            "Добавить можно только самостоятельных неархивных животных этого хозяйства ($updated из ${ids.size})"
        }
        val date = today()
        ids.forEach { recordMembership(it, herd.id, AnimalCountVersion.HERD_JOIN, date) }
    }

    /**
     * Член становится самостоятельным, его счётчики не меняются. В истории группы остаётся
     * HERD_LEAVE с его поголовьем (у архивного — без записи: его поголовье уже выбыло).
     */
    @Transaction
    suspend fun removeMember(memberId: Long) {
        val member = getAnimalRow(memberId)
        val herdId = member?.herdId
        check(herdId != null && detachMember(memberId) == 1) { "Животное $memberId не состоит в сборной группе" }
        if (member?.archive == false) recordMembership(memberId, herdId, AnimalCountVersion.HERD_LEAVE, today())
        deleteHerdIfEmpty(herdId)
    }

    /**
     * Новое животное прямо в сборной группе (форма «Добавить», как в списке животных): само животное,
     * первая запись счётчика с меткой группы — приход или покупка — и расход покупки.
     * Возвращает id животного.
     */
    @Transaction
    suspend fun insertAnimalIntoHerd(
        herdId: Long,
        animal: DomainAnimalTable,
        count: DomainAnimalCount,
        expenses: DomainExpensesTable?
    ): Long {
        val herd = requireHerd(herdId)
        check(animal.idPT == herd.idPT) { "Животное другого хозяйства" }
        check(count.version == AnimalCountVersion.ADD || count.version == AnimalCountVersion.EXPENSES) {
            "Первая запись нового животного — приход или покупка"
        }
        val id = insertAnimal(animal.copy(id = 0, archive = false, herdId = herd.id).toRoomMap())
        val countId = insertCount(count.copy(id = 0, idAnimal = id, herdId = herd.id).toRoomMap())
        expenses?.let { insertExpenses(it.copy(animalId = id, animalCountId = countId).toExpensesRoomMap()) }
        return id
    }

    /** Несколько членов выходят из группы разом: всё или ничего. */
    @Transaction
    suspend fun removeMembers(memberIds: List<Long>) {
        check(memberIds.isNotEmpty()) { "Не выбраны животные" }
        memberIds.distinct().forEach { removeMember(it) }
    }

    /**
     * Корректировка поголовья сборной группы: каждому члену из [operation] — запись счётчика
     * с меткой группы и связанные продажа/списание/расход/продукция забоя. Убыль не больше
     * поголовья члена; приход — только обезличенным группам.
     * Индивидуальные животные, у которых поголовье выбыло, уходят в архив (herd_id сохраняется),
     * их активные беременности прерываются с причиной FEMALE_LOST (напоминания отменяет вызывающий).
     * Возвращает id заархивированных животных.
     */
    @Transaction
    suspend fun applyHerdOperation(operation: DomainHerdOperation): List<Long> {
        val herd = requireHerd(operation.herdId)
        val version = operation.version
        check(version.sign != 0 && !version.isTransfer) { "Версия $version не меняет поголовье группы" }
        check(operation.items.isNotEmpty()) { "Не выбраны животные" }
        check(operation.items.map { it.animalId }.distinct().size == operation.items.size) {
            "Животное выбрано дважды"
        }
        val archived = mutableListOf<Long>()
        operation.items.forEach { item ->
            check(item.count > 0) { "Количество должно быть больше нуля" }
            val member = checkNotNull(getAnimalRow(item.animalId)?.takeIf { it.herdId == herd.id && !it.archive }) {
                "Животное ${item.animalId} не состоит в группе или в архиве"
            }
            check(version.sign < 0 || member.isGroup) { "Индивидуальному животному нельзя добавить поголовье" }
            val suffix = countSuffixOf(member.id)
            val own = getOwnCount(member.id)
            check(version.sign > 0 || item.count <= own + COUNT_EPS) {
                "У «${member.name}» ${own.toCountString()}, нельзя убрать ${item.count.toCountString()}"
            }
            check(item.femalesCount >= 0 && item.malesCount >= 0 && item.femalesCount + item.malesCount <= item.count + COUNT_EPS) {
                "Самок и самцов больше, чем голов в операции"
            }
            check(member.isGroup || item.femalesCount + item.malesCount == 0) { "Пол указывается только у обезличенной группы" }
            check(version.sign > 0 || (item.femalesCount <= member.femalesCount && item.malesCount <= member.malesCount)) {
                "У «${member.name}» нет столько самок или самцов"
            }
            settleLegacyCount(member.id, operation.date, suffix)
            val countId = insertCount(
                AnimalCountTable(
                    count = item.count.toCountString(),
                    suffix = suffix,
                    date = operation.date,
                    note = operation.note,
                    version = version,
                    animalId = member.id,
                    herdId = herd.id,
                    femalesCount = item.femalesCount,
                    malesCount = item.malesCount
                )
            )
            if (item.femalesCount + item.malesCount > 0)
                addSexCounts(member.id, version.sign * item.femalesCount, version.sign * item.malesCount)
            item.sale?.let { insertSale(it.copy(animalId = member.id, animalCountId = countId).toRoomMap()) }
            item.writeOff?.let { insertWriteOff(it.copy(animalCountId = countId).toRoomMap()) }
            item.expenses?.let {
                insertExpenses(it.copy(animalId = member.id, animalCountId = countId).toExpensesRoomMap())
            }
            item.products.forEach {
                insertAdd(it.copy(animalId = member.id, animalCountId = countId).toAddRoomMap())
            }
            if (!member.isGroup && version.sign < 0 && own - item.count <= COUNT_EPS) {
                setArchive(member.id, true)
                archived += member.id
            }
        }
        archived.chunked(IN_CHUNK)
            .forEach { interruptActivePregnancies(it, operation.date, PregnancyInterruptReason.FEMALE_LOST) }
        return archived
    }

    /**
     * Возвращает животное из архива. Если оно числится в сборной группе, а своего поголовья
     * у него нет (own ≤ 0), оно выходит из неё — иначе висело бы в составе «пустым».
     * Возвращает true, если животное стало самостоятельным.
     */
    @Transaction
    suspend fun unarchiveAnimal(id: Long): Boolean {
        setArchive(id, false)
        val herdId = getAnimalRow(id)?.herdId ?: return false
        if (getOwnCount(id) > 0) return false
        if (detachMember(id) != 1) return false
        deleteHerdIfEmpty(herdId)
        return true
    }

    /** Все члены (и архивные) становятся самостоятельными, сборная группа удаляется. */
    @Transaction
    suspend fun disbandHerd(herdId: Long) {
        requireHerd(herdId)
        detachAllMembers(herdId)
        deleteHerd(herdId)
    }

    /**
     * Индивидуальные неархивные члены уходят в архив (herd_id сохраняется — видно, откуда они),
     * их поголовье переводится в обезличенную группу своего вида внутри этой же сборной группы:
     * в существующую неархивную, иначе в новую. Активные беременности прерываются с причиной
     * FEMALE_LOST (напоминания отменяет вызывающий).
     * Возвращает поголовье, перешедшее в счётчики (округлённое до целого).
     */
    @Transaction
    suspend fun depersonalizeHerd(
        herdId: Long,
        date: String,
        groupNote: String,
        memberNote: String
    ): Int {
        val herd = requireHerd(herdId)
        val members = getActiveMemberRows(herd.id)
        val individuals = members.filter { !it.isGroup }
        check(individuals.isNotEmpty()) { "В сборной группе нет индивидуальных животных" }
        individuals.map { it.id }.chunked(IN_CHUNK)
            .forEach { interruptActivePregnancies(it, date, PregnancyInterruptReason.FEMALE_LOST) }

        var movedTotal = 0.0
        individuals.groupBy { it.speciesKey() }.forEach { (key, bucket) ->
            val transferred = bucket.associateWith { getOwnCount(it.id) }.filterValues { it > 0 }
            val memberSuffixes = transferred.keys.associate { it.id to countSuffixOf(it.id) }
            // Переводить нечего (поголовье уже выбыло) — пустую обезличенную группу не заводим
            val target = if (transferred.isEmpty()) null
            else members.firstOrNull { it.isGroup && it.speciesKey() == key } ?: newImpersonalGroup(herd, bucket)
            // У группы без истории счётчика суффикс берём у первого переводимого члена
            val targetSuffix = target?.let { t -> getFirstCountSuffixCode(t.id)?.let { Suffix.fromCode(it) } }
                ?: transferred.keys.firstOrNull()?.let { memberSuffixes.getValue(it.id) }
                ?: Suffix.PIECES
            var moved = 0.0
            // Пол переведённых особей — в счётчики самок и самцов обезличенной группы
            var movedFemales = 0.0
            var movedMales = 0.0
            bucket.forEach { member ->
                val own = transferred[member]
                if (own != null) {
                    val suffix = memberSuffixes.getValue(member.id)
                    settleLegacyCount(member.id, date, suffix)
                    insertCount(
                        AnimalCountTable(
                            count = own.toCountString(),
                            suffix = suffix,
                            date = date,
                            note = memberNote,
                            version = AnimalCountVersion.TRANSFER_OUT,
                            animalId = member.id,
                            herdId = herd.id
                        )
                    )
                    moved += own
                    if (member.sex) movedMales += own else movedFemales += own
                }
                setArchive(member.id, true)
            }
            if (moved > 0 && target != null) {
                settleLegacyCount(target.id, date, targetSuffix)
                addSexCounts(target.id, Math.round(movedFemales).toInt(), Math.round(movedMales).toInt())
                insertCount(
                    AnimalCountTable(
                        count = moved.toCountString(),
                        suffix = targetSuffix,
                        date = date,
                        note = groupNote,
                        version = AnimalCountVersion.TRANSFER_IN,
                        animalId = target.id,
                        herdId = herd.id
                    )
                )
            }
            movedTotal += moved
        }
        return Math.round(movedTotal).toInt()
    }

    /**
     * Выделяет из счётчика обезличенной группы N = names.size именных особей
     * (1 ≤ N ≤ min(own группы, [MAX_SPLIT])). Группе — TRANSFER_OUT N, каждой новой особи —
     * TRANSFER_IN 1 (всё с суффиксом группы). Новые особи попадают в сборную группу исходной;
     * если её нет — создаётся сборная группа с названием исходной (иконку задаёт пользователь), и та входит в неё.
     * Возвращает id новых особей.
     */
    @Transaction
    suspend fun splitFromCounter(
        groupId: Long,
        names: List<String>,
        sex: Boolean,
        date: String,
        groupNote: String,
        memberNote: String
    ): List<Long> {
        val group = checkNotNull(getActiveImpersonalGroup(groupId)) {
            "Группа $groupId не найдена, в архиве или это не обезличенная группа"
        }
        val n = names.size
        val own = getOwnCount(group.id)
        check(n >= 1) { "Не указано, сколько особей выделить" }
        check(n <= MAX_SPLIT) { "Нельзя выделить больше $MAX_SPLIT особей за раз" }
        check(n <= own) { "Нельзя выделить $n: в счётчике группы ${own.toCountString()}" }
        check(names.none { it.isBlank() }) { "У новой особи пустое имя" }
        val herdId = group.herdId ?: insertHerd(
            AnimalHerdTable(
                name = group.name,
                note = "",
                // Иконку и фото группы задаёт пользователь — до этого в списке иконки членов
                idPT = group.idPT
            )
        ).also {
            setHerd(group.id, it)
            recordMembership(group.id, it, AnimalCountVersion.HERD_JOIN, date)
        }
        val suffix = countSuffixOf(group.id)
        settleLegacyCount(group.id, date, suffix)
        // Выделенные особи этого пола уходят из счётчика самок или самцов группы (не ниже 0)
        addSexCounts(group.id, females = if (sex) 0 else -n, males = if (sex) -n else 0)
        insertCount(
            AnimalCountTable(
                count = n.toString(),
                suffix = suffix,
                date = date,
                note = groupNote,
                version = AnimalCountVersion.TRANSFER_OUT,
                animalId = group.id,
                herdId = herdId
            )
        )
        return names.map { name ->
            val id = insertAnimal(
                AnimalTable(
                    name = name.trim(),
                    // «Курицы» → «Петух»/«Курица» по полу; свой тип остаётся как есть
                    type = AnimalSpecies.adaptType(group.type, isGroup = false, sex = sex),
                    species = group.species,
                    breed = group.breed,
                    date = group.date,
                    dateFactory = group.dateFactory,
                    isGroup = false,
                    sex = sex,
                    note = "",
                    imagePath = null,
                    currentIcon = group.currentIcon,
                    archive = false,
                    foodDay = group.foodDay,
                    foodDaySuffix = group.foodDaySuffix,
                    idPT = group.idPT,
                    herdId = herdId
                )
            )
            insertCount(
                AnimalCountTable(
                    count = "1",
                    suffix = suffix,
                    date = date,
                    note = memberNote,
                    version = AnimalCountVersion.TRANSFER_IN,
                    animalId = id,
                    herdId = herdId
                )
            )
            id
        }
    }

    /** Удаляет животное; если это был последний член сборной группы — удаляет и её. */
    @Transaction
    suspend fun deleteAnimal(id: Long) {
        val herdId = getAnimalRow(id)?.herdId
        deleteAnimalRow(id)
        herdId?.let { deleteHerdIfEmpty(it) }
    }

    @Transaction
    suspend fun clearAndInsertForImport(herds: List<AnimalHerdTable>) {
        deleteAllHerds()
        insertAllHerds(herds)
    }

    // endregion
}

/** Размер пачки для IN (:ids): меньше лимита параметров SQLite (999). */
private const val IN_CHUNK = 500

private suspend fun AnimalHerdDao.attachMembersChunked(herdId: Long, idPT: Long, ids: List<Long>): Int =
    ids.chunked(IN_CHUNK).sumOf { attachMembers(herdId, idPT, it) }

private suspend fun AnimalHerdDao.requireHerd(herdId: Long): AnimalHerdTable =
    checkNotNull(getHerd(herdId)) { "Сборная группа $herdId не найдена" }

private suspend fun AnimalHerdDao.deleteHerdIfEmpty(herdId: Long) {
    if (countMembers(herdId) == 0) deleteHerd(herdId)
}

/** Вид для подбора общей обезличенной группы: известный вид, иначе свой тип. */
private fun AnimalTable.speciesKey(): String =
    if (species != AnimalSpecies.UNKNOWN) species.name else type.trim().lowercase()

/**
 * Обезличенная группа вида первой особи [bucket] внутри сборной группы: тип во множественном
 * числе («Корова» → «Коровы»), он же — название. Дата — самая ранняя дата рождения особей.
 */
private suspend fun AnimalHerdDao.newImpersonalGroup(herd: AnimalHerdTable, bucket: List<AnimalTable>): AnimalTable {
    val first = bucket.first()
    val type = AnimalSpecies.adaptType(first.type, isGroup = true, sex = first.sex)
    val date = bucket.mapNotNull { it.date.toLocalDateOrNull() }.minOrNull()?.format(DATE_FORMATTER) ?: first.date
    val group = AnimalTable(
        name = type,
        type = type,
        species = first.species,
        breed = bucket.map { it.breed }.distinct().singleOrNull(),
        date = date,
        dateFactory = null,
        isGroup = true,
        sex = first.sex,
        note = "",
        currentIcon = first.currentIcon,
        archive = false,
        foodDay = first.foodDay,
        foodDaySuffix = first.foodDaySuffix,
        idPT = herd.idPT,
        herdId = herd.id
    )
    return group.copy(id = insertAnimal(group))
}

/** Суффикс счётчика животного — суффикс первой записи, по умолчанию PIECES. */
private suspend fun AnimalHerdDao.countSuffixOf(animalId: Long): Suffix =
    getFirstCountSuffixCode(animalId)?.let { Suffix.fromCode(it) } ?: Suffix.PIECES

/**
 * Legacy-счётчик: если последняя запись (порядок как в own) без версии, own = её count (снимок S),
 * а записи со знаком игнорируются. Добавленная после неё TRANSFER-запись тогда ничего бы не
 * изменила, а сама TRANSFER-запись, оказавшись последней, переключила бы расчёт на SUM, где
 * записи без версии дают 0 — поголовье «потерялось» бы.
 *
 * Поэтому перед TRANSFER вставляем ADD так, чтобы он стал последней записью и SUM-ветка дала
 * ровно S: ADD = S − Σ(записей со знаком). В чисто legacy-истории Σ = 0 и ADD = S (снимок уже
 * учтён как own, ADD лишь переносит его в SUM, не удваивая). Дата ADD — дата операции, но не
 * раньше даты legacy-записи, иначе последней осталась бы legacy-запись.
 *
 * Запись служебная — TRANSFER_IN (тот же знак, что ADD): её нельзя удалить или изменить
 * в карточке животного, иначе SUM-ветка потеряла бы снимок. Если переносить нечего (Σ = S)
 * и следующая запись и так станет последней, она не нужна.
 */
private suspend fun AnimalHerdDao.settleLegacyCount(animalId: Long, date: String, suffix: Suffix) {
    if (isLastCountLegacy(animalId) != true) return
    val snapshot = getOwnCount(animalId)
    val delta = snapshot - getSignedCountSum(animalId)
    val lastDate = getLastCountDate(animalId)
    val opDate = date.toLocalDateOrNull()
    val legacyDate = lastDate?.toLocalDateOrNull()
    val isLegacyLater = opDate != null && legacyDate != null && legacyDate.isAfter(opDate)
    if (delta == 0.0 && !isLegacyLater) return
    insertCount(
        AnimalCountTable(
            count = delta.toCountString(),
            suffix = suffix,
            date = if (isLegacyLater) lastDate else date,
            note = "",
            version = AnimalCountVersion.TRANSFER_IN,
            animalId = animalId
        )
    )
}

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

private fun String.toLocalDateOrNull(): LocalDate? =
    try {
        LocalDate.parse(this, DATE_FORMATTER)
    } catch (_: DateTimeParseException) {
        null
    }

/** Количество для animal_count_table.count: целое — без «.0». */
internal fun Double.toCountString(): String =
    if (this % 1.0 == 0.0) toLong().toString() else toString()

/** Допуск сравнения дробного поголовья. */
private const val COUNT_EPS = 1e-9

private fun today(): String = LocalDate.now().format(DATE_FORMATTER)

/**
 * Вход в сборную группу или выход из неё: HERD_JOIN / HERD_LEAVE со снимком поголовья члена
 * и меткой группы. Счётчик члена не меняется (версия без знака); legacy-счётчик сперва
 * переводится в SUM, иначе запись с версией, став последней, обнулила бы снимок.
 */
private suspend fun AnimalHerdDao.recordMembership(
    memberId: Long,
    herdId: Long,
    version: AnimalCountVersion,
    date: String
) {
    val suffix = countSuffixOf(memberId)
    settleLegacyCount(memberId, date, suffix)
    insertCount(
        AnimalCountTable(
            count = maxOf(getOwnCount(memberId), 0.0).toCountString(),
            suffix = suffix,
            date = date,
            note = "",
            version = version,
            animalId = memberId,
            herdId = herdId
        )
    )
}

