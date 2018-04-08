package com.hawaiki.service;

import com.hawaiki.domain.Billing;
import com.hawaiki.repository.BillingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing Billing.
 */
@Service
@Transactional
public class BillingService {

    private final Logger log = LoggerFactory.getLogger(BillingService.class);

    private final BillingRepository billingRepository;

    public BillingService(BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    /**
     * Save a billing.
     *
     * @param billing the entity to save
     * @return the persisted entity
     */
    public Billing save(Billing billing) {
        log.debug("Request to save Billing : {}", billing);
        return billingRepository.save(billing);
    }

    /**
     * Get all the billings.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Billing> findAll(Pageable pageable) {
        log.debug("Request to get all Billings");
        return billingRepository.findAll(pageable);
    }

    /**
     * Get one billing by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Billing findOne(Long id) {
        log.debug("Request to get Billing : {}", id);
        return billingRepository.findOne(id);
    }

    /**
     * Delete the billing by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Billing : {}", id);
        billingRepository.delete(id);
    }
}
