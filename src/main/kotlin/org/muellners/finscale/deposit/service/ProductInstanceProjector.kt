package org.muellners.finscale.deposit.service

import java.util.*
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.muellners.finscale.deposit.events.*
import org.muellners.finscale.deposit.queries.GetAllProductInstancesQuery
import org.muellners.finscale.deposit.queries.GetProductInstanceQuery
import org.muellners.finscale.deposit.repository.ProductInstanceViewRepository
import org.muellners.finscale.deposit.service.mapper.ProductDefinitionMapper
import org.muellners.finscale.deposit.views.ProductInstanceView
import org.springframework.stereotype.Component

@Component
class ProductInstanceProjector(
    val productInstanceViewRepository: ProductInstanceViewRepository,
    val productDefinitionMapper: ProductDefinitionMapper
) {
    @EventHandler
    fun on(event: CreatedProductInstanceEvent) {
        val productInstanceView = ProductInstanceView(
            id = event.id.toString(),
            customerIdentifier = event.customerIdentifier,
            productDefinition = productDefinitionMapper.productDefinitionToProductDefinitionView(event.productDefinition!!),
            accountIdentifier = event.accountIdentifier,
            beneficiaries = event.beneficiaries,
            openedOn = event.openedOn,
            lastTransactionDate = event.lastTransactionDate,
            state = event.state
        )
        productInstanceViewRepository.save(productInstanceView)
    }

    @EventHandler
    fun on(event: UpdatedProductInstanceEvent) {
        val productInstanceView = productInstanceViewRepository.findById(event.id!!.toString()).orElse(null)!!
        productInstanceView.customerIdentifier = event.customerIdentifier
        productInstanceView.productDefinition = productDefinitionMapper.productDefinitionToProductDefinitionView(event.productDefinition!!)
        productInstanceView.accountIdentifier = event.accountIdentifier
        productInstanceView.beneficiaries = event.beneficiaries
        productInstanceView.openedOn = event.openedOn
        productInstanceView.lastTransactionDate = event.lastTransactionDate
        productInstanceView.state = event.state
        productInstanceViewRepository.save(productInstanceView)
    }

    @EventHandler
    fun on(event: PerformedActionProductInstanceEvent) {
        val productInstanceView = productInstanceViewRepository.findById(event.id!!.toString()).orElse(null)
        productInstanceView.state = event.state
        productInstanceViewRepository.save(productInstanceView)
    }

    @EventHandler
    fun on(event: DeletedProductInstanceEvent) {
        val productInstanceView = productInstanceViewRepository.findById(event.id!!.toString()).orElse(null)!!
        productInstanceViewRepository.delete(productInstanceView)
    }

    @EventHandler
    fun on(event: TransactionProcessedEvent) {
        val productInstanceView = productInstanceViewRepository.findById(event.id!!.toString()).orElse(null)!!
        productInstanceView.lastTransactionDate = Calendar.getInstance().time
        productInstanceViewRepository.save(productInstanceView)
    }

    @QueryHandler
    fun handle(query: GetProductInstanceQuery): ProductInstanceView {
        return productInstanceViewRepository.findById(query.id!!.toString()).orElse(null)!!
    }

    @QueryHandler
    fun handle(query: GetAllProductInstancesQuery): MutableList<ProductInstanceView> {
        return productInstanceViewRepository.findAll()
    }
}
