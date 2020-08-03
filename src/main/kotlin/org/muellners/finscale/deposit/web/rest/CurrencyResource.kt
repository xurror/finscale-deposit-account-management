package org.muellners.finscale.deposit.web.rest

import org.muellners.finscale.deposit.domain.Currency
import org.muellners.finscale.deposit.repository.CurrencyRepository
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

private const val ENTITY_NAME = "depositAccountManagementCurrency"
/**
 * REST controller for managing [org.muellners.finscale.deposit.domain.Currency].
 */
@RestController
@RequestMapping("/api")
@Transactional
class CurrencyResource(
    private val currencyRepository: CurrencyRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /currencies` : Create a new currency.
     *
     * @param currency the currency to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new currency, or with status `400 (Bad Request)` if the currency has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/currencies")
    fun createCurrency(@Valid @RequestBody currency: Currency): ResponseEntity<Currency> {
        log.debug("REST request to save Currency : $currency")
        if (currency.id != null) {
            throw BadRequestAlertException(
                "A new currency cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = currencyRepository.save(currency)
        return ResponseEntity.created(URI("/api/currencies/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /currencies` : Updates an existing currency.
     *
     * @param currency the currency to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated currency,
     * or with status `400 (Bad Request)` if the currency is not valid,
     * or with status `500 (Internal Server Error)` if the currency couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/currencies")
    fun updateCurrency(@Valid @RequestBody currency: Currency): ResponseEntity<Currency> {
        log.debug("REST request to update Currency : $currency")
        if (currency.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = currencyRepository.save(currency)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     currency.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /currencies` : get all the currencies.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of currencies in body.
     */
    @GetMapping("/currencies")    
    fun getAllCurrencies(): MutableList<Currency> {
        log.debug("REST request to get all Currencies")
                return currencyRepository.findAll()
    }

    /**
     * `GET  /currencies/:id` : get the "id" currency.
     *
     * @param id the id of the currency to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the currency, or with status `404 (Not Found)`.
     */
    @GetMapping("/currencies/{id}")
    fun getCurrency(@PathVariable id: Long): ResponseEntity<Currency> {
        log.debug("REST request to get Currency : $id")
        val currency = currencyRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(currency)
    }
    /**
     *  `DELETE  /currencies/:id` : delete the "id" currency.
     *
     * @param id the id of the currency to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/currencies/{id}")
    fun deleteCurrency(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Currency : $id")

        currencyRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
