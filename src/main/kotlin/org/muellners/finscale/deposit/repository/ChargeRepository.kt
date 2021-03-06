package org.muellners.finscale.deposit.repository

import org.muellners.finscale.deposit.domain.Charge
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Charge] entity.
 */
@Suppress("unused")
@Repository
interface ChargeRepository : JpaRepository<Charge, Long>
