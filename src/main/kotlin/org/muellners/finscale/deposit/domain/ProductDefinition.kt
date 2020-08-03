package org.muellners.finscale.deposit.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.*
import javax.validation.constraints.*

import java.io.Serializable

import org.muellners.finscale.deposit.domain.enumeration.Type

/**
 * A ProductDefinition.
 */
@Entity
@Table(name = "product_definition")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class ProductDefinition(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    var type: Type? = null,

    @Column(name = "identifier")
    var identifier: String? = null,

    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @get: NotNull
    @Column(name = "minimum_balance", nullable = false)
    var minimumBalance: Double? = null,

    @Column(name = "equity_ledger_identifier")
    var equityLedgerIdentifier: String? = null,

    @Column(name = "cash_account_identifier")
    var cashAccountIdentifier: String? = null,

    @Column(name = "expense_account_identifier")
    var expenseAccountIdentifier: String? = null,

    @Column(name = "accrue_account_identifier")
    var accrueAccountIdentifier: String? = null,

    @Column(name = "interest")
    var interest: Double? = null,

    @get: NotNull
    @Column(name = "flexible", nullable = false)
    var flexible: Boolean? = null,

    @Column(name = "active")
    var active: Boolean? = null,

    @OneToMany(mappedBy = "productDefinition")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    var charges: MutableSet<Charge> = mutableSetOf(),

    @ManyToOne(optional = false)    @NotNull
    @JsonIgnoreProperties(value = ["productDefinitions"], allowSetters = true)
    var term: Term? = null,

    @ManyToOne(optional = false)    @NotNull
    @JsonIgnoreProperties(value = ["productDefinitions"], allowSetters = true)
    var currency: Currency? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addCharges(charge: Charge): ProductDefinition {
        this.charges.add(charge)
        charge.productDefinition = this
        return this
    }

    fun removeCharges(charge: Charge): ProductDefinition {
        this.charges.remove(charge)
        charge.productDefinition = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductDefinition) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "ProductDefinition{" +
        "id=$id" +
        ", type='$type'" +
        ", identifier='$identifier'" +
        ", name='$name'" +
        ", description='$description'" +
        ", minimumBalance=$minimumBalance" +
        ", equityLedgerIdentifier='$equityLedgerIdentifier'" +
        ", cashAccountIdentifier='$cashAccountIdentifier'" +
        ", expenseAccountIdentifier='$expenseAccountIdentifier'" +
        ", accrueAccountIdentifier='$accrueAccountIdentifier'" +
        ", interest=$interest" +
        ", flexible='$flexible'" +
        ", active='$active'" +
        "}"


    companion object {
        private const val serialVersionUID = 1L
    }
}
