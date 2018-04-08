package com.hawaiki.repository;

import com.hawaiki.domain.Billing;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Billing entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BillingRepository extends JpaRepository<Billing, Long>, JpaSpecificationExecutor<Billing> {

}
