package com.invillia.order.web.rest;

import com.invillia.order.OrderApp;
import com.invillia.order.domain.OrderInfo;
import com.invillia.order.repository.OrderInfoRepository;
import com.invillia.order.service.OrderInfoService;
import com.invillia.order.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static com.invillia.order.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.invillia.order.domain.enumeration.OrderStatus;
/**
 * Integration tests for the {@link OrderInfoResource} REST controller.
 */
@SpringBootTest(classes = OrderApp.class)
public class OrderInfoResourceIT {

    private static final LocalDate DEFAULT_CONFIRMATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CONFIRMATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.NEW;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.PROCESSING;

    private static final UUID DEFAULT_STORE_ID = UUID.randomUUID();
    private static final UUID UPDATED_STORE_ID = UUID.randomUUID();

    private static final UUID DEFAULT_PAYMENT_ID = UUID.randomUUID();
    private static final UUID UPDATED_PAYMENT_ID = UUID.randomUUID();

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restOrderInfoMockMvc;

    private OrderInfo orderInfo;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final OrderInfoResource orderInfoResource = new OrderInfoResource(orderInfoService);
        this.restOrderInfoMockMvc = MockMvcBuilders.standaloneSetup(orderInfoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderInfo createEntity(EntityManager em) {
        OrderInfo orderInfo = new OrderInfo()
            .confirmationDate(DEFAULT_CONFIRMATION_DATE)
            .status(DEFAULT_STATUS)
            .storeId(DEFAULT_STORE_ID)
            .paymentId(DEFAULT_PAYMENT_ID);
        return orderInfo;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderInfo createUpdatedEntity(EntityManager em) {
        OrderInfo orderInfo = new OrderInfo()
            .confirmationDate(UPDATED_CONFIRMATION_DATE)
            .status(UPDATED_STATUS)
            .storeId(UPDATED_STORE_ID)
            .paymentId(UPDATED_PAYMENT_ID);
        return orderInfo;
    }

    @BeforeEach
    public void initTest() {
        orderInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrderInfo() throws Exception {
        int databaseSizeBeforeCreate = orderInfoRepository.findAll().size();

        // Create the OrderInfo
        restOrderInfoMockMvc.perform(post("/api/order-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderInfo)))
            .andExpect(status().isCreated());

        // Validate the OrderInfo in the database
        List<OrderInfo> orderInfoList = orderInfoRepository.findAll();
        assertThat(orderInfoList).hasSize(databaseSizeBeforeCreate + 1);
        OrderInfo testOrderInfo = orderInfoList.get(orderInfoList.size() - 1);
        assertThat(testOrderInfo.getConfirmationDate()).isEqualTo(DEFAULT_CONFIRMATION_DATE);
        assertThat(testOrderInfo.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testOrderInfo.getStoreId()).isEqualTo(DEFAULT_STORE_ID);
        assertThat(testOrderInfo.getPaymentId()).isEqualTo(DEFAULT_PAYMENT_ID);
    }

    @Test
    @Transactional
    public void createOrderInfoWithExistingId() throws Exception {
        // Initialize the database
        orderInfoRepository.saveAndFlush(orderInfo);

        List<OrderInfo> orderInfoListBeforeCreate = orderInfoRepository.findAll();
        int databaseSizeBeforeCreate = orderInfoListBeforeCreate.size();

        // Create the OrderInfo with an existing ID
        orderInfo.setId(orderInfoListBeforeCreate.get(0).getId());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderInfoMockMvc.perform(post("/api/order-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderInfo)))
            .andExpect(status().isBadRequest());

        // Validate the OrderInfo in the database
        List<OrderInfo> orderInfoList = orderInfoRepository.findAll();
        assertThat(orderInfoList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllOrderInfos() throws Exception {
        // Initialize the database
        orderInfoRepository.saveAndFlush(orderInfo);

        // Get all the orderInfoList
        restOrderInfoMockMvc.perform(get("/api/order-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderInfo.getId().toString())))
            .andExpect(jsonPath("$.[*].confirmationDate").value(hasItem(DEFAULT_CONFIRMATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].storeId").value(hasItem(DEFAULT_STORE_ID.toString())))
            .andExpect(jsonPath("$.[*].paymentId").value(hasItem(DEFAULT_PAYMENT_ID.toString())));
    }

    @Test
    @Transactional
    public void getOrderInfo() throws Exception {
        // Initialize the database
        orderInfoRepository.saveAndFlush(orderInfo);

        // Get the orderInfo
        restOrderInfoMockMvc.perform(get("/api/order-infos/{id}", orderInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(orderInfo.getId().toString()))
            .andExpect(jsonPath("$.confirmationDate").value(DEFAULT_CONFIRMATION_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.storeId").value(DEFAULT_STORE_ID.toString()))
            .andExpect(jsonPath("$.paymentId").value(DEFAULT_PAYMENT_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOrderInfo() throws Exception {
        // Get the orderInfo
        restOrderInfoMockMvc.perform(get("/api/order-infos/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrderInfo() throws Exception {
        // Initialize the database
        orderInfoService.save(orderInfo);

        int databaseSizeBeforeUpdate = orderInfoRepository.findAll().size();

        // Update the orderInfo
        OrderInfo updatedOrderInfo = orderInfoRepository.findById(orderInfo.getId()).get();
        // Disconnect from session so that the updates on updatedOrderInfo are not directly saved in db
        em.detach(updatedOrderInfo);
        updatedOrderInfo
            .confirmationDate(UPDATED_CONFIRMATION_DATE)
            .status(UPDATED_STATUS)
            .storeId(UPDATED_STORE_ID)
            .paymentId(UPDATED_PAYMENT_ID);

        restOrderInfoMockMvc.perform(put("/api/order-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOrderInfo)))
            .andExpect(status().isOk());

        // Validate the OrderInfo in the database
        List<OrderInfo> orderInfoList = orderInfoRepository.findAll();
        assertThat(orderInfoList).hasSize(databaseSizeBeforeUpdate);
        OrderInfo testOrderInfo = orderInfoList.get(orderInfoList.size() - 1);
        assertThat(testOrderInfo.getConfirmationDate()).isEqualTo(UPDATED_CONFIRMATION_DATE);
        assertThat(testOrderInfo.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOrderInfo.getStoreId()).isEqualTo(UPDATED_STORE_ID);
        assertThat(testOrderInfo.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingOrderInfo() throws Exception {
        int databaseSizeBeforeUpdate = orderInfoRepository.findAll().size();

        // Create the OrderInfo # not actually

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderInfoMockMvc.perform(put("/api/order-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderInfo)))
            .andExpect(status().isBadRequest());

        // Validate the OrderInfo in the database
        List<OrderInfo> orderInfoList = orderInfoRepository.findAll();
        assertThat(orderInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteOrderInfo() throws Exception {
        // Initialize the database
        orderInfoService.save(orderInfo);

        int databaseSizeBeforeDelete = orderInfoRepository.findAll().size();

        // Delete the orderInfo
        restOrderInfoMockMvc.perform(delete("/api/order-infos/{id}", orderInfo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderInfo> orderInfoList = orderInfoRepository.findAll();
        assertThat(orderInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void noRefundOrderAfterMaxRefundDays() throws Exception {
        // Initialize the database
        orderInfo.setConfirmationDate(LocalDate.now().minusDays(OrderInfo.getMaxRefundDays()+1));
        orderInfoRepository.saveAndFlush(orderInfo);
        em.detach(orderInfo);

        int databaseSizeBeforeUpdate = orderInfoRepository.findAll().size();

        // Update the orderInfo
        restOrderInfoMockMvc.perform(put("/api/order-infos/refund/{id}", orderInfo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isPreconditionFailed());

        // Validate the OrderInfo in the database
        List<OrderInfo> orderInfoList = orderInfoRepository.findAll();
        assertThat(orderInfoList).hasSize(databaseSizeBeforeUpdate);
        OrderInfo testOrderInfo = orderInfoList.get(orderInfoList.size() - 1);
        assertThat(testOrderInfo.getStatus()).isEqualTo(orderInfo.getStatus());
    }

    @Test
    @Transactional
    public void refundOrderBeforeMaxRefundDays() throws Exception {
        // Initialize the database
        orderInfo.setConfirmationDate(LocalDate.now().minusDays(OrderInfo.getMaxRefundDays()));
        orderInfoRepository.saveAndFlush(orderInfo);
        em.detach(orderInfo);

        restOrderInfoMockMvc.perform(put("/api/order-infos/refund/{id}", orderInfo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the OrderInfo in the database
        List<OrderInfo> orderInfoList = orderInfoRepository.findAll();
        OrderInfo testOrderInfo = orderInfoList.get(orderInfoList.size() - 1);
        assertThat(testOrderInfo.getStatus()).isEqualTo(OrderStatus.PENDING_CANCEL);
    }
}
