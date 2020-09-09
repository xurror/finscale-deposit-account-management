package org.muellners.finscale.deposit.commands

import java.util.*
import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.muellners.finscale.deposit.domain.productInstance.commands.ProductDefinition

data class CreateProductInstanceCommand(
    @TargetAggregateIdentifier
    val id: UUID?,
    val productDefinition: ProductDefinition?,
    val customerIdentifier: String?,
    val accountIdentifier: String?,
    val beneficiaries: String?,
    val openedOn: Date?,
    val lastTransactionDate: Date?,
    val state: Boolean?
)
