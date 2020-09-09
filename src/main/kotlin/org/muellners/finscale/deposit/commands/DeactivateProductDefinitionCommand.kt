package org.muellners.finscale.deposit.commands

import java.util.*
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class DeactivateProductDefinitionCommand(
    @TargetAggregateIdentifier
    val id: UUID?,
    val state: Boolean?
)
