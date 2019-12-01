package com.invillia.store.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.invillia.store.web.rest.TestUtil;

public class StoreInfoTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StoreInfo.class);
        StoreInfo storeInfo1 = new StoreInfo();
        storeInfo1.setId(1L);
        StoreInfo storeInfo2 = new StoreInfo();
        storeInfo2.setId(storeInfo1.getId());
        assertThat(storeInfo1).isEqualTo(storeInfo2);
        storeInfo2.setId(2L);
        assertThat(storeInfo1).isNotEqualTo(storeInfo2);
        storeInfo1.setId(null);
        assertThat(storeInfo1).isNotEqualTo(storeInfo2);
    }
}
