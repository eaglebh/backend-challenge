package com.invillia.order.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.invillia.order.web.rest.TestUtil;

import java.util.UUID;

public class OrderInfoTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderInfo.class);
        OrderInfo orderInfo1 = new OrderInfo();
        orderInfo1.setId(UUID.randomUUID());
        OrderInfo orderInfo2 = new OrderInfo();
        orderInfo2.setId(orderInfo1.getId());
        assertThat(orderInfo1).isEqualTo(orderInfo2);
        orderInfo2.setId(UUID.randomUUID());
        assertThat(orderInfo1).isNotEqualTo(orderInfo2);
        orderInfo1.setId(null);
        assertThat(orderInfo1).isNotEqualTo(orderInfo2);
    }
}
