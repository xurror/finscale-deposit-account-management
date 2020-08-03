package org.muellners.finscale.deposit.repository

import org.muellners.finscale.deposit.domain.Term
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Term] entity.
 */
@Suppress("unused")
@Repository
interface TermRepository : JpaRepository<Term, Long> {
}
