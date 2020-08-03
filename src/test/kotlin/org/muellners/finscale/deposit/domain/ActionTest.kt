package org.muellners.finscale.deposit.domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.muellners.finscale.deposit.web.rest.equalsVerifier

class ActionTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Action::class)
        val action1 = Action()
        action1.id = 1L
        val action2 = Action()
        action2.id = action1.id
        assertThat(action1).isEqualTo(action2)
        action2.id = 2L
        assertThat(action1).isNotEqualTo(action2)
        action1.id = null
        assertThat(action1).isNotEqualTo(action2)
    }
}
