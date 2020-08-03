package org.muellners.finscale.deposit.web.rest

import org.muellners.finscale.deposit.domain.Charge
import org.muellners.finscale.deposit.repository.ChargeRepository
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

private const val ENTITY_NAME = "depositAccountManagementCharge"
/**
 * REST controller for managing [org.muellners.finscale.deposit.domain.Charge].
 */
@RestController
@RequestMapping("/api")
@Transactional
class ChargeResource(
    private val chargeRepository: ChargeRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /charges` : Create a new charge.
     *
     * @param charge the charge to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new charge, or with status `400 (Bad Request)` if the charge has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/charges")
    fun createCharge(@Valid @RequestBody charge: Charge): ResponseEntity<Charge> {
        log.debug("REST request to save Charge : $charge")
        if (charge.id != null) {
            throw BadRequestAlertException(
                "A new charge cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = chargeRepository.save(charge)
        return ResponseEntity.created(URI("/api/charges/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /charges` : Updates an existing charge.
     *
     * @param charge the charge to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated charge,
     * or with status `400 (Bad Request)` if the charge is not valid,
     * or with status `500 (Internal Server Error)` if the charge couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/charges")
    fun updateCharge(@Valid @RequestBody charge: Charge): ResponseEntity<Charge> {
        log.debug("REST request to update Charge : $charge")
        if (charge.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = chargeRepository.save(charge)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     charge.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /charges` : get all the charges.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of charges in body.
     */
    @GetMapping("/charges")    
    fun getAllCharges(): MutableList<Charge> {
        log.debug("REST request to get all Charges")
                return chargeRepository.findAll()
    }

    /**
     * `GET  /charges/:id` : get the "id" charge.
     *
     * @param id the id of the charge to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the charge, or with status `404 (Not Found)`.
     */
    @GetMapping("/charges/{id}")
    fun getCharge(@PathVariable id: Long): ResponseEntity<Charge> {
        log.debug("REST request to get Charge : $id")
        val charge = chargeRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(charge)
    }
    /**
     *  `DELETE  /charges/:id` : delete the "id" charge.
     *
     * @param id the id of the charge to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/charges/{id}")
    fun deleteCharge(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Charge : $id")

        chargeRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
