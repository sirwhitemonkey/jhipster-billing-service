package com.hawaiki.web.rest;

import com.hawaiki.BillingServiceApp;

import com.hawaiki.domain.Billing;
import com.hawaiki.repository.BillingRepository;
import com.hawaiki.service.BillingService;
import com.hawaiki.web.rest.errors.ExceptionTranslator;
import com.hawaiki.service.dto.BillingCriteria;
import com.hawaiki.service.BillingQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static com.hawaiki.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BillingResource REST controller.
 *
 * @see BillingResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillingServiceApp.class)
public class BillingResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_CUSTOMER_ID = 1;
    private static final Integer UPDATED_CUSTOMER_ID = 2;

    private static final LocalDate DEFAULT_BILLING_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BILLING_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final Double DEFAULT_BILLING_AMOUNT = 1D;
    private static final Double UPDATED_BILLING_AMOUNT = 2D;

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private BillingService billingService;

    @Autowired
    private BillingQueryService billingQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBillingMockMvc;

    private Billing billing;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BillingResource billingResource = new BillingResource(billingService, billingQueryService);
        this.restBillingMockMvc = MockMvcBuilders.standaloneSetup(billingResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Billing createEntity(EntityManager em) {
        Billing billing = new Billing()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .customerId(DEFAULT_CUSTOMER_ID)
            .billingDate(DEFAULT_BILLING_DATE)
            .quantity(DEFAULT_QUANTITY)
            .billingAmount(DEFAULT_BILLING_AMOUNT);
        return billing;
    }

    @Before
    public void initTest() {
        billing = createEntity(em);
    }

    @Test
    @Transactional
    public void createBilling() throws Exception {
        int databaseSizeBeforeCreate = billingRepository.findAll().size();

        // Create the Billing
        restBillingMockMvc.perform(post("/api/billings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(billing)))
            .andExpect(status().isCreated());

        // Validate the Billing in the database
        List<Billing> billingList = billingRepository.findAll();
        assertThat(billingList).hasSize(databaseSizeBeforeCreate + 1);
        Billing testBilling = billingList.get(billingList.size() - 1);
        assertThat(testBilling.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBilling.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBilling.getCustomerId()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testBilling.getBillingDate()).isEqualTo(DEFAULT_BILLING_DATE);
        assertThat(testBilling.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testBilling.getBillingAmount()).isEqualTo(DEFAULT_BILLING_AMOUNT);
    }

    @Test
    @Transactional
    public void createBillingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = billingRepository.findAll().size();

        // Create the Billing with an existing ID
        billing.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBillingMockMvc.perform(post("/api/billings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(billing)))
            .andExpect(status().isBadRequest());

        // Validate the Billing in the database
        List<Billing> billingList = billingRepository.findAll();
        assertThat(billingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllBillings() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList
        restBillingMockMvc.perform(get("/api/billings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(billing.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID)))
            .andExpect(jsonPath("$.[*].billingDate").value(hasItem(DEFAULT_BILLING_DATE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].billingAmount").value(hasItem(DEFAULT_BILLING_AMOUNT.doubleValue())));
    }

    @Test
    @Transactional
    public void getBilling() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get the billing
        restBillingMockMvc.perform(get("/api/billings/{id}", billing.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(billing.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.customerId").value(DEFAULT_CUSTOMER_ID))
            .andExpect(jsonPath("$.billingDate").value(DEFAULT_BILLING_DATE.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.billingAmount").value(DEFAULT_BILLING_AMOUNT.doubleValue()));
    }

    @Test
    @Transactional
    public void getAllBillingsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where name equals to DEFAULT_NAME
        defaultBillingShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the billingList where name equals to UPDATED_NAME
        defaultBillingShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBillingsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where name in DEFAULT_NAME or UPDATED_NAME
        defaultBillingShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the billingList where name equals to UPDATED_NAME
        defaultBillingShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBillingsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where name is not null
        defaultBillingShouldBeFound("name.specified=true");

        // Get all the billingList where name is null
        defaultBillingShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllBillingsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where description equals to DEFAULT_DESCRIPTION
        defaultBillingShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the billingList where description equals to UPDATED_DESCRIPTION
        defaultBillingShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllBillingsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultBillingShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the billingList where description equals to UPDATED_DESCRIPTION
        defaultBillingShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllBillingsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where description is not null
        defaultBillingShouldBeFound("description.specified=true");

        // Get all the billingList where description is null
        defaultBillingShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllBillingsByCustomerIdIsEqualToSomething() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where customerId equals to DEFAULT_CUSTOMER_ID
        defaultBillingShouldBeFound("customerId.equals=" + DEFAULT_CUSTOMER_ID);

        // Get all the billingList where customerId equals to UPDATED_CUSTOMER_ID
        defaultBillingShouldNotBeFound("customerId.equals=" + UPDATED_CUSTOMER_ID);
    }

    @Test
    @Transactional
    public void getAllBillingsByCustomerIdIsInShouldWork() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where customerId in DEFAULT_CUSTOMER_ID or UPDATED_CUSTOMER_ID
        defaultBillingShouldBeFound("customerId.in=" + DEFAULT_CUSTOMER_ID + "," + UPDATED_CUSTOMER_ID);

        // Get all the billingList where customerId equals to UPDATED_CUSTOMER_ID
        defaultBillingShouldNotBeFound("customerId.in=" + UPDATED_CUSTOMER_ID);
    }

    @Test
    @Transactional
    public void getAllBillingsByCustomerIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where customerId is not null
        defaultBillingShouldBeFound("customerId.specified=true");

        // Get all the billingList where customerId is null
        defaultBillingShouldNotBeFound("customerId.specified=false");
    }

    @Test
    @Transactional
    public void getAllBillingsByCustomerIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where customerId greater than or equals to DEFAULT_CUSTOMER_ID
        defaultBillingShouldBeFound("customerId.greaterOrEqualThan=" + DEFAULT_CUSTOMER_ID);

        // Get all the billingList where customerId greater than or equals to UPDATED_CUSTOMER_ID
        defaultBillingShouldNotBeFound("customerId.greaterOrEqualThan=" + UPDATED_CUSTOMER_ID);
    }

    @Test
    @Transactional
    public void getAllBillingsByCustomerIdIsLessThanSomething() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where customerId less than or equals to DEFAULT_CUSTOMER_ID
        defaultBillingShouldNotBeFound("customerId.lessThan=" + DEFAULT_CUSTOMER_ID);

        // Get all the billingList where customerId less than or equals to UPDATED_CUSTOMER_ID
        defaultBillingShouldBeFound("customerId.lessThan=" + UPDATED_CUSTOMER_ID);
    }


    @Test
    @Transactional
    public void getAllBillingsByBillingDateIsEqualToSomething() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where billingDate equals to DEFAULT_BILLING_DATE
        defaultBillingShouldBeFound("billingDate.equals=" + DEFAULT_BILLING_DATE);

        // Get all the billingList where billingDate equals to UPDATED_BILLING_DATE
        defaultBillingShouldNotBeFound("billingDate.equals=" + UPDATED_BILLING_DATE);
    }

    @Test
    @Transactional
    public void getAllBillingsByBillingDateIsInShouldWork() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where billingDate in DEFAULT_BILLING_DATE or UPDATED_BILLING_DATE
        defaultBillingShouldBeFound("billingDate.in=" + DEFAULT_BILLING_DATE + "," + UPDATED_BILLING_DATE);

        // Get all the billingList where billingDate equals to UPDATED_BILLING_DATE
        defaultBillingShouldNotBeFound("billingDate.in=" + UPDATED_BILLING_DATE);
    }

    @Test
    @Transactional
    public void getAllBillingsByBillingDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where billingDate is not null
        defaultBillingShouldBeFound("billingDate.specified=true");

        // Get all the billingList where billingDate is null
        defaultBillingShouldNotBeFound("billingDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllBillingsByBillingDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where billingDate greater than or equals to DEFAULT_BILLING_DATE
        defaultBillingShouldBeFound("billingDate.greaterOrEqualThan=" + DEFAULT_BILLING_DATE);

        // Get all the billingList where billingDate greater than or equals to UPDATED_BILLING_DATE
        defaultBillingShouldNotBeFound("billingDate.greaterOrEqualThan=" + UPDATED_BILLING_DATE);
    }

    @Test
    @Transactional
    public void getAllBillingsByBillingDateIsLessThanSomething() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where billingDate less than or equals to DEFAULT_BILLING_DATE
        defaultBillingShouldNotBeFound("billingDate.lessThan=" + DEFAULT_BILLING_DATE);

        // Get all the billingList where billingDate less than or equals to UPDATED_BILLING_DATE
        defaultBillingShouldBeFound("billingDate.lessThan=" + UPDATED_BILLING_DATE);
    }


    @Test
    @Transactional
    public void getAllBillingsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where quantity equals to DEFAULT_QUANTITY
        defaultBillingShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the billingList where quantity equals to UPDATED_QUANTITY
        defaultBillingShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    public void getAllBillingsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultBillingShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the billingList where quantity equals to UPDATED_QUANTITY
        defaultBillingShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    public void getAllBillingsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where quantity is not null
        defaultBillingShouldBeFound("quantity.specified=true");

        // Get all the billingList where quantity is null
        defaultBillingShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    public void getAllBillingsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where quantity greater than or equals to DEFAULT_QUANTITY
        defaultBillingShouldBeFound("quantity.greaterOrEqualThan=" + DEFAULT_QUANTITY);

        // Get all the billingList where quantity greater than or equals to UPDATED_QUANTITY
        defaultBillingShouldNotBeFound("quantity.greaterOrEqualThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    public void getAllBillingsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where quantity less than or equals to DEFAULT_QUANTITY
        defaultBillingShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the billingList where quantity less than or equals to UPDATED_QUANTITY
        defaultBillingShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }


    @Test
    @Transactional
    public void getAllBillingsByBillingAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where billingAmount equals to DEFAULT_BILLING_AMOUNT
        defaultBillingShouldBeFound("billingAmount.equals=" + DEFAULT_BILLING_AMOUNT);

        // Get all the billingList where billingAmount equals to UPDATED_BILLING_AMOUNT
        defaultBillingShouldNotBeFound("billingAmount.equals=" + UPDATED_BILLING_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllBillingsByBillingAmountIsInShouldWork() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where billingAmount in DEFAULT_BILLING_AMOUNT or UPDATED_BILLING_AMOUNT
        defaultBillingShouldBeFound("billingAmount.in=" + DEFAULT_BILLING_AMOUNT + "," + UPDATED_BILLING_AMOUNT);

        // Get all the billingList where billingAmount equals to UPDATED_BILLING_AMOUNT
        defaultBillingShouldNotBeFound("billingAmount.in=" + UPDATED_BILLING_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllBillingsByBillingAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        billingRepository.saveAndFlush(billing);

        // Get all the billingList where billingAmount is not null
        defaultBillingShouldBeFound("billingAmount.specified=true");

        // Get all the billingList where billingAmount is null
        defaultBillingShouldNotBeFound("billingAmount.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultBillingShouldBeFound(String filter) throws Exception {
        restBillingMockMvc.perform(get("/api/billings?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(billing.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID)))
            .andExpect(jsonPath("$.[*].billingDate").value(hasItem(DEFAULT_BILLING_DATE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].billingAmount").value(hasItem(DEFAULT_BILLING_AMOUNT.doubleValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultBillingShouldNotBeFound(String filter) throws Exception {
        restBillingMockMvc.perform(get("/api/billings?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingBilling() throws Exception {
        // Get the billing
        restBillingMockMvc.perform(get("/api/billings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBilling() throws Exception {
        // Initialize the database
        billingService.save(billing);

        int databaseSizeBeforeUpdate = billingRepository.findAll().size();

        // Update the billing
        Billing updatedBilling = billingRepository.findOne(billing.getId());
        // Disconnect from session so that the updates on updatedBilling are not directly saved in db
        em.detach(updatedBilling);
        updatedBilling
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .customerId(UPDATED_CUSTOMER_ID)
            .billingDate(UPDATED_BILLING_DATE)
            .quantity(UPDATED_QUANTITY)
            .billingAmount(UPDATED_BILLING_AMOUNT);

        restBillingMockMvc.perform(put("/api/billings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBilling)))
            .andExpect(status().isOk());

        // Validate the Billing in the database
        List<Billing> billingList = billingRepository.findAll();
        assertThat(billingList).hasSize(databaseSizeBeforeUpdate);
        Billing testBilling = billingList.get(billingList.size() - 1);
        assertThat(testBilling.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBilling.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBilling.getCustomerId()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testBilling.getBillingDate()).isEqualTo(UPDATED_BILLING_DATE);
        assertThat(testBilling.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testBilling.getBillingAmount()).isEqualTo(UPDATED_BILLING_AMOUNT);
    }

    @Test
    @Transactional
    public void updateNonExistingBilling() throws Exception {
        int databaseSizeBeforeUpdate = billingRepository.findAll().size();

        // Create the Billing

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBillingMockMvc.perform(put("/api/billings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(billing)))
            .andExpect(status().isCreated());

        // Validate the Billing in the database
        List<Billing> billingList = billingRepository.findAll();
        assertThat(billingList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBilling() throws Exception {
        // Initialize the database
        billingService.save(billing);

        int databaseSizeBeforeDelete = billingRepository.findAll().size();

        // Get the billing
        restBillingMockMvc.perform(delete("/api/billings/{id}", billing.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Billing> billingList = billingRepository.findAll();
        assertThat(billingList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Billing.class);
        Billing billing1 = new Billing();
        billing1.setId(1L);
        Billing billing2 = new Billing();
        billing2.setId(billing1.getId());
        assertThat(billing1).isEqualTo(billing2);
        billing2.setId(2L);
        assertThat(billing1).isNotEqualTo(billing2);
        billing1.setId(null);
        assertThat(billing1).isNotEqualTo(billing2);
    }
}
