package org.muellners.finscale.deposit.domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.muellners.finscale.deposit.web.rest.equalsVerifier

class ProductDefinitionTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(ProductDefinition::class)
        val productDefinition1 = ProductDefinition()
        productDefinition1.id = 1L
        val productDefinition2 = ProductDefinition()
        productDefinition2.id = productDefinition1.id
        assertThat(productDefinition1).isEqualTo(productDefinition2)
        productDefinition2.id = 2L
        assertThat(productDefinition1).isNotEqualTo(productDefinition2)
        productDefinition1.id = null
        assertThat(productDefinition1).isNotEqualTo(productDefinition2)
    }
}
