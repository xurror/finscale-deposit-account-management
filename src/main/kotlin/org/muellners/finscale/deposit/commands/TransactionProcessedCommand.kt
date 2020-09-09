package org.muellners.finscale.deposit.commands

import java.util.*
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class TransactionProcessedCommand(
    @TargetAggregateIdentifier
    val id: UUID?
)
