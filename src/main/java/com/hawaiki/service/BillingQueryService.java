package com.hawaiki.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.hawaiki.domain.Billing;
import com.hawaiki.domain.*; // for static metamodels
import com.hawaiki.repository.BillingRepository;
import com.hawaiki.service.dto.BillingCriteria;


/**
 * Service for executing complex queries for Billing entities in the database.
 * The main input is a {@link BillingCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Billing} or a {@link Page} of {@link Billing} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BillingQueryService extends QueryService<Billing> {

    private final Logger log = LoggerFactory.getLogger(BillingQueryService.class);


    private final BillingRepository billingRepository;

    public BillingQueryService(BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    /**
     * Return a {@link List} of {@link Billing} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Billing> findByCriteria(BillingCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Billing> specification = createSpecification(criteria);
        return billingRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Billing} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Billing> findByCriteria(BillingCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Billing> specification = createSpecification(criteria);
        return billingRepository.findAll(specification, page);
    }

    /**
     * Function to convert BillingCriteria to a {@link Specifications}
     */
    private Specifications<Billing> createSpecification(BillingCriteria criteria) {
        Specifications<Billing> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Billing_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Billing_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Billing_.description));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCustomerId(), Billing_.customerId));
            }
            if (criteria.getBillingDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBillingDate(), Billing_.billingDate));
            }
            if (criteria.getQuantity() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQuantity(), Billing_.quantity));
            }
            if (criteria.getBillingAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBillingAmount(), Billing_.billingAmount));
            }
        }
        return specification;
    }

}
