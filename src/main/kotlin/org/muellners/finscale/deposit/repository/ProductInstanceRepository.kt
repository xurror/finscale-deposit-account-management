package org.muellners.finscale.deposit.repository

import org.muellners.finscale.deposit.domain.ProductInstance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ProductInstance] entity.
 */
@Suppress("unused")
@Repository
interface ProductInstanceRepository : JpaRepository<ProductInstance, Long> {
}
