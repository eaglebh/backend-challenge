package com.invillia.payment.service;

import com.invillia.payment.domain.Payment;
import com.invillia.payment.domain.enumeration.PaymentStatus;
import com.invillia.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Implementation for managing {@link Payment}.
 */
@Service
@Transactional
public class PaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Save a payment.
     *
     * @param payment the entity to save.
     * @return the persisted entity.
     */
    public Payment save(Payment payment) {
        log.debug("Request to save Payment : {}", payment);
        return paymentRepository.save(payment);
    }

    /**
     * Get all the payments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        log.debug("Request to get all Payments");
        return paymentRepository.findAll();
    }


    /**
     * Get one payment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Payment> findOne(UUID id) {
        log.debug("Request to get Payment : {}", id);
        return paymentRepository.findById(id);
    }

    /**
     * Delete the payment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete Payment : {}", id);
        paymentRepository.deleteById(id);
    }

    /**
     * Cancel the payment by id.
     *
     * @param id the id of the entity.
     */
    public Optional<Payment> cancel(UUID id) throws NonCancellableException {
        Optional<Payment> payment = paymentRepository.findById(id);
        if (payment.isPresent()) {
            Payment currentOrderInfo = payment.get();
            if (currentOrderInfo.getStatus().equals(PaymentStatus.DONE)) {
                currentOrderInfo.setStatus(PaymentStatus.CANCELLED);
                // TODO implement send message to "observers" about payment cancellation
            } else {
                throw new NonCancellableException("Can't be cancelled while not concluded");
            }
        }
        return payment;
    }
}
