package com.zaroslikov.crm

import com.zaroslikov.crm.domain.model.crmSalesKey
import org.junit.Assert.assertEquals
import org.junit.Test

class CrmSalesKeyTest {

    @Test
    fun salesKey_stripsOnlySpaces_likeSqliteTrim() {
        assertEquals("Иван", crmSalesKey("  Иван  "))
        assertEquals("Иван Петров", crmSalesKey(" Иван Петров "))
        // SQLite TRIM без аргументов не трогает табуляцию и неразрывный пробел
        assertEquals("\tИван", crmSalesKey(" \tИван "))
        assertEquals("Иван ", crmSalesKey("Иван  "))
        assertEquals("", crmSalesKey("   "))
    }
}
