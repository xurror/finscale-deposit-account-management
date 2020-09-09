package org.muellners.finscale.deposit.views

import java.util.*
import javax.persistence.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.muellners.finscale.deposit.domain.AbstractAuditingEntity

/**
 * A ProductInstance.
 */
@Entity
@Table(name = "product_instance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class ProductInstanceView(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: String? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = [CascadeType.ALL])
    @JoinColumn(name = "product_definition_id", nullable = false)
    var productDefinition: ProductDefinitionView? = null,

    @Column(name = "customer_identifier", nullable = false)
    var customerIdentifier: String? = null,

    @Column(name = "product_identifier", nullable = false)
    var productIdentifier: String? = null,

    @Column(name = "account_identifier", nullable = false)
    var accountIdentifier: String? = null,

    @Column(name = "alternative_account_number", nullable = false)
    var alternativeAccountNumber: String? = null,

    @Column(name = "beneficiaries", nullable = false)
    var beneficiaries: String? = null,

    @Column(name = "opened_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var openedOn: Date? = null,

    @Column(name = "last_transaction_date", nullable = false)
    var lastTransactionDate: Date? = null,

    @Column(name = "state", nullable = false)
    var state: Boolean? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : AbstractAuditingEntity() {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductInstanceView) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "ProductInstance{" +
        "id=$id" +
        ", productDefinition='$productDefinition'" +
        ", customerIdentifier='$customerIdentifier'" +
        ", productIdentifier='$productIdentifier'" +
        ", accountIdentifier='$accountIdentifier'" +
        ", alternativeAccountNumber='$alternativeAccountNumber'" +
        ", beneficiaries='$beneficiaries'" +
        ", lastTransactionDate='$lastTransactionDate'" +
        ", state='$state'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
