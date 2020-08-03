package org.muellners.finscale.deposit.domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.muellners.finscale.deposit.web.rest.equalsVerifier

class AvailableTransactionTypeTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(AvailableTransactionType::class)
        val availableTransactionType1 = AvailableTransactionType()
        availableTransactionType1.id = 1L
        val availableTransactionType2 = AvailableTransactionType()
        availableTransactionType2.id = availableTransactionType1.id
        assertThat(availableTransactionType1).isEqualTo(availableTransactionType2)
        availableTransactionType2.id = 2L
        assertThat(availableTransactionType1).isNotEqualTo(availableTransactionType2)
        availableTransactionType1.id = null
        assertThat(availableTransactionType1).isNotEqualTo(availableTransactionType2)
    }
}
