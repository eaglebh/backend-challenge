package com.invillia.order.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.invillia.order.domain.enumeration.OrderStatus;

/**
 * A OrderInfo.
 */
@Entity
@Table(name = "order_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int MAX_REFUND_DAYS = 10;

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "confirmation_date")
    private LocalDate confirmationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Type(type = "uuid-char")
    @Column(name = "store_id", length = 36)
    private UUID storeId;

    @Type(type = "uuid-char")
    @Column(name = "payment_id", length = 36)
    private UUID paymentId;

    @OneToOne
    @JoinColumn(unique = true)
    private Address address;

    @OneToMany(mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<OrderItem> items = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getConfirmationDate() {
        return confirmationDate;
    }

    public OrderInfo confirmationDate(LocalDate confirmationDate) {
        this.confirmationDate = confirmationDate;
        return this;
    }

    public void setConfirmationDate(LocalDate confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OrderInfo status(OrderStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public UUID getStoreId() {
        return storeId;
    }

    public OrderInfo storeId(UUID storeId) {
        this.storeId = storeId;
        return this;
    }

    public void setStoreId(UUID storeId) {
        this.storeId = storeId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public OrderInfo paymentId(UUID paymentId) {
        this.paymentId = paymentId;
        return this;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public Address getAddress() {
        return address;
    }

    public OrderInfo address(Address address) {
        this.address = address;
        return this;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<OrderItem> getItems() {
        return items;
    }

    public OrderInfo items(Set<OrderItem> orderItems) {
        this.items = orderItems;
        return this;
    }

    public OrderInfo addItem(OrderItem orderItem) {
        this.items.add(orderItem);
        orderItem.setOrder(this);
        return this;
    }

    public OrderInfo removeItem(OrderItem orderItem) {
        this.items.remove(orderItem);
        orderItem.setOrder(null);
        return this;
    }

    public void setItems(Set<OrderItem> orderItems) {
        this.items = orderItems;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    public static int getMaxRefundDays() {
        return MAX_REFUND_DAYS;
    }

    public boolean isCancellable() {
        LocalDate expirationDate = LocalDate.now().minusDays(OrderInfo.getMaxRefundDays() + 1);
        return this.getConfirmationDate().isAfter(expirationDate) &&
            this.getStatus().equals(OrderStatus.COMPLETE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderInfo)) {
            return false;
        }
        return id != null && id.equals(((OrderInfo) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
            "id=" + getId() +
            ", confirmationDate='" + getConfirmationDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", storeId='" + getStoreId() + "'" +
            ", paymentId='" + getPaymentId() + "'" +
            "}";
    }
}
