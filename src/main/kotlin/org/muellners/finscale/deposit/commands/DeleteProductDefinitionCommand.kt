package org.muellners.finscale.deposit.commands

import java.util.*
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class DeleteProductDefinitionCommand(
    @TargetAggregateIdentifier
    val id: UUID?
)
