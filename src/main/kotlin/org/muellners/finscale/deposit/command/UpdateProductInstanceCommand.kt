package org.muellners.finscale.deposit.command

import java.util.*
import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.muellners.finscale.deposit.domain.productInstance.commands.ProductDefinition

data class UpdateProductInstanceCommand(
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
