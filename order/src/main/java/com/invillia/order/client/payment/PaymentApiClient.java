package com.invillia.order.client.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.hateoas.Resources;

import java.util.UUID;

@FeignClient("payment")
public interface PaymentApiClient {

    @PutMapping(value = "/payments/cancel/{id}")
    @CrossOrigin
    Resources<Payment> cancel(UUID id);
}
