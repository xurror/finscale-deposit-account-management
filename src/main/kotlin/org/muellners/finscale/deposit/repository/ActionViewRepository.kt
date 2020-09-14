package org.muellners.finscale.deposit.repository

import org.muellners.finscale.deposit.views.Action
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Action] entity.
 */
@Suppress("unused")
@Repository
interface ActionRepository : JpaRepository<Action, Long>
