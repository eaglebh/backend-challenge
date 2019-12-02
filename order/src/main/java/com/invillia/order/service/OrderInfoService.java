package com.invillia.order.service;

import com.invillia.order.domain.OrderInfo;
import com.invillia.order.domain.enumeration.OrderStatus;
import com.invillia.order.repository.OrderInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Implementation for managing {@link OrderInfo}.
 */
@Service
@Transactional
public class OrderInfoService {

    private final Logger log = LoggerFactory.getLogger(OrderInfoService.class);

    private final OrderInfoRepository orderInfoRepository;

    public OrderInfoService(OrderInfoRepository orderInfoRepository) {
        this.orderInfoRepository = orderInfoRepository;
    }

    /**
     * Save a orderInfo.
     *
     * @param orderInfo the entity to save.
     * @return the persisted entity.
     */
    public OrderInfo save(OrderInfo orderInfo) {
        log.debug("Request to save OrderInfo : {}", orderInfo);
        return orderInfoRepository.save(orderInfo);
    }

    /**
     * Get all the orderInfos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderInfo> findAll(Pageable pageable) {
        log.debug("Request to get all OrderInfos");
        return orderInfoRepository.findAll(pageable);
    }


    /**
     * Get one orderInfo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrderInfo> findOne(UUID id) {
        log.debug("Request to get OrderInfo : {}", id);
        return orderInfoRepository.findById(id);
    }

    /**
     * Delete the orderInfo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete OrderInfo : {}", id);
        orderInfoRepository.deleteById(id);
    }

    public Optional<OrderInfo> refund(UUID id) throws NonRefundableException {
        Optional<OrderInfo> orderInfo = orderInfoRepository.findById(id);
        if (orderInfo.isPresent()) {
            OrderInfo currentOrderInfo = orderInfo.get();
            LocalDate expirationDate = LocalDate.now().minusDays(OrderInfo.getMaxRefundDays() + 1);
            if (currentOrderInfo.getConfirmationDate().isAfter(expirationDate)) {
                currentOrderInfo.setStatus(OrderStatus.PENDING_CANCEL);
                // @TODO implement paymentApiClient properly
            } else {
                throw new NonRefundableException("Non refundable after expiration");
            }
        }
        return orderInfo;
    }
}
