package org.muellners.finscale.deposit.web.rest

import org.muellners.finscale.deposit.domain.Action
import org.muellners.finscale.deposit.repository.ActionRepository
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

private const val ENTITY_NAME = "depositAccountManagementAction"
/**
 * REST controller for managing [org.muellners.finscale.deposit.domain.Action].
 */
@RestController
@RequestMapping("/api")
@Transactional
class ActionResource(
    private val actionRepository: ActionRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /actions` : Create a new action.
     *
     * @param action the action to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new action, or with status `400 (Bad Request)` if the action has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/actions")
    fun createAction(@Valid @RequestBody action: Action): ResponseEntity<Action> {
        log.debug("REST request to save Action : $action")
        if (action.id != null) {
            throw BadRequestAlertException(
                "A new action cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = actionRepository.save(action)
        return ResponseEntity.created(URI("/api/actions/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /actions` : Updates an existing action.
     *
     * @param action the action to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated action,
     * or with status `400 (Bad Request)` if the action is not valid,
     * or with status `500 (Internal Server Error)` if the action couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/actions")
    fun updateAction(@Valid @RequestBody action: Action): ResponseEntity<Action> {
        log.debug("REST request to update Action : $action")
        if (action.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = actionRepository.save(action)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     action.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /actions` : get all the actions.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of actions in body.
     */
    @GetMapping("/actions")    
    fun getAllActions(): MutableList<Action> {
        log.debug("REST request to get all Actions")
                return actionRepository.findAll()
    }

    /**
     * `GET  /actions/:id` : get the "id" action.
     *
     * @param id the id of the action to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the action, or with status `404 (Not Found)`.
     */
    @GetMapping("/actions/{id}")
    fun getAction(@PathVariable id: Long): ResponseEntity<Action> {
        log.debug("REST request to get Action : $id")
        val action = actionRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(action)
    }
    /**
     *  `DELETE  /actions/:id` : delete the "id" action.
     *
     * @param id the id of the action to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/actions/{id}")
    fun deleteAction(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Action : $id")

        actionRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
