package com.zaroslikov.domain.models.enums

/**
 * Версия записи счётчика животного. [sign] — как запись меняет его поголовье;
 * HERD_JOIN / HERD_LEAVE (sign 0) — снимок поголовья при входе в сборную группу и выходе из неё:
 * счётчик животного не меняют, но меняют поголовье группы и попадают в её историю.
 */
enum class AnimalCountVersion(val code: Int, val sign: Int, val isTransfer: Boolean = false) {
    SALE(0, -1),
    EXPENSES(1, +1),
    KILL(2, -1),
    WRITE_OFF(3, -1),
    ADD(4, +1),
    INCUBATOR(5, +1),
    TRANSFER_IN(6, +1, isTransfer = true),
    TRANSFER_OUT(7, -1, isTransfer = true),
    HERD_JOIN(8, 0, isTransfer = true),
    HERD_LEAVE(9, 0, isTransfer = true);

    companion object {
        fun fromCode(code: Int): AnimalCountVersion =
            AnimalCountVersion.entries.first { it.code == code }
    }
}
