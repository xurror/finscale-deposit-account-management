package org.muellners.finscale.deposit.web.rest

import org.muellners.finscale.deposit.domain.DividendDistribution
import org.muellners.finscale.deposit.repository.DividendDistributionRepository
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

private const val ENTITY_NAME = "depositAccountManagementDividendDistribution"
/**
 * REST controller for managing [org.muellners.finscale.deposit.domain.DividendDistribution].
 */
@RestController
@RequestMapping("/api")
@Transactional
class DividendDistributionResource(
    private val dividendDistributionRepository: DividendDistributionRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /dividend-distributions` : Create a new dividendDistribution.
     *
     * @param dividendDistribution the dividendDistribution to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new dividendDistribution, or with status `400 (Bad Request)` if the dividendDistribution has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/dividend-distributions")
    fun createDividendDistribution(@Valid @RequestBody dividendDistribution: DividendDistribution): ResponseEntity<DividendDistribution> {
        log.debug("REST request to save DividendDistribution : $dividendDistribution")
        if (dividendDistribution.id != null) {
            throw BadRequestAlertException(
                "A new dividendDistribution cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = dividendDistributionRepository.save(dividendDistribution)
        return ResponseEntity.created(URI("/api/dividend-distributions/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /dividend-distributions` : Updates an existing dividendDistribution.
     *
     * @param dividendDistribution the dividendDistribution to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated dividendDistribution,
     * or with status `400 (Bad Request)` if the dividendDistribution is not valid,
     * or with status `500 (Internal Server Error)` if the dividendDistribution couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/dividend-distributions")
    fun updateDividendDistribution(@Valid @RequestBody dividendDistribution: DividendDistribution): ResponseEntity<DividendDistribution> {
        log.debug("REST request to update DividendDistribution : $dividendDistribution")
        if (dividendDistribution.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = dividendDistributionRepository.save(dividendDistribution)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     dividendDistribution.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /dividend-distributions` : get all the dividendDistributions.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of dividendDistributions in body.
     */
    @GetMapping("/dividend-distributions")    
    fun getAllDividendDistributions(): MutableList<DividendDistribution> {
        log.debug("REST request to get all DividendDistributions")
                return dividendDistributionRepository.findAll()
    }

    /**
     * `GET  /dividend-distributions/:id` : get the "id" dividendDistribution.
     *
     * @param id the id of the dividendDistribution to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the dividendDistribution, or with status `404 (Not Found)`.
     */
    @GetMapping("/dividend-distributions/{id}")
    fun getDividendDistribution(@PathVariable id: Long): ResponseEntity<DividendDistribution> {
        log.debug("REST request to get DividendDistribution : $id")
        val dividendDistribution = dividendDistributionRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(dividendDistribution)
    }
    /**
     *  `DELETE  /dividend-distributions/:id` : delete the "id" dividendDistribution.
     *
     * @param id the id of the dividendDistribution to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/dividend-distributions/{id}")
    fun deleteDividendDistribution(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete DividendDistribution : $id")

        dividendDistributionRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
