package org.muellners.finscale.deposit.domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.muellners.finscale.deposit.web.rest.equalsVerifier

class TermTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Term::class)
        val term1 = Term()
        term1.id = 1L
        val term2 = Term()
        term2.id = term1.id
        assertThat(term1).isEqualTo(term2)
        term2.id = 2L
        assertThat(term1).isNotEqualTo(term2)
        term1.id = null
        assertThat(term1).isNotEqualTo(term2)
    }
}
