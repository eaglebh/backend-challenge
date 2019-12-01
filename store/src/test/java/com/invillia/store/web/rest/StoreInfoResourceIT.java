package com.invillia.store.web.rest;

import com.invillia.store.StoreApp;
import com.invillia.store.domain.StoreInfo;
import com.invillia.store.repository.StoreInfoRepository;
import com.invillia.store.service.StoreInfoService;
import com.invillia.store.web.rest.errors.ExceptionTranslator;

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
import java.util.List;
import java.util.UUID;

import static com.invillia.store.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link StoreInfoResource} REST controller.
 */
@SpringBootTest(classes = StoreApp.class)
public class StoreInfoResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private StoreInfoRepository storeInfoRepository;

    @Autowired
    private StoreInfoService storeInfoService;

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

    private MockMvc restStoreInfoMockMvc;

    private StoreInfo storeInfo;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final StoreInfoResource storeInfoResource = new StoreInfoResource(storeInfoService);
        this.restStoreInfoMockMvc = MockMvcBuilders.standaloneSetup(storeInfoResource)
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
    public static StoreInfo createEntity(EntityManager em) {
        StoreInfo storeInfo = new StoreInfo()
            .name(DEFAULT_NAME);
        return storeInfo;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StoreInfo createUpdatedEntity(EntityManager em) {
        StoreInfo storeInfo = new StoreInfo()
            .name(UPDATED_NAME);
        return storeInfo;
    }

    @BeforeEach
    public void initTest() {
        storeInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createStoreInfo() throws Exception {
        int databaseSizeBeforeCreate = storeInfoRepository.findAll().size();

        // Create the StoreInfo
        restStoreInfoMockMvc.perform(post("/api/store-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(storeInfo)))
            .andExpect(status().isCreated());

        // Validate the StoreInfo in the database
        List<StoreInfo> storeInfoList = storeInfoRepository.findAll();
        assertThat(storeInfoList).hasSize(databaseSizeBeforeCreate + 1);
        StoreInfo testStoreInfo = storeInfoList.get(storeInfoList.size() - 1);
        assertThat(testStoreInfo.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createStoreInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = storeInfoRepository.findAll().size();

        // Create the StoreInfo with an existing ID
        storeInfo.setId(UUID.randomUUID());

        // An entity with an existing ID cannot be created, so this API call must fail
        restStoreInfoMockMvc.perform(post("/api/store-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(storeInfo)))
            .andExpect(status().isBadRequest());

        // Validate the StoreInfo in the database
        List<StoreInfo> storeInfoList = storeInfoRepository.findAll();
        assertThat(storeInfoList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = storeInfoRepository.findAll().size();
        // set the field null
        storeInfo.setName(null);

        // Create the StoreInfo, which fails.

        restStoreInfoMockMvc.perform(post("/api/store-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(storeInfo)))
            .andExpect(status().isBadRequest());

        List<StoreInfo> storeInfoList = storeInfoRepository.findAll();
        assertThat(storeInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStoreInfos() throws Exception {
        // Initialize the database
        storeInfoRepository.saveAndFlush(storeInfo);

        // Get all the storeInfoList
        restStoreInfoMockMvc.perform(get("/api/store-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(storeInfo.getId().toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    public void getStoreInfo() throws Exception {
        // Initialize the database
        storeInfoRepository.saveAndFlush(storeInfo);

        // Get the storeInfo
        restStoreInfoMockMvc.perform(get("/api/store-infos/{id}", storeInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(storeInfo.getId().toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    public void getNonExistingStoreInfo() throws Exception {
        // Get the storeInfo
        restStoreInfoMockMvc.perform(get("/api/store-infos/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStoreInfo() throws Exception {
        // Initialize the database
        storeInfoService.save(storeInfo);

        int databaseSizeBeforeUpdate = storeInfoRepository.findAll().size();

        // Update the storeInfo
        StoreInfo updatedStoreInfo = storeInfoRepository.findById(storeInfo.getId()).get();
        // Disconnect from session so that the updates on updatedStoreInfo are not directly saved in db
        em.detach(updatedStoreInfo);
        updatedStoreInfo
            .name(UPDATED_NAME);

        restStoreInfoMockMvc.perform(put("/api/store-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedStoreInfo)))
            .andExpect(status().isOk());

        // Validate the StoreInfo in the database
        List<StoreInfo> storeInfoList = storeInfoRepository.findAll();
        assertThat(storeInfoList).hasSize(databaseSizeBeforeUpdate);
        StoreInfo testStoreInfo = storeInfoList.get(storeInfoList.size() - 1);
        assertThat(testStoreInfo.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingStoreInfo() throws Exception {
        int databaseSizeBeforeUpdate = storeInfoRepository.findAll().size();

        // Create the StoreInfo

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStoreInfoMockMvc.perform(put("/api/store-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(storeInfo)))
            .andExpect(status().isBadRequest());

        // Validate the StoreInfo in the database
        List<StoreInfo> storeInfoList = storeInfoRepository.findAll();
        assertThat(storeInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteStoreInfo() throws Exception {
        // Initialize the database
        storeInfoService.save(storeInfo);

        int databaseSizeBeforeDelete = storeInfoRepository.findAll().size();

        // Delete the storeInfo
        restStoreInfoMockMvc.perform(delete("/api/store-infos/{id}", storeInfo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StoreInfo> storeInfoList = storeInfoRepository.findAll();
        assertThat(storeInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
