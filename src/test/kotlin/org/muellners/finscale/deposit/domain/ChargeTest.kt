package org.muellners.finscale.deposit.domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.muellners.finscale.deposit.web.rest.equalsVerifier

class ChargeTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Charge::class)
        val charge1 = Charge()
        charge1.id = 1L
        val charge2 = Charge()
        charge2.id = charge1.id
        assertThat(charge1).isEqualTo(charge2)
        charge2.id = 2L
        assertThat(charge1).isNotEqualTo(charge2)
        charge1.id = null
        assertThat(charge1).isNotEqualTo(charge2)
    }
}
