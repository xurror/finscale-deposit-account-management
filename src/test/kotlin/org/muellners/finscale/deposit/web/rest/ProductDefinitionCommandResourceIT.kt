package org.muellners.finscale.deposit.web.rest

import org.muellners.finscale.deposit.DepositAccountManagementApp
import org.muellners.finscale.deposit.config.SecurityBeanOverrideConfiguration
import org.muellners.finscale.deposit.domain.ProductDefinitionCommand
import org.muellners.finscale.deposit.repository.ProductDefinitionCommandRepository
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

import org.muellners.finscale.deposit.domain.enumeration.Action

/**
 * Integration tests for the [ProductDefinitionCommandResource] REST controller.
 *
 * @see ProductDefinitionCommandResource
 */
@SpringBootTest(classes = [SecurityBeanOverrideConfiguration::class, DepositAccountManagementApp::class])
@AutoConfigureMockMvc
@WithMockUser
class ProductDefinitionCommandResourceIT  {

    @Autowired
    private lateinit var productDefinitionCommandRepository: ProductDefinitionCommandRepository

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


    private lateinit var restProductDefinitionCommandMockMvc: MockMvc

    private lateinit var productDefinitionCommand: ProductDefinitionCommand

    
    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val productDefinitionCommandResource = ProductDefinitionCommandResource(productDefinitionCommandRepository)		
         this.restProductDefinitionCommandMockMvc = MockMvcBuilders.standaloneSetup(productDefinitionCommandResource)		
             .setCustomArgumentResolvers(pageableArgumentResolver)		
             .setControllerAdvice(exceptionTranslator)		
             .setConversionService(createFormattingConversionService())		
             .setMessageConverters(jacksonMessageConverter)		
             .setValidator(validator).build()
    }


    @BeforeEach
    fun initTest() {
        productDefinitionCommand = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProductDefinitionCommand() {
        val databaseSizeBeforeCreate = productDefinitionCommandRepository.findAll().size

        // Create the ProductDefinitionCommand
        restProductDefinitionCommandMockMvc.perform(
            post("/api/product-definition-commands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productDefinitionCommand))
        ).andExpect(status().isCreated)

        // Validate the ProductDefinitionCommand in the database
        val productDefinitionCommandList = productDefinitionCommandRepository.findAll()
        assertThat(productDefinitionCommandList).hasSize(databaseSizeBeforeCreate + 1)
        val testProductDefinitionCommand = productDefinitionCommandList[productDefinitionCommandList.size - 1]
        assertThat(testProductDefinitionCommand.action).isEqualTo(DEFAULT_ACTION)
        assertThat(testProductDefinitionCommand.note).isEqualTo(DEFAULT_NOTE)
        assertThat(testProductDefinitionCommand.createdOn).isEqualTo(DEFAULT_CREATED_ON)
        assertThat(testProductDefinitionCommand.createdBy).isEqualTo(DEFAULT_CREATED_BY)
    }

    @Test
    @Transactional
    fun createProductDefinitionCommandWithExistingId() {
        val databaseSizeBeforeCreate = productDefinitionCommandRepository.findAll().size

        // Create the ProductDefinitionCommand with an existing ID
        productDefinitionCommand.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductDefinitionCommandMockMvc.perform(
            post("/api/product-definition-commands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productDefinitionCommand))
        ).andExpect(status().isBadRequest)

        // Validate the ProductDefinitionCommand in the database
        val productDefinitionCommandList = productDefinitionCommandRepository.findAll()
        assertThat(productDefinitionCommandList).hasSize(databaseSizeBeforeCreate)
    }


    @Test
    @Transactional
    fun checkActionIsRequired() {
        val databaseSizeBeforeTest = productDefinitionCommandRepository.findAll().size
        // set the field null
        productDefinitionCommand.action = null

        // Create the ProductDefinitionCommand, which fails.

        restProductDefinitionCommandMockMvc.perform(
            post("/api/product-definition-commands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productDefinitionCommand))
        ).andExpect(status().isBadRequest)

        val productDefinitionCommandList = productDefinitionCommandRepository.findAll()
        assertThat(productDefinitionCommandList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllProductDefinitionCommands() {
        // Initialize the database
        productDefinitionCommandRepository.saveAndFlush(productDefinitionCommand)
        
        // Get all the productDefinitionCommandList
        restProductDefinitionCommandMockMvc.perform(get("/api/product-definition-commands?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productDefinitionCommand.id?.toInt())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))    }
    
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getProductDefinitionCommand() {
        // Initialize the database
        productDefinitionCommandRepository.saveAndFlush(productDefinitionCommand)

        val id = productDefinitionCommand.id
        assertNotNull(id)

        // Get the productDefinitionCommand
        restProductDefinitionCommandMockMvc.perform(get("/api/product-definition-commands/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productDefinitionCommand.id?.toInt()))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.createdOn").value(DEFAULT_CREATED_ON))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingProductDefinitionCommand() {
        // Get the productDefinitionCommand
        restProductDefinitionCommandMockMvc.perform(get("/api/product-definition-commands/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateProductDefinitionCommand() {
        // Initialize the database
        productDefinitionCommandRepository.saveAndFlush(productDefinitionCommand)

        val databaseSizeBeforeUpdate = productDefinitionCommandRepository.findAll().size

        // Update the productDefinitionCommand
        val id = productDefinitionCommand.id
        assertNotNull(id)
        val updatedProductDefinitionCommand = productDefinitionCommandRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedProductDefinitionCommand are not directly saved in db
        em.detach(updatedProductDefinitionCommand)
        updatedProductDefinitionCommand.action = UPDATED_ACTION
        updatedProductDefinitionCommand.note = UPDATED_NOTE
        updatedProductDefinitionCommand.createdOn = UPDATED_CREATED_ON
        updatedProductDefinitionCommand.createdBy = UPDATED_CREATED_BY

        restProductDefinitionCommandMockMvc.perform(
            put("/api/product-definition-commands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedProductDefinitionCommand))
        ).andExpect(status().isOk)

        // Validate the ProductDefinitionCommand in the database
        val productDefinitionCommandList = productDefinitionCommandRepository.findAll()
        assertThat(productDefinitionCommandList).hasSize(databaseSizeBeforeUpdate)
        val testProductDefinitionCommand = productDefinitionCommandList[productDefinitionCommandList.size - 1]
        assertThat(testProductDefinitionCommand.action).isEqualTo(UPDATED_ACTION)
        assertThat(testProductDefinitionCommand.note).isEqualTo(UPDATED_NOTE)
        assertThat(testProductDefinitionCommand.createdOn).isEqualTo(UPDATED_CREATED_ON)
        assertThat(testProductDefinitionCommand.createdBy).isEqualTo(UPDATED_CREATED_BY)
    }

    @Test
    @Transactional
    fun updateNonExistingProductDefinitionCommand() {
        val databaseSizeBeforeUpdate = productDefinitionCommandRepository.findAll().size


        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductDefinitionCommandMockMvc.perform(
            put("/api/product-definition-commands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productDefinitionCommand))
        ).andExpect(status().isBadRequest)

        // Validate the ProductDefinitionCommand in the database
        val productDefinitionCommandList = productDefinitionCommandRepository.findAll()
        assertThat(productDefinitionCommandList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteProductDefinitionCommand() {
        // Initialize the database
        productDefinitionCommandRepository.saveAndFlush(productDefinitionCommand)

        val databaseSizeBeforeDelete = productDefinitionCommandRepository.findAll().size

        // Delete the productDefinitionCommand
        restProductDefinitionCommandMockMvc.perform(
            delete("/api/product-definition-commands/{id}", productDefinitionCommand.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val productDefinitionCommandList = productDefinitionCommandRepository.findAll()
        assertThat(productDefinitionCommandList).hasSize(databaseSizeBeforeDelete - 1)
    }


    companion object {

        private val DEFAULT_ACTION: Action = Action.ACTIVATE
        private val UPDATED_ACTION: Action = Action.DEACTIVATE

        private const val DEFAULT_NOTE = "AAAAAAAAAA"
        private const val UPDATED_NOTE = "BBBBBBBBBB"

        private const val DEFAULT_CREATED_ON = "AAAAAAAAAA"
        private const val UPDATED_CREATED_ON = "BBBBBBBBBB"

        private const val DEFAULT_CREATED_BY = "AAAAAAAAAA"
        private const val UPDATED_CREATED_BY = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): ProductDefinitionCommand {
            val productDefinitionCommand = ProductDefinitionCommand(
                action = DEFAULT_ACTION,
                note = DEFAULT_NOTE,
                createdOn = DEFAULT_CREATED_ON,
                createdBy = DEFAULT_CREATED_BY
            )

            return productDefinitionCommand
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ProductDefinitionCommand {
            val productDefinitionCommand = ProductDefinitionCommand(
                action = UPDATED_ACTION,
                note = UPDATED_NOTE,
                createdOn = UPDATED_CREATED_ON,
                createdBy = UPDATED_CREATED_BY
            )

            return productDefinitionCommand
        }
    }
}
