package org.muellners.finscale.deposit.repository

import org.muellners.finscale.deposit.domain.Action
import org.muellners.finscale.deposit.views.ProductDefinitionView
import org.muellners.finscale.deposit.views.TermView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Accruedinterest] entity.
 */
@Suppress("unused")
@Repository
interface AccruedInterestViewRepository : JpaRepository<Action, Long> {
    fun findByCustomerAccountIdentifier(customerAccountIdentifier: String): TermView
}
