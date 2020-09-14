package org.muellners.finscale.deposit.service

import org.axonframework.eventhandling.EventHandler
import org.muellners.finscale.deposit.repository.DividendDistributionViewRepository
import org.muellners.finscale.deposit.repository.ProductDefinitionViewRepository
import org.muellners.finscale.deposit.service.mapper.DividendDistributionMapper
import org.muellners.finscale.deposit.service.mapper.ProductDefinitionMapper
import org.springframework.stereotype.Component

@Component
class DividendDistributionProjection(
    val productDefinitionViewRepository: ProductDefinitionViewRepository,
    val dividendDistributionViewRepository: DividendDistributionViewRepository,
    val productDefinitionMapper: ProductDefinitionMapper,
    val dividendDistributionMapper: DividendDistributionMapper
) {
    @EventHandler
    fun on
}
