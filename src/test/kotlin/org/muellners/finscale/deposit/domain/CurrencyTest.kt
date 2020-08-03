package org.muellners.finscale.deposit.domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.muellners.finscale.deposit.web.rest.equalsVerifier

class CurrencyTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Currency::class)
        val currency1 = Currency()
        currency1.id = 1L
        val currency2 = Currency()
        currency2.id = currency1.id
        assertThat(currency1).isEqualTo(currency2)
        currency2.id = 2L
        assertThat(currency1).isNotEqualTo(currency2)
        currency1.id = null
        assertThat(currency1).isNotEqualTo(currency2)
    }
}
