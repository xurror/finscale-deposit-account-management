package org.muellners.finscale.deposit.repository

import org.muellners.finscale.deposit.domain.ProductDefinition
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ProductDefinition] entity.
 */
@Suppress("unused")
@Repository
interface ProductDefinitionRepository : JpaRepository<ProductDefinition, Long> {
}
