package org.muellners.finscale.deposit.domain

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.*

import java.io.Serializable

/**
 * A ProductInstance.
 */
@Entity
@Table(name = "product_instance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class ProductInstance(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @Column(name = "customer_identifier")
    var customerIdentifier: String? = null,

    @Column(name = "product_identifier")
    var productIdentifier: String? = null,

    @Column(name = "account_identifier")
    var accountIdentifier: String? = null,

    @Column(name = "alternative_account_number")
    var alternativeAccountNumber: String? = null,

    @Column(name = "beneficiaries")
    var beneficiaries: String? = null,

    @Column(name = "opened_on")
    var openedOn: String? = null,

    @Column(name = "last_transaction_date")
    var lastTransactionDate: String? = null,

    @Column(name = "state")
    var state: String? = null,

    @Column(name = "balance")
    var balance: Double? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductInstance) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "ProductInstance{" +
        "id=$id" +
        ", customerIdentifier='$customerIdentifier'" +
        ", productIdentifier='$productIdentifier'" +
        ", accountIdentifier='$accountIdentifier'" +
        ", alternativeAccountNumber='$alternativeAccountNumber'" +
        ", beneficiaries='$beneficiaries'" +
        ", openedOn='$openedOn'" +
        ", lastTransactionDate='$lastTransactionDate'" +
        ", state='$state'" +
        ", balance=$balance" +
        "}"


    companion object {
        private const val serialVersionUID = 1L
    }
}
