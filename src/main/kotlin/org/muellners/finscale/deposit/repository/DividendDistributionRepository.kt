package org.muellners.finscale.deposit.repository

import org.muellners.finscale.deposit.domain.DividendDistribution
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [DividendDistribution] entity.
 */
@Suppress("unused")
@Repository
interface DividendDistributionRepository : JpaRepository<DividendDistribution, Long> {
}
