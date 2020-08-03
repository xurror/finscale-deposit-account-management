package org.muellners.finscale.deposit.web.rest

import org.muellners.finscale.deposit.DepositAccountManagementApp
import org.muellners.finscale.deposit.config.SecurityBeanOverrideConfiguration
import org.muellners.finscale.deposit.domain.ProductInstance
import org.muellners.finscale.deposit.repository.ProductInstanceRepository
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


/**
 * Integration tests for the [ProductInstanceResource] REST controller.
 *
 * @see ProductInstanceResource
 */
@SpringBootTest(classes = [SecurityBeanOverrideConfiguration::class, DepositAccountManagementApp::class])
@AutoConfigureMockMvc
@WithMockUser
class ProductInstanceResourceIT  {

    @Autowired
    private lateinit var productInstanceRepository: ProductInstanceRepository

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


    private lateinit var restProductInstanceMockMvc: MockMvc

    private lateinit var productInstance: ProductInstance

    
    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val productInstanceResource = ProductInstanceResource(productInstanceRepository)		
         this.restProductInstanceMockMvc = MockMvcBuilders.standaloneSetup(productInstanceResource)		
             .setCustomArgumentResolvers(pageableArgumentResolver)		
             .setControllerAdvice(exceptionTranslator)		
             .setConversionService(createFormattingConversionService())		
             .setMessageConverters(jacksonMessageConverter)		
             .setValidator(validator).build()
    }


    @BeforeEach
    fun initTest() {
        productInstance = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProductInstance() {
        val databaseSizeBeforeCreate = productInstanceRepository.findAll().size

        // Create the ProductInstance
        restProductInstanceMockMvc.perform(
            post("/api/product-instances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productInstance))
        ).andExpect(status().isCreated)

        // Validate the ProductInstance in the database
        val productInstanceList = productInstanceRepository.findAll()
        assertThat(productInstanceList).hasSize(databaseSizeBeforeCreate + 1)
        val testProductInstance = productInstanceList[productInstanceList.size - 1]
        assertThat(testProductInstance.customerIdentifier).isEqualTo(DEFAULT_CUSTOMER_IDENTIFIER)
        assertThat(testProductInstance.productIdentifier).isEqualTo(DEFAULT_PRODUCT_IDENTIFIER)
        assertThat(testProductInstance.accountIdentifier).isEqualTo(DEFAULT_ACCOUNT_IDENTIFIER)
        assertThat(testProductInstance.alternativeAccountNumber).isEqualTo(DEFAULT_ALTERNATIVE_ACCOUNT_NUMBER)
        assertThat(testProductInstance.beneficiaries).isEqualTo(DEFAULT_BENEFICIARIES)
        assertThat(testProductInstance.openedOn).isEqualTo(DEFAULT_OPENED_ON)
        assertThat(testProductInstance.lastTransactionDate).isEqualTo(DEFAULT_LAST_TRANSACTION_DATE)
        assertThat(testProductInstance.state).isEqualTo(DEFAULT_STATE)
        assertThat(testProductInstance.balance).isEqualTo(DEFAULT_BALANCE)
    }

    @Test
    @Transactional
    fun createProductInstanceWithExistingId() {
        val databaseSizeBeforeCreate = productInstanceRepository.findAll().size

        // Create the ProductInstance with an existing ID
        productInstance.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductInstanceMockMvc.perform(
            post("/api/product-instances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productInstance))
        ).andExpect(status().isBadRequest)

        // Validate the ProductInstance in the database
        val productInstanceList = productInstanceRepository.findAll()
        assertThat(productInstanceList).hasSize(databaseSizeBeforeCreate)
    }


    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllProductInstances() {
        // Initialize the database
        productInstanceRepository.saveAndFlush(productInstance)
        
        // Get all the productInstanceList
        restProductInstanceMockMvc.perform(get("/api/product-instances?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productInstance.id?.toInt())))
            .andExpect(jsonPath("$.[*].customerIdentifier").value(hasItem(DEFAULT_CUSTOMER_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].productIdentifier").value(hasItem(DEFAULT_PRODUCT_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].accountIdentifier").value(hasItem(DEFAULT_ACCOUNT_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].alternativeAccountNumber").value(hasItem(DEFAULT_ALTERNATIVE_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].beneficiaries").value(hasItem(DEFAULT_BENEFICIARIES)))
            .andExpect(jsonPath("$.[*].openedOn").value(hasItem(DEFAULT_OPENED_ON)))
            .andExpect(jsonPath("$.[*].lastTransactionDate").value(hasItem(DEFAULT_LAST_TRANSACTION_DATE)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].balance").value(hasItem(DEFAULT_BALANCE.toDouble())))    }
    
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getProductInstance() {
        // Initialize the database
        productInstanceRepository.saveAndFlush(productInstance)

        val id = productInstance.id
        assertNotNull(id)

        // Get the productInstance
        restProductInstanceMockMvc.perform(get("/api/product-instances/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productInstance.id?.toInt()))
            .andExpect(jsonPath("$.customerIdentifier").value(DEFAULT_CUSTOMER_IDENTIFIER))
            .andExpect(jsonPath("$.productIdentifier").value(DEFAULT_PRODUCT_IDENTIFIER))
            .andExpect(jsonPath("$.accountIdentifier").value(DEFAULT_ACCOUNT_IDENTIFIER))
            .andExpect(jsonPath("$.alternativeAccountNumber").value(DEFAULT_ALTERNATIVE_ACCOUNT_NUMBER))
            .andExpect(jsonPath("$.beneficiaries").value(DEFAULT_BENEFICIARIES))
            .andExpect(jsonPath("$.openedOn").value(DEFAULT_OPENED_ON))
            .andExpect(jsonPath("$.lastTransactionDate").value(DEFAULT_LAST_TRANSACTION_DATE))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE))
            .andExpect(jsonPath("$.balance").value(DEFAULT_BALANCE.toDouble()))    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingProductInstance() {
        // Get the productInstance
        restProductInstanceMockMvc.perform(get("/api/product-instances/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateProductInstance() {
        // Initialize the database
        productInstanceRepository.saveAndFlush(productInstance)

        val databaseSizeBeforeUpdate = productInstanceRepository.findAll().size

        // Update the productInstance
        val id = productInstance.id
        assertNotNull(id)
        val updatedProductInstance = productInstanceRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedProductInstance are not directly saved in db
        em.detach(updatedProductInstance)
        updatedProductInstance.customerIdentifier = UPDATED_CUSTOMER_IDENTIFIER
        updatedProductInstance.productIdentifier = UPDATED_PRODUCT_IDENTIFIER
        updatedProductInstance.accountIdentifier = UPDATED_ACCOUNT_IDENTIFIER
        updatedProductInstance.alternativeAccountNumber = UPDATED_ALTERNATIVE_ACCOUNT_NUMBER
        updatedProductInstance.beneficiaries = UPDATED_BENEFICIARIES
        updatedProductInstance.openedOn = UPDATED_OPENED_ON
        updatedProductInstance.lastTransactionDate = UPDATED_LAST_TRANSACTION_DATE
        updatedProductInstance.state = UPDATED_STATE
        updatedProductInstance.balance = UPDATED_BALANCE

        restProductInstanceMockMvc.perform(
            put("/api/product-instances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedProductInstance))
        ).andExpect(status().isOk)

        // Validate the ProductInstance in the database
        val productInstanceList = productInstanceRepository.findAll()
        assertThat(productInstanceList).hasSize(databaseSizeBeforeUpdate)
        val testProductInstance = productInstanceList[productInstanceList.size - 1]
        assertThat(testProductInstance.customerIdentifier).isEqualTo(UPDATED_CUSTOMER_IDENTIFIER)
        assertThat(testProductInstance.productIdentifier).isEqualTo(UPDATED_PRODUCT_IDENTIFIER)
        assertThat(testProductInstance.accountIdentifier).isEqualTo(UPDATED_ACCOUNT_IDENTIFIER)
        assertThat(testProductInstance.alternativeAccountNumber).isEqualTo(UPDATED_ALTERNATIVE_ACCOUNT_NUMBER)
        assertThat(testProductInstance.beneficiaries).isEqualTo(UPDATED_BENEFICIARIES)
        assertThat(testProductInstance.openedOn).isEqualTo(UPDATED_OPENED_ON)
        assertThat(testProductInstance.lastTransactionDate).isEqualTo(UPDATED_LAST_TRANSACTION_DATE)
        assertThat(testProductInstance.state).isEqualTo(UPDATED_STATE)
        assertThat(testProductInstance.balance).isEqualTo(UPDATED_BALANCE)
    }

    @Test
    @Transactional
    fun updateNonExistingProductInstance() {
        val databaseSizeBeforeUpdate = productInstanceRepository.findAll().size


        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductInstanceMockMvc.perform(
            put("/api/product-instances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productInstance))
        ).andExpect(status().isBadRequest)

        // Validate the ProductInstance in the database
        val productInstanceList = productInstanceRepository.findAll()
        assertThat(productInstanceList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteProductInstance() {
        // Initialize the database
        productInstanceRepository.saveAndFlush(productInstance)

        val databaseSizeBeforeDelete = productInstanceRepository.findAll().size

        // Delete the productInstance
        restProductInstanceMockMvc.perform(
            delete("/api/product-instances/{id}", productInstance.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val productInstanceList = productInstanceRepository.findAll()
        assertThat(productInstanceList).hasSize(databaseSizeBeforeDelete - 1)
    }


    companion object {

        private const val DEFAULT_CUSTOMER_IDENTIFIER = "AAAAAAAAAA"
        private const val UPDATED_CUSTOMER_IDENTIFIER = "BBBBBBBBBB"

        private const val DEFAULT_PRODUCT_IDENTIFIER = "AAAAAAAAAA"
        private const val UPDATED_PRODUCT_IDENTIFIER = "BBBBBBBBBB"

        private const val DEFAULT_ACCOUNT_IDENTIFIER = "AAAAAAAAAA"
        private const val UPDATED_ACCOUNT_IDENTIFIER = "BBBBBBBBBB"

        private const val DEFAULT_ALTERNATIVE_ACCOUNT_NUMBER = "AAAAAAAAAA"
        private const val UPDATED_ALTERNATIVE_ACCOUNT_NUMBER = "BBBBBBBBBB"

        private const val DEFAULT_BENEFICIARIES = "AAAAAAAAAA"
        private const val UPDATED_BENEFICIARIES = "BBBBBBBBBB"

        private const val DEFAULT_OPENED_ON = "AAAAAAAAAA"
        private const val UPDATED_OPENED_ON = "BBBBBBBBBB"

        private const val DEFAULT_LAST_TRANSACTION_DATE = "AAAAAAAAAA"
        private const val UPDATED_LAST_TRANSACTION_DATE = "BBBBBBBBBB"

        private const val DEFAULT_STATE = "AAAAAAAAAA"
        private const val UPDATED_STATE = "BBBBBBBBBB"

        private const val DEFAULT_BALANCE: Double = 1.0
        private const val UPDATED_BALANCE: Double = 2.0

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): ProductInstance {
            val productInstance = ProductInstance(
                customerIdentifier = DEFAULT_CUSTOMER_IDENTIFIER,
                productIdentifier = DEFAULT_PRODUCT_IDENTIFIER,
                accountIdentifier = DEFAULT_ACCOUNT_IDENTIFIER,
                alternativeAccountNumber = DEFAULT_ALTERNATIVE_ACCOUNT_NUMBER,
                beneficiaries = DEFAULT_BENEFICIARIES,
                openedOn = DEFAULT_OPENED_ON,
                lastTransactionDate = DEFAULT_LAST_TRANSACTION_DATE,
                state = DEFAULT_STATE,
                balance = DEFAULT_BALANCE
            )

            return productInstance
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ProductInstance {
            val productInstance = ProductInstance(
                customerIdentifier = UPDATED_CUSTOMER_IDENTIFIER,
                productIdentifier = UPDATED_PRODUCT_IDENTIFIER,
                accountIdentifier = UPDATED_ACCOUNT_IDENTIFIER,
                alternativeAccountNumber = UPDATED_ALTERNATIVE_ACCOUNT_NUMBER,
                beneficiaries = UPDATED_BENEFICIARIES,
                openedOn = UPDATED_OPENED_ON,
                lastTransactionDate = UPDATED_LAST_TRANSACTION_DATE,
                state = UPDATED_STATE,
                balance = UPDATED_BALANCE
            )

            return productInstance
        }
    }
}
