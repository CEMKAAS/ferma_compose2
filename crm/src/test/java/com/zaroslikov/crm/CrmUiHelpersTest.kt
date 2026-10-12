package com.zaroslikov.crm

import com.zaroslikov.crm.domain.model.CrmContactType
import com.zaroslikov.crm.ui.components.crmAvatarColorIndex
import com.zaroslikov.crm.ui.components.crmInitials
import com.zaroslikov.crm.ui.components.crmNiceStep
import com.zaroslikov.crm.util.crmContactUri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CrmUiHelpersTest {

    @Test
    fun niceStep_coversMaxWithRoundSteps() {
        assertEquals(1f, crmNiceStep(0f, 4), 0.0001f)
        assertEquals(25f, crmNiceStep(100f, 4), 0.0001f)
        assertEquals(2.5f, crmNiceStep(10f, 4), 0.0001f)
        assertEquals(500f, crmNiceStep(1234f, 4), 0.0001f)
        assertTrue(crmNiceStep(98_765f, 4) * 4 >= 98_765f)
    }

    @Test
    fun initials() {
        assertEquals("ИП", crmInitials("иван петров"))
        assertEquals("Ф", crmInitials("  ферма  "))
        assertEquals("АБ", crmInitials("Анна Бокова Сергеевна"))
        assertEquals("?", crmInitials("   "))
    }

    @Test
    fun avatarColor_isStableAndInRange() {
        val first = crmAvatarColorIndex("Иван Петров", 8)
        assertEquals(first, crmAvatarColorIndex(" иван петров ", 8))
        repeat(50) { assertTrue(crmAvatarColorIndex("client $it", 8) in 0 until 8) }
    }

    @Test
    fun contactUri() {
        assertEquals("tel:+79001234567", crmContactUri(CrmContactType.PHONE, "+7 (900) 123-45-67"))
        assertEquals("mailto:a@b.ru", crmContactUri(CrmContactType.EMAIL, " a@b.ru "))
        assertEquals("geo:0,0?q=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0", crmContactUri(CrmContactType.ADDRESS, "Москва"))
        assertEquals("https://t.me/farmer", crmContactUri(CrmContactType.TELEGRAM, "@farmer"))
        assertEquals("https://t.me/+79001234567", crmContactUri(CrmContactType.TELEGRAM, "+7 900 123-45-67"))
        assertEquals("https://wa.me/79001234567", crmContactUri(CrmContactType.WHATSAPP, "+7 900 123 45 67"))
        assertEquals("https://vk.com/id1", crmContactUri(CrmContactType.VK, "id1"))
        assertEquals("https://vk.com/id1", crmContactUri(CrmContactType.VK, "https://vk.com/id1"))
        assertNull(crmContactUri(CrmContactType.OTHER, "что угодно"))
        assertEquals("geo:0,0?q=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C%20%D1%83%D0%BB.%201", crmContactUri(CrmContactType.ADDRESS, "Москва, ул. 1"))
        assertEquals("mailto:a%2Bb@c.ru", crmContactUri(CrmContactType.EMAIL, "a+b@c.ru"))
        assertEquals("https://t.me/a%2Fb%3Fx%3D1", crmContactUri(CrmContactType.TELEGRAM, "a/b?x=1"))
        assertNull(crmContactUri(CrmContactType.PHONE, "  "))
    }

    @Test
    fun contactUri_linksOnlyToAllowedHostsOverHttps() {
        assertEquals("https://t.me/farmer", crmContactUri(CrmContactType.TELEGRAM, "http://t.me/farmer"))
        assertEquals("https://t.me/farmer", crmContactUri(CrmContactType.TELEGRAM, "HTTPS://www.T.me/farmer"))
        assertEquals("https://telegram.me/farmer", crmContactUri(CrmContactType.TELEGRAM, "https://telegram.me/farmer"))
        assertEquals("https://wa.me/79001234567?text=hi", crmContactUri(CrmContactType.WHATSAPP, "https://wa.me/79001234567?text=hi"))
        assertEquals("https://api.whatsapp.com/send", crmContactUri(CrmContactType.WHATSAPP, "https://api.whatsapp.com/send"))
        assertEquals("https://m.vk.com/id1", crmContactUri(CrmContactType.VK, "http://m.vk.com/id1"))
        assertEquals("https://vk.ru/id1", crmContactUri(CrmContactType.VK, "https://vk.ru/id1"))

        assertNull(crmContactUri(CrmContactType.TELEGRAM, "https://evil.example/x"))
        assertNull(crmContactUri(CrmContactType.TELEGRAM, "https://t.me.evil.example/x"))
        assertNull(crmContactUri(CrmContactType.TELEGRAM, "https://t.me@evil.example/x"))
        assertNull(crmContactUri(CrmContactType.TELEGRAM, "https://vk.com/id1"))
        assertNull(crmContactUri(CrmContactType.WHATSAPP, "http://evil.example/wa.me"))
        assertNull(crmContactUri(CrmContactType.VK, "https://vk.com.evil.example/id1"))
        assertNull(crmContactUri(CrmContactType.VK, "https://exa mple.com"))
    }
}
