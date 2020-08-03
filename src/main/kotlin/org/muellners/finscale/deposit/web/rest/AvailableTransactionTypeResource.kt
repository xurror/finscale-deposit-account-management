package org.muellners.finscale.deposit.web.rest

import org.muellners.finscale.deposit.domain.AvailableTransactionType
import org.muellners.finscale.deposit.repository.AvailableTransactionTypeRepository
import org.muellners.finscale.deposit.web.rest.errors.BadRequestAlertException

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

import javax.validation.Valid
import java.net.URI
import java.net.URISyntaxException

private const val ENTITY_NAME = "depositAccountManagementAvailableTransactionType"
/**
 * REST controller for managing [org.muellners.finscale.deposit.domain.AvailableTransactionType].
 */
@RestController
@RequestMapping("/api")
@Transactional
class AvailableTransactionTypeResource(
    private val availableTransactionTypeRepository: AvailableTransactionTypeRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /available-transaction-types` : Create a new availableTransactionType.
     *
     * @param availableTransactionType the availableTransactionType to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new availableTransactionType, or with status `400 (Bad Request)` if the availableTransactionType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/available-transaction-types")
    fun createAvailableTransactionType(@Valid @RequestBody availableTransactionType: AvailableTransactionType): ResponseEntity<AvailableTransactionType> {
        log.debug("REST request to save AvailableTransactionType : $availableTransactionType")
        if (availableTransactionType.id != null) {
            throw BadRequestAlertException(
                "A new availableTransactionType cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = availableTransactionTypeRepository.save(availableTransactionType)
        return ResponseEntity.created(URI("/api/available-transaction-types/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /available-transaction-types` : Updates an existing availableTransactionType.
     *
     * @param availableTransactionType the availableTransactionType to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated availableTransactionType,
     * or with status `400 (Bad Request)` if the availableTransactionType is not valid,
     * or with status `500 (Internal Server Error)` if the availableTransactionType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/available-transaction-types")
    fun updateAvailableTransactionType(@Valid @RequestBody availableTransactionType: AvailableTransactionType): ResponseEntity<AvailableTransactionType> {
        log.debug("REST request to update AvailableTransactionType : $availableTransactionType")
        if (availableTransactionType.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = availableTransactionTypeRepository.save(availableTransactionType)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     availableTransactionType.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /available-transaction-types` : get all the availableTransactionTypes.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of availableTransactionTypes in body.
     */
    @GetMapping("/available-transaction-types")    
    fun getAllAvailableTransactionTypes(): MutableList<AvailableTransactionType> {
        log.debug("REST request to get all AvailableTransactionTypes")
                return availableTransactionTypeRepository.findAll()
    }

    /**
     * `GET  /available-transaction-types/:id` : get the "id" availableTransactionType.
     *
     * @param id the id of the availableTransactionType to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the availableTransactionType, or with status `404 (Not Found)`.
     */
    @GetMapping("/available-transaction-types/{id}")
    fun getAvailableTransactionType(@PathVariable id: Long): ResponseEntity<AvailableTransactionType> {
        log.debug("REST request to get AvailableTransactionType : $id")
        val availableTransactionType = availableTransactionTypeRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(availableTransactionType)
    }
    /**
     *  `DELETE  /available-transaction-types/:id` : delete the "id" availableTransactionType.
     *
     * @param id the id of the availableTransactionType to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/available-transaction-types/{id}")
    fun deleteAvailableTransactionType(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete AvailableTransactionType : $id")

        availableTransactionTypeRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
