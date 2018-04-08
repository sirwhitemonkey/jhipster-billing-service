package com.hawaiki.domain;


import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Billing.
 */
@Entity
@Table(name = "billing")
public class Billing implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "billing_date")
    private LocalDate billingDate;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "billing_amount")
    private Double billingAmount;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Billing name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Billing description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public Billing customerId(Integer customerId) {
        this.customerId = customerId;
        return this;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public LocalDate getBillingDate() {
        return billingDate;
    }

    public Billing billingDate(LocalDate billingDate) {
        this.billingDate = billingDate;
        return this;
    }

    public void setBillingDate(LocalDate billingDate) {
        this.billingDate = billingDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Billing quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getBillingAmount() {
        return billingAmount;
    }

    public Billing billingAmount(Double billingAmount) {
        this.billingAmount = billingAmount;
        return this;
    }

    public void setBillingAmount(Double billingAmount) {
        this.billingAmount = billingAmount;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Billing billing = (Billing) o;
        if (billing.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), billing.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Billing{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", customerId=" + getCustomerId() +
            ", billingDate='" + getBillingDate() + "'" +
            ", quantity=" + getQuantity() +
            ", billingAmount=" + getBillingAmount() +
            "}";
    }
}
