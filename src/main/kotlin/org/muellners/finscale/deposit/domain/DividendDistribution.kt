package org.muellners.finscale.deposit.domain

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.*
import javax.validation.constraints.*

import java.io.Serializable
import java.time.LocalDate

/**
 * A DividendDistribution.
 */
@Entity
@Table(name = "dividend_distribution")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class DividendDistribution(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @get: NotNull
    @Column(name = "due_date", nullable = false)
    var dueDate: LocalDate? = null,

    @get: NotNull
    @Column(name = "dividend_rate", nullable = false)
    var dividendRate: String? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DividendDistribution) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "DividendDistribution{" +
        "id=$id" +
        ", dueDate='$dueDate'" +
        ", dividendRate='$dividendRate'" +
        "}"


    companion object {
        private const val serialVersionUID = 1L
    }
}
