package org.muellners.finscale.deposit.repository

import org.muellners.finscale.deposit.views.ProductInstanceView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ProductInstanceView] entity.
 */
@Suppress("unused")
@Repository
interface ProductInstanceViewRepository : JpaRepository<ProductInstanceView, String>
