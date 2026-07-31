package com.zaroslikov.domain.models.enums

enum class TemplateType(val code: Int) {
    ADD(0),
    SALE(1),
    WRITE_OFF(2),
    EXPENSES(3);

    companion object {
        fun fromCode(code: Int): TemplateType = TemplateType.entries.first { it.code == code }
    }
}
