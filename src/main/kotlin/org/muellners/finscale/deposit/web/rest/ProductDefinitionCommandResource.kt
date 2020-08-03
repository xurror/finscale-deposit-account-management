package org.muellners.finscale.deposit.web.rest

import org.muellners.finscale.deposit.domain.ProductDefinitionCommand
import org.muellners.finscale.deposit.repository.ProductDefinitionCommandRepository
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

private const val ENTITY_NAME = "depositAccountManagementProductDefinitionCommand"
/**
 * REST controller for managing [org.muellners.finscale.deposit.domain.ProductDefinitionCommand].
 */
@RestController
@RequestMapping("/api")
@Transactional
class ProductDefinitionCommandResource(
    private val productDefinitionCommandRepository: ProductDefinitionCommandRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /product-definition-commands` : Create a new productDefinitionCommand.
     *
     * @param productDefinitionCommand the productDefinitionCommand to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new productDefinitionCommand, or with status `400 (Bad Request)` if the productDefinitionCommand has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-definition-commands")
    fun createProductDefinitionCommand(@Valid @RequestBody productDefinitionCommand: ProductDefinitionCommand): ResponseEntity<ProductDefinitionCommand> {
        log.debug("REST request to save ProductDefinitionCommand : $productDefinitionCommand")
        if (productDefinitionCommand.id != null) {
            throw BadRequestAlertException(
                "A new productDefinitionCommand cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = productDefinitionCommandRepository.save(productDefinitionCommand)
        return ResponseEntity.created(URI("/api/product-definition-commands/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /product-definition-commands` : Updates an existing productDefinitionCommand.
     *
     * @param productDefinitionCommand the productDefinitionCommand to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated productDefinitionCommand,
     * or with status `400 (Bad Request)` if the productDefinitionCommand is not valid,
     * or with status `500 (Internal Server Error)` if the productDefinitionCommand couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-definition-commands")
    fun updateProductDefinitionCommand(@Valid @RequestBody productDefinitionCommand: ProductDefinitionCommand): ResponseEntity<ProductDefinitionCommand> {
        log.debug("REST request to update ProductDefinitionCommand : $productDefinitionCommand")
        if (productDefinitionCommand.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = productDefinitionCommandRepository.save(productDefinitionCommand)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     productDefinitionCommand.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /product-definition-commands` : get all the productDefinitionCommands.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of productDefinitionCommands in body.
     */
    @GetMapping("/product-definition-commands")    
    fun getAllProductDefinitionCommands(): MutableList<ProductDefinitionCommand> {
        log.debug("REST request to get all ProductDefinitionCommands")
                return productDefinitionCommandRepository.findAll()
    }

    /**
     * `GET  /product-definition-commands/:id` : get the "id" productDefinitionCommand.
     *
     * @param id the id of the productDefinitionCommand to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the productDefinitionCommand, or with status `404 (Not Found)`.
     */
    @GetMapping("/product-definition-commands/{id}")
    fun getProductDefinitionCommand(@PathVariable id: Long): ResponseEntity<ProductDefinitionCommand> {
        log.debug("REST request to get ProductDefinitionCommand : $id")
        val productDefinitionCommand = productDefinitionCommandRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(productDefinitionCommand)
    }
    /**
     *  `DELETE  /product-definition-commands/:id` : delete the "id" productDefinitionCommand.
     *
     * @param id the id of the productDefinitionCommand to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/product-definition-commands/{id}")
    fun deleteProductDefinitionCommand(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete ProductDefinitionCommand : $id")

        productDefinitionCommandRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
