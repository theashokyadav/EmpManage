package com.empmanage.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals

class PhoneNormalizerTest {

    @Test
    fun stripsSpacesDashesParensAndPlus() {
        assertEquals("9876543210", PhoneNormalizer.normalize("+91 (987) 654-3210"))
    }

    @Test
    fun stripsLeadingCountryCode() {
        assertEquals("9876543210", PhoneNormalizer.normalize("919876543210"))
    }

    @Test
    fun stripsLeadingZero() {
        assertEquals("9876543210", PhoneNormalizer.normalize("09876543210"))
    }

    @Test
    fun plainTenDigitNumberIsUnchanged() {
        assertEquals("9876543210", PhoneNormalizer.normalize("9876543210"))
    }

    @Test
    fun emptyInputNormalizesToEmpty() {
        assertEquals("", PhoneNormalizer.normalize(""))
    }
}
