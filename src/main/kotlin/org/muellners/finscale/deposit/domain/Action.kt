package org.muellners.finscale.deposit.domain

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * A Action.
 */
@Entity
@Table(name = "action")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Action(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "identifier")
    var identifier: String? = null,

    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "transaction_type")
    var transactionType: String? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Action) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Action{" +
        "id=$id" +
        ", identifier='$identifier'" +
        ", name='$name'" +
        ", description='$description'" +
        ", transactionType='$transactionType'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }

}
