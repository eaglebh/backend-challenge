package com.invillia.order.client.payment;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * A Payment.
 */
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;

    private PaymentStatus status;

    private String creditCardNumber;

    private LocalDate paymentDate;

    private UUID orderId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Payment status(PaymentStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public Payment creditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
        return this;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public Payment paymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
        return this;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public Payment orderId(UUID orderId) {
        this.orderId = orderId;
        return this;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return id != null && id.equals(((Payment) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", creditCardNumber='" + getCreditCardNumber() + "'" +
            ", paymentDate='" + getPaymentDate() + "'" +
            ", orderId='" + getOrderId() + "'" +
            "}";
    }
}
