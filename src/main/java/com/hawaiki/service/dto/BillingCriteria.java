package com.hawaiki.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;


import io.github.jhipster.service.filter.LocalDateFilter;



/**
 * Criteria class for the Billing entity. This class is used in BillingResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /billings?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BillingCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private IntegerFilter customerId;

    private LocalDateFilter billingDate;

    private IntegerFilter quantity;

    private DoubleFilter billingAmount;

    public BillingCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public IntegerFilter getCustomerId() {
        return customerId;
    }

    public void setCustomerId(IntegerFilter customerId) {
        this.customerId = customerId;
    }

    public LocalDateFilter getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(LocalDateFilter billingDate) {
        this.billingDate = billingDate;
    }

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public DoubleFilter getBillingAmount() {
        return billingAmount;
    }

    public void setBillingAmount(DoubleFilter billingAmount) {
        this.billingAmount = billingAmount;
    }

    @Override
    public String toString() {
        return "BillingCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
                (billingDate != null ? "billingDate=" + billingDate + ", " : "") +
                (quantity != null ? "quantity=" + quantity + ", " : "") +
                (billingAmount != null ? "billingAmount=" + billingAmount + ", " : "") +
            "}";
    }

}
