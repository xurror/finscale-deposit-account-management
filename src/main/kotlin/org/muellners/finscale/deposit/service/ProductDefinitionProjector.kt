package org.muellners.finscale.deposit.service

import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.muellners.finscale.deposit.events.CreatedProductDefinitionEvent
import org.muellners.finscale.deposit.events.DeletedProductDefinitionEvent
import org.muellners.finscale.deposit.events.PerformedActionProductDefinitionEvent
import org.muellners.finscale.deposit.events.UpdatedProductDefinitionEvent
import org.muellners.finscale.deposit.queries.GetAllProductDefinitionsQuery
import org.muellners.finscale.deposit.queries.GetProductDefinitionQuery
import org.muellners.finscale.deposit.repository.ProductDefinitionViewRepository
import org.muellners.finscale.deposit.views.ProductDefinitionView
import org.springframework.stereotype.Component

@Component
class ProductDefinitionProjector(val productDefinitionViewRepository: ProductDefinitionViewRepository) {
    @EventHandler
    fun on(event: CreatedProductDefinitionEvent) {
        val productDefinitionView = ProductDefinitionView(
            id = event.id.toString(),
            identifier = event.identifier,
            type = event.type,
            name = event.name,
            description = event.description,
            minimumBalance = event.minimumBalance,
            equityLedgerIdentifier = event.equityLedgerIdentifier,
            cashAccountIdentifier = event.cashAccountIdentifier,
            expenseAccountIdentifier = event.expenseAccountIdentifier,
            accrueAccountIdentifier = event.accrueAccountIdentifier,
            interest = event.interest,
            flexible = event.flexible,
            active = event.active
        )
        productDefinitionViewRepository.save(productDefinitionView)
    }

    @EventHandler
    fun on(event: UpdatedProductDefinitionEvent) {
        val productDefinitionView = productDefinitionViewRepository.findById(event.id!!.toString()).orElse(null)!!
            productDefinitionView.identifier = event.identifier
            productDefinitionView.type = event.type
            productDefinitionView.name = event.name
            productDefinitionView.description = event.description
            productDefinitionView.minimumBalance = event.minimumBalance
            productDefinitionView.equityLedgerIdentifier = event.equityLedgerIdentifier
            productDefinitionView.cashAccountIdentifier = event.cashAccountIdentifier
            productDefinitionView.expenseAccountIdentifier = event.expenseAccountIdentifier
            productDefinitionView.accrueAccountIdentifier = event.accrueAccountIdentifier
            productDefinitionView.interest = event.interest
            productDefinitionView.flexible = event.flexible
            productDefinitionView.active = event.active
        productDefinitionViewRepository.save(productDefinitionView)
    }

    @EventHandler
    fun on(event: PerformedActionProductDefinitionEvent) {
        val productDefinitionView = productDefinitionViewRepository.findById(event.id!!.toString()).orElse(null)!!
        productDefinitionView.active = event.state
        productDefinitionViewRepository.save(productDefinitionView)
    }

    @EventHandler
    fun on(event: DeletedProductDefinitionEvent) {
        val productDefinitionView = productDefinitionViewRepository.findById(event.id!!.toString()).orElse(null)!!
        productDefinitionViewRepository.delete(productDefinitionView)
    }

    @QueryHandler
    fun handle(query: GetAllProductDefinitionsQuery): MutableList<ProductDefinitionView> {
        return productDefinitionViewRepository.findAll()
    }

    @QueryHandler
    fun handle(query: GetProductDefinitionQuery): ProductDefinitionView {
        return productDefinitionViewRepository.findById(query.id!!.toString()).orElse(null)!!
    }
}
