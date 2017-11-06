package com.eliseev.newsagent.util

import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.GregorianCalendar
import java.util.Locale

class DateFormatterTest {

    @Before
    fun setUp() {
        Locale.setDefault(Locale.UK)
    }

    @Test
    fun formatDate() {
        val formatted = DateFormatter.format(GregorianCalendar(2017, 10, 7, 6, 15).time)
        assertEquals("07/11/17 06:15", formatted)
    }

    @Test
    fun formatNullDate() {
        assertEquals("", DateFormatter.format(null))
    }
}