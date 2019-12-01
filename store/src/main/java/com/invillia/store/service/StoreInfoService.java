package com.invillia.store.service;

import com.invillia.store.domain.StoreInfo;
import com.invillia.store.repository.StoreInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link StoreInfo}.
 */
@Service
@Transactional
public class StoreInfoService {

    private final Logger log = LoggerFactory.getLogger(StoreInfoService.class);

    private final StoreInfoRepository storeInfoRepository;

    public StoreInfoService(StoreInfoRepository storeInfoRepository) {
        this.storeInfoRepository = storeInfoRepository;
    }

    /**
     * Save a storeInfo.
     *
     * @param storeInfo the entity to save.
     * @return the persisted entity.
     */
    public StoreInfo save(StoreInfo storeInfo) {
        log.debug("Request to save StoreInfo : {}", storeInfo);
        return storeInfoRepository.save(storeInfo);
    }

    /**
     * Get all the storeInfos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StoreInfo> findAll(Pageable pageable) {
        log.debug("Request to get all StoreInfos");
        return storeInfoRepository.findAll(pageable);
    }


    /**
     * Get one storeInfo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StoreInfo> findOne(Long id) {
        log.debug("Request to get StoreInfo : {}", id);
        return storeInfoRepository.findById(id);
    }

    /**
     * Delete the storeInfo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete StoreInfo : {}", id);
        storeInfoRepository.deleteById(id);
    }
}
