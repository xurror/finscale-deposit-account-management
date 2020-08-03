package org.muellners.finscale.deposit.web.rest

import org.muellners.finscale.deposit.DepositAccountManagementApp
import org.muellners.finscale.deposit.config.SecurityBeanOverrideConfiguration
import org.muellners.finscale.deposit.domain.Term
import org.muellners.finscale.deposit.repository.TermRepository
import org.muellners.finscale.deposit.web.rest.errors.ExceptionTranslator

import kotlin.test.assertNotNull

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import javax.persistence.EntityManager

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import org.muellners.finscale.deposit.domain.enumeration.TimeUnit
import org.muellners.finscale.deposit.domain.enumeration.InterestPayable

/**
 * Integration tests for the [TermResource] REST controller.
 *
 * @see TermResource
 */
@SpringBootTest(classes = [SecurityBeanOverrideConfiguration::class, DepositAccountManagementApp::class])
@AutoConfigureMockMvc
@WithMockUser
class TermResourceIT  {

    @Autowired
    private lateinit var termRepository: TermRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator


    @Autowired
    private lateinit var em: EntityManager


    private lateinit var restTermMockMvc: MockMvc

    private lateinit var term: Term

    
    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val termResource = TermResource(termRepository)		
         this.restTermMockMvc = MockMvcBuilders.standaloneSetup(termResource)		
             .setCustomArgumentResolvers(pageableArgumentResolver)		
             .setControllerAdvice(exceptionTranslator)		
             .setConversionService(createFormattingConversionService())		
             .setMessageConverters(jacksonMessageConverter)		
             .setValidator(validator).build()
    }


    @BeforeEach
    fun initTest() {
        term = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createTerm() {
        val databaseSizeBeforeCreate = termRepository.findAll().size

        // Create the Term
        restTermMockMvc.perform(
            post("/api/terms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(term))
        ).andExpect(status().isCreated)

        // Validate the Term in the database
        val termList = termRepository.findAll()
        assertThat(termList).hasSize(databaseSizeBeforeCreate + 1)
        val testTerm = termList[termList.size - 1]
        assertThat(testTerm.period).isEqualTo(DEFAULT_PERIOD)
        assertThat(testTerm.timeUnit).isEqualTo(DEFAULT_TIME_UNIT)
        assertThat(testTerm.interestPayable).isEqualTo(DEFAULT_INTEREST_PAYABLE)
    }

    @Test
    @Transactional
    fun createTermWithExistingId() {
        val databaseSizeBeforeCreate = termRepository.findAll().size

        // Create the Term with an existing ID
        term.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restTermMockMvc.perform(
            post("/api/terms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(term))
        ).andExpect(status().isBadRequest)

        // Validate the Term in the database
        val termList = termRepository.findAll()
        assertThat(termList).hasSize(databaseSizeBeforeCreate)
    }


    @Test
    @Transactional
    fun checkInterestPayableIsRequired() {
        val databaseSizeBeforeTest = termRepository.findAll().size
        // set the field null
        term.interestPayable = null

        // Create the Term, which fails.

        restTermMockMvc.perform(
            post("/api/terms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(term))
        ).andExpect(status().isBadRequest)

        val termList = termRepository.findAll()
        assertThat(termList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllTerms() {
        // Initialize the database
        termRepository.saveAndFlush(term)
        
        // Get all the termList
        restTermMockMvc.perform(get("/api/terms?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(term.id?.toInt())))
            .andExpect(jsonPath("$.[*].period").value(hasItem(DEFAULT_PERIOD)))
            .andExpect(jsonPath("$.[*].timeUnit").value(hasItem(DEFAULT_TIME_UNIT.toString())))
            .andExpect(jsonPath("$.[*].interestPayable").value(hasItem(DEFAULT_INTEREST_PAYABLE.toString())))    }
    
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getTerm() {
        // Initialize the database
        termRepository.saveAndFlush(term)

        val id = term.id
        assertNotNull(id)

        // Get the term
        restTermMockMvc.perform(get("/api/terms/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(term.id?.toInt()))
            .andExpect(jsonPath("$.period").value(DEFAULT_PERIOD))
            .andExpect(jsonPath("$.timeUnit").value(DEFAULT_TIME_UNIT.toString()))
            .andExpect(jsonPath("$.interestPayable").value(DEFAULT_INTEREST_PAYABLE.toString()))    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingTerm() {
        // Get the term
        restTermMockMvc.perform(get("/api/terms/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateTerm() {
        // Initialize the database
        termRepository.saveAndFlush(term)

        val databaseSizeBeforeUpdate = termRepository.findAll().size

        // Update the term
        val id = term.id
        assertNotNull(id)
        val updatedTerm = termRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedTerm are not directly saved in db
        em.detach(updatedTerm)
        updatedTerm.period = UPDATED_PERIOD
        updatedTerm.timeUnit = UPDATED_TIME_UNIT
        updatedTerm.interestPayable = UPDATED_INTEREST_PAYABLE

        restTermMockMvc.perform(
            put("/api/terms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedTerm))
        ).andExpect(status().isOk)

        // Validate the Term in the database
        val termList = termRepository.findAll()
        assertThat(termList).hasSize(databaseSizeBeforeUpdate)
        val testTerm = termList[termList.size - 1]
        assertThat(testTerm.period).isEqualTo(UPDATED_PERIOD)
        assertThat(testTerm.timeUnit).isEqualTo(UPDATED_TIME_UNIT)
        assertThat(testTerm.interestPayable).isEqualTo(UPDATED_INTEREST_PAYABLE)
    }

    @Test
    @Transactional
    fun updateNonExistingTerm() {
        val databaseSizeBeforeUpdate = termRepository.findAll().size


        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTermMockMvc.perform(
            put("/api/terms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(term))
        ).andExpect(status().isBadRequest)

        // Validate the Term in the database
        val termList = termRepository.findAll()
        assertThat(termList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteTerm() {
        // Initialize the database
        termRepository.saveAndFlush(term)

        val databaseSizeBeforeDelete = termRepository.findAll().size

        // Delete the term
        restTermMockMvc.perform(
            delete("/api/terms/{id}", term.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val termList = termRepository.findAll()
        assertThat(termList).hasSize(databaseSizeBeforeDelete - 1)
    }


    companion object {

        private const val DEFAULT_PERIOD: Int = 1
        private const val UPDATED_PERIOD: Int = 2

        private val DEFAULT_TIME_UNIT: TimeUnit = TimeUnit.MONTH
        private val UPDATED_TIME_UNIT: TimeUnit = TimeUnit.YEAR

        private val DEFAULT_INTEREST_PAYABLE: InterestPayable = InterestPayable.MATURITY
        private val UPDATED_INTEREST_PAYABLE: InterestPayable = InterestPayable.ANNUALLY

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Term {
            val term = Term(
                period = DEFAULT_PERIOD,
                timeUnit = DEFAULT_TIME_UNIT,
                interestPayable = DEFAULT_INTEREST_PAYABLE
            )

            return term
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Term {
            val term = Term(
                period = UPDATED_PERIOD,
                timeUnit = UPDATED_TIME_UNIT,
                interestPayable = UPDATED_INTEREST_PAYABLE
            )

            return term
        }
    }
}
