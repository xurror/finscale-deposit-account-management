package org.muellners.finscale.deposit.events

import java.util.*

data class PerformedActionProductDefinitionEvent(
    val id: UUID?,
    val state: Boolean?
)
