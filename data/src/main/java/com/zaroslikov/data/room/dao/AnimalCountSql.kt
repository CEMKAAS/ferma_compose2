package com.zaroslikov.data.room.dao

/** Коды [com.zaroslikov.domain.models.enums.AnimalCountVersion] с sign = +1. */
const val ANIMAL_COUNT_PLUS = "1, 4, 5, 6"

/** Коды [com.zaroslikov.domain.models.enums.AnimalCountVersion] с sign = -1. */
const val ANIMAL_COUNT_MINUS = "0, 2, 3, 7"

/** Количество записи animal_count_table со знаком версии; старые записи без версии — 0. */
const val ANIMAL_SIGNED_COUNT =
    "CASE WHEN version IN ($ANIMAL_COUNT_PLUS) THEN count WHEN version IN ($ANIMAL_COUNT_MINUS) THEN -count ELSE 0 END"

/** Код [com.zaroslikov.domain.models.enums.Suffix.PIECES] — суффикс счётчика по умолчанию. */
const val SUFFIX_PIECES_CODE = 1

/**
 * Порядок «последняя запись первой» для animal_count_table: по дате (dd.MM.yyyy), затем по id.
 * Тот же порядок, что в [AnimalCountDao.getCountAnimalLimit]. Колонки без алиаса таблицы:
 * в коррелированном подзапросе они относятся к самой внутренней таблице.
 */
const val ANIMAL_COUNT_LAST_FIRST_ORDER =
    "DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"

/**
 * Тело CTE own без области и GROUP BY: собственный счётчик особи/группы (own_count)
 * и суффикс её первой записи (count_suffix).
 *
 * Legacy-ветка выровнена с [AnimalCountDao.getCountAnimalLimit]: если у последней по дате
 * (затем по id) записи версия NULL — берётся её count, иначе сумма записей со знаком версии.
 * Без оконных функций: на API 26 SQLite 3.18.
 */
const val ANIMAL_OWN_SELECT = """
        SELECT c.animal_id AS animal_id,
               COALESCE(
                   (SELECT CASE WHEN l.version IS NULL THEN CAST(l.count AS REAL) END
                      FROM animal_count_table l
                     WHERE l.animal_id = c.animal_id
                     ORDER BY $ANIMAL_COUNT_LAST_FIRST_ORDER
                     LIMIT 1),
                   CAST(SUM($ANIMAL_SIGNED_COUNT) AS REAL),
                   0.0
               ) AS own_count,
               (SELECT f.suffix FROM animal_count_table f
                 WHERE f.animal_id = c.animal_id
                 ORDER BY f.id LIMIT 1) AS count_suffix
          FROM animal_count_table c
"""

// Области CTE own: считаем счётчики только тех животных, которые нужны запросу.
// Параметры: :idPT — проект, :herdId — сборная группа, :id — одно животное, :animalId — одно животное.

/** own по всем животным проекта :idPT. */
const val ANIMAL_OWN_CTE_BY_PROJECT = """
    own AS ($ANIMAL_OWN_SELECT
         WHERE c.animal_id IN (SELECT id FROM animal_table WHERE idPT = :idPT)
         GROUP BY c.animal_id)
"""

/** own по членам сборной группы :herdId. */
const val ANIMAL_OWN_CTE_BY_HERD = """
    own AS ($ANIMAL_OWN_SELECT
         WHERE c.animal_id IN (SELECT id FROM animal_table WHERE herd_id = :herdId)
         GROUP BY c.animal_id)
"""

/** own одного животного :id. */
const val ANIMAL_OWN_CTE_BY_ANIMAL = """
    own AS ($ANIMAL_OWN_SELECT
         WHERE c.animal_id = :id
         GROUP BY c.animal_id)
"""

/** own одного животного :animalId. */
const val ANIMAL_OWN_CTE_SINGLE = """
    own AS ($ANIMAL_OWN_SELECT
         WHERE c.animal_id = :animalId
         GROUP BY c.animal_id)
"""

/** Колонки и JOIN-ы списка животных со счётчиком и сборной группой (идут после WITH own). */
const val ANIMAL_WITH_HERD_COLUMNS = """
    SELECT an.id AS id,
           an.name AS name,
           an.type AS type,
           an.breed AS breed,
           an.date AS date,
           an.date_factory AS dateFactory,
           an.is_group AS is_group,
           an.sex AS sex,
           an.food_day AS food_day,
           an.food_day_suffix AS food_day_suffix,
           an.archive AS archive,
           an.image_path AS image_path,
           an.icon AS icon,
           an.females_count AS females_count,
           an.males_count AS males_count,
           an.herd_id AS herdId,
           h.name AS herdName,
           COALESCE(o.own_count, 0.0) AS ownCount,
           COALESCE(o.count_suffix, $SUFFIX_PIECES_CODE) AS countSuffix
      FROM animal_table an
      LEFT JOIN own o ON o.animal_id = an.id
      LEFT JOIN animal_herd_table h ON h.id = an.herd_id
"""

/** Животные проекта :idPT; дальше дописываются WHERE/ORDER BY. */
const val ANIMAL_WITH_HERD_SELECT_BY_PROJECT = "WITH $ANIMAL_OWN_CTE_BY_PROJECT $ANIMAL_WITH_HERD_COLUMNS"

/** Члены сборной группы :herdId; дальше дописываются WHERE/ORDER BY. */
const val ANIMAL_WITH_HERD_SELECT_BY_HERD = "WITH $ANIMAL_OWN_CTE_BY_HERD $ANIMAL_WITH_HERD_COLUMNS"

/** Одно животное :id; дальше дописывается WHERE an.id = :id. */
const val ANIMAL_WITH_HERD_SELECT_BY_ANIMAL = "WITH $ANIMAL_OWN_CTE_BY_ANIMAL $ANIMAL_WITH_HERD_COLUMNS"
