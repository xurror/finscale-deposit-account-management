package org.muellners.finscale.deposit.web.rest

import org.muellners.finscale.deposit.domain.ProductDefinition
import org.muellners.finscale.deposit.repository.ProductDefinitionRepository
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

private const val ENTITY_NAME = "depositAccountManagementProductDefinition"
/**
 * REST controller for managing [org.muellners.finscale.deposit.domain.ProductDefinition].
 */
@RestController
@RequestMapping("/api")
@Transactional
class ProductDefinitionResource(
    private val productDefinitionRepository: ProductDefinitionRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /product-definitions` : Create a new productDefinition.
     *
     * @param productDefinition the productDefinition to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new productDefinition, or with status `400 (Bad Request)` if the productDefinition has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-definitions")
    fun createProductDefinition(@Valid @RequestBody productDefinition: ProductDefinition): ResponseEntity<ProductDefinition> {
        log.debug("REST request to save ProductDefinition : $productDefinition")
        if (productDefinition.id != null) {
            throw BadRequestAlertException(
                "A new productDefinition cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = productDefinitionRepository.save(productDefinition)
        return ResponseEntity.created(URI("/api/product-definitions/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /product-definitions` : Updates an existing productDefinition.
     *
     * @param productDefinition the productDefinition to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated productDefinition,
     * or with status `400 (Bad Request)` if the productDefinition is not valid,
     * or with status `500 (Internal Server Error)` if the productDefinition couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-definitions")
    fun updateProductDefinition(@Valid @RequestBody productDefinition: ProductDefinition): ResponseEntity<ProductDefinition> {
        log.debug("REST request to update ProductDefinition : $productDefinition")
        if (productDefinition.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = productDefinitionRepository.save(productDefinition)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     productDefinition.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /product-definitions` : get all the productDefinitions.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of productDefinitions in body.
     */
    @GetMapping("/product-definitions")    
    fun getAllProductDefinitions(): MutableList<ProductDefinition> {
        log.debug("REST request to get all ProductDefinitions")
                return productDefinitionRepository.findAll()
    }

    /**
     * `GET  /product-definitions/:id` : get the "id" productDefinition.
     *
     * @param id the id of the productDefinition to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the productDefinition, or with status `404 (Not Found)`.
     */
    @GetMapping("/product-definitions/{id}")
    fun getProductDefinition(@PathVariable id: Long): ResponseEntity<ProductDefinition> {
        log.debug("REST request to get ProductDefinition : $id")
        val productDefinition = productDefinitionRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(productDefinition)
    }
    /**
     *  `DELETE  /product-definitions/:id` : delete the "id" productDefinition.
     *
     * @param id the id of the productDefinition to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/product-definitions/{id}")
    fun deleteProductDefinition(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete ProductDefinition : $id")

        productDefinitionRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
