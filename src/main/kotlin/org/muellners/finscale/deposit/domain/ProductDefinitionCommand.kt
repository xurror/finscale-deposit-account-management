package org.muellners.finscale.deposit.domain

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.*
import javax.validation.constraints.*

import java.io.Serializable

import org.muellners.finscale.deposit.domain.enumeration.Action

/**
 * A ProductDefinitionCommand.
 */
@Entity
@Table(name = "product_definition_command")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class ProductDefinitionCommand(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    var action: Action? = null,

    @Column(name = "note")
    var note: String? = null,

    @Column(name = "created_on")
    var createdOn: String? = null,

    @Column(name = "created_by")
    var createdBy: String? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductDefinitionCommand) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "ProductDefinitionCommand{" +
        "id=$id" +
        ", action='$action'" +
        ", note='$note'" +
        ", createdOn='$createdOn'" +
        ", createdBy='$createdBy'" +
        "}"


    companion object {
        private const val serialVersionUID = 1L
    }
}
