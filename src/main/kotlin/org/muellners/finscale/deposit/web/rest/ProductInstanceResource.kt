package org.muellners.finscale.deposit.web.rest

import org.muellners.finscale.deposit.domain.ProductInstance
import org.muellners.finscale.deposit.repository.ProductInstanceRepository
import org.muellners.finscale.deposit.web.rest.errors.BadRequestAlertException

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

import java.net.URI
import java.net.URISyntaxException

private const val ENTITY_NAME = "depositAccountManagementProductInstance"
/**
 * REST controller for managing [org.muellners.finscale.deposit.domain.ProductInstance].
 */
@RestController
@RequestMapping("/api")
@Transactional
class ProductInstanceResource(
    private val productInstanceRepository: ProductInstanceRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /product-instances` : Create a new productInstance.
     *
     * @param productInstance the productInstance to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new productInstance, or with status `400 (Bad Request)` if the productInstance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-instances")
    fun createProductInstance(@RequestBody productInstance: ProductInstance): ResponseEntity<ProductInstance> {
        log.debug("REST request to save ProductInstance : $productInstance")
        if (productInstance.id != null) {
            throw BadRequestAlertException(
                "A new productInstance cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = productInstanceRepository.save(productInstance)
        return ResponseEntity.created(URI("/api/product-instances/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /product-instances` : Updates an existing productInstance.
     *
     * @param productInstance the productInstance to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated productInstance,
     * or with status `400 (Bad Request)` if the productInstance is not valid,
     * or with status `500 (Internal Server Error)` if the productInstance couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-instances")
    fun updateProductInstance(@RequestBody productInstance: ProductInstance): ResponseEntity<ProductInstance> {
        log.debug("REST request to update ProductInstance : $productInstance")
        if (productInstance.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = productInstanceRepository.save(productInstance)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     productInstance.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /product-instances` : get all the productInstances.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of productInstances in body.
     */
    @GetMapping("/product-instances")    
    fun getAllProductInstances(): MutableList<ProductInstance> {
        log.debug("REST request to get all ProductInstances")
                return productInstanceRepository.findAll()
    }

    /**
     * `GET  /product-instances/:id` : get the "id" productInstance.
     *
     * @param id the id of the productInstance to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the productInstance, or with status `404 (Not Found)`.
     */
    @GetMapping("/product-instances/{id}")
    fun getProductInstance(@PathVariable id: Long): ResponseEntity<ProductInstance> {
        log.debug("REST request to get ProductInstance : $id")
        val productInstance = productInstanceRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(productInstance)
    }
    /**
     *  `DELETE  /product-instances/:id` : delete the "id" productInstance.
     *
     * @param id the id of the productInstance to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/product-instances/{id}")
    fun deleteProductInstance(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete ProductInstance : $id")

        productInstanceRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
