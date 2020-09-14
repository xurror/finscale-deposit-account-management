package org.muellners.finscale.deposit.views

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A DividendDistribution.
 */
@Entity
@Table(name = "dividend_distribution")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class DividendDistributionView(
    @Id
    var id: String? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = [CascadeType.ALL])
    @JoinColumn(name = "product_definition_id", nullable = false)
    var productDefinition: ProductDefinitionView,

    @get: NotNull
    @Column(name = "due_date", nullable = false)
    var dueDate: LocalDate? = null,

    @get: NotNull
    @Column(name = "rate", nullable = false)
    var rate: Double? = null,

    @Column(name = "created_by", nullable = false, length = 32)
    var createdBy: String? = null,

    @Column(name = "created_on", nullable = false)
    var createdOn: LocalDateTime? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DividendDistributionView) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "DividendDistribution{" +
        "id=$id" +
        ", productDefinition='$productDefinition'" +
        ", dueDate='$dueDate'" +
        ", dividendRate='$rate'" +
        ", createdBy='$createdBy'" +
        ", createdOn='$createdOn'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
