package com.empmanage.ui.util

import kotlin.test.Test
import kotlin.test.assertEquals

class OrdinalFormatterTest {

    @Test
    fun formatsFirstSecondThird() {
        assertEquals("1st", OrdinalFormatter.format(1))
        assertEquals("2nd", OrdinalFormatter.format(2))
        assertEquals("3rd", OrdinalFormatter.format(3))
    }

    @Test
    fun formatsFourthThroughTenthAsTh() {
        assertEquals("4th", OrdinalFormatter.format(4))
        assertEquals("10th", OrdinalFormatter.format(10))
    }

    @Test
    fun formatsElevenToThirteenAsThNotStNdRd() {
        assertEquals("11th", OrdinalFormatter.format(11))
        assertEquals("12th", OrdinalFormatter.format(12))
        assertEquals("13th", OrdinalFormatter.format(13))
    }

    @Test
    fun formatsTwentyFirstAsSt() {
        assertEquals("21st", OrdinalFormatter.format(21))
    }
}
