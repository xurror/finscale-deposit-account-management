package org.muellners.finscale.deposit.web.rest

import org.muellners.finscale.deposit.domain.Term
import org.muellners.finscale.deposit.repository.TermRepository
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

private const val ENTITY_NAME = "depositAccountManagementTerm"
/**
 * REST controller for managing [org.muellners.finscale.deposit.domain.Term].
 */
@RestController
@RequestMapping("/api")
@Transactional
class TermResource(
    private val termRepository: TermRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /terms` : Create a new term.
     *
     * @param term the term to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new term, or with status `400 (Bad Request)` if the term has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/terms")
    fun createTerm(@Valid @RequestBody term: Term): ResponseEntity<Term> {
        log.debug("REST request to save Term : $term")
        if (term.id != null) {
            throw BadRequestAlertException(
                "A new term cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = termRepository.save(term)
        return ResponseEntity.created(URI("/api/terms/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /terms` : Updates an existing term.
     *
     * @param term the term to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated term,
     * or with status `400 (Bad Request)` if the term is not valid,
     * or with status `500 (Internal Server Error)` if the term couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/terms")
    fun updateTerm(@Valid @RequestBody term: Term): ResponseEntity<Term> {
        log.debug("REST request to update Term : $term")
        if (term.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = termRepository.save(term)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     term.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /terms` : get all the terms.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of terms in body.
     */
    @GetMapping("/terms")    
    fun getAllTerms(): MutableList<Term> {
        log.debug("REST request to get all Terms")
                return termRepository.findAll()
    }

    /**
     * `GET  /terms/:id` : get the "id" term.
     *
     * @param id the id of the term to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the term, or with status `404 (Not Found)`.
     */
    @GetMapping("/terms/{id}")
    fun getTerm(@PathVariable id: Long): ResponseEntity<Term> {
        log.debug("REST request to get Term : $id")
        val term = termRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(term)
    }
    /**
     *  `DELETE  /terms/:id` : delete the "id" term.
     *
     * @param id the id of the term to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/terms/{id}")
    fun deleteTerm(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Term : $id")

        termRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
