package com.invillia.order.service;

import com.invillia.order.domain.OrderInfo;
import com.invillia.order.domain.OrderItem;
import com.invillia.order.domain.enumeration.OrderItemStatus;
import com.invillia.order.domain.enumeration.OrderStatus;
import com.invillia.order.repository.OrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Service Implementation for managing {@link OrderItem}.
 */
@Service
@Transactional
public class OrderItemService {

    private final Logger log = LoggerFactory.getLogger(OrderItemService.class);

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Save a orderItem.
     *
     * @param orderItem the entity to save.
     * @return the persisted entity.
     */
    public OrderItem save(OrderItem orderItem) {
        log.debug("Request to save OrderItem : {}", orderItem);
        return orderItemRepository.save(orderItem);
    }

    /**
     * Get all the orderItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderItem> findAll(Pageable pageable) {
        log.debug("Request to get all OrderItems");
        return orderItemRepository.findAll(pageable);
    }


    /**
     * Get one orderItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrderItem> findOne(UUID id) {
        log.debug("Request to get OrderItem : {}", id);
        return orderItemRepository.findById(id);
    }

    /**
     * Delete the orderItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete OrderItem : {}", id);
        orderItemRepository.deleteById(id);
    }

    public Optional<OrderItem> refund(UUID id) throws NonRefundableException {
        Optional<OrderItem> orderItem = orderItemRepository.findById(id);
        if (orderItem.isPresent()) {
            OrderItem currentOrderItem = orderItem.get();
            if (currentOrderItem.getOrder() != null && currentOrderItem.getOrder().isCancellable()) {
                currentOrderItem.setStatus(OrderItemStatus.CANCELLED);
                // @TODO implement paymentApiClient properly and call refund
            } else {
                throw new NonRefundableException("Non refundable after expiration");
            }
        }
        return orderItem;
    }
}
