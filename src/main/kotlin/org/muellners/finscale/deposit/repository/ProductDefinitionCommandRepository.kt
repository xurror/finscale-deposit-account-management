package org.muellners.finscale.deposit.repository

import org.muellners.finscale.deposit.domain.ProductDefinitionCommand
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ProductDefinitionCommand] entity.
 */
@Suppress("unused")
@Repository
interface ProductDefinitionCommandRepository : JpaRepository<ProductDefinitionCommand, Long> {
}
