package org.muellners.finscale.deposit.events

import java.util.*

data class PerformedActionProductInstanceEvent(
    val id: UUID?,
    val state: Boolean?
)
