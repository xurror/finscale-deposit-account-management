package org.muellners.finscale.deposit.domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.muellners.finscale.deposit.web.rest.equalsVerifier

class ProductInstanceTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(ProductInstance::class)
        val productInstance1 = ProductInstance()
        productInstance1.id = 1L
        val productInstance2 = ProductInstance()
        productInstance2.id = productInstance1.id
        assertThat(productInstance1).isEqualTo(productInstance2)
        productInstance2.id = 2L
        assertThat(productInstance1).isNotEqualTo(productInstance2)
        productInstance1.id = null
        assertThat(productInstance1).isNotEqualTo(productInstance2)
    }
}
