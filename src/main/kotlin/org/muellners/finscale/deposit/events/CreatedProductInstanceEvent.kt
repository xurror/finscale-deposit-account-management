package org.muellners.finscale.deposit.events

import java.util.*
import org.muellners.finscale.deposit.domain.productInstance.commands.ProductDefinition

data class CreatedProductInstanceEvent(
    val id: UUID?,
    val productDefinition: ProductDefinition?,
    val customerIdentifier: String?,
    val accountIdentifier: String?,
    val beneficiaries: String?,
    val openedOn: Date?,
    val lastTransactionDate: Date?,
    val state: Boolean?
)
