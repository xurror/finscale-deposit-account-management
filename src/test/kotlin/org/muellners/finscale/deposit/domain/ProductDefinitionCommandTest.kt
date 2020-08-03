package org.muellners.finscale.deposit.domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.muellners.finscale.deposit.web.rest.equalsVerifier

class ProductDefinitionCommandTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(ProductDefinitionCommand::class)
        val productDefinitionCommand1 = ProductDefinitionCommand()
        productDefinitionCommand1.id = 1L
        val productDefinitionCommand2 = ProductDefinitionCommand()
        productDefinitionCommand2.id = productDefinitionCommand1.id
        assertThat(productDefinitionCommand1).isEqualTo(productDefinitionCommand2)
        productDefinitionCommand2.id = 2L
        assertThat(productDefinitionCommand1).isNotEqualTo(productDefinitionCommand2)
        productDefinitionCommand1.id = null
        assertThat(productDefinitionCommand1).isNotEqualTo(productDefinitionCommand2)
    }
}
