package org.muellners.finscale.deposit.domain

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.*
import javax.validation.constraints.*

import java.io.Serializable

import org.muellners.finscale.deposit.domain.enumeration.TimeUnit

import org.muellners.finscale.deposit.domain.enumeration.InterestPayable

/**
 * A Term.
 */
@Entity
@Table(name = "term")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Term(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "period")
    var period: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "time_unit")
    var timeUnit: TimeUnit? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "interest_payable", nullable = false)
    var interestPayable: InterestPayable? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Term) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Term{" +
        "id=$id" +
        ", period=$period" +
        ", timeUnit='$timeUnit'" +
        ", interestPayable='$interestPayable'" +
        "}"


    companion object {
        private const val serialVersionUID = 1L
    }
}
