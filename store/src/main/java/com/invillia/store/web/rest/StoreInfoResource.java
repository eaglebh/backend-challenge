package com.invillia.store.web.rest;

import com.invillia.store.domain.StoreInfo;
import com.invillia.store.service.StoreInfoService;
import com.invillia.store.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing {@link com.invillia.store.domain.StoreInfo}.
 */
@RestController
@RequestMapping("/api")
public class StoreInfoResource {

    private final Logger log = LoggerFactory.getLogger(StoreInfoResource.class);

    private static final String ENTITY_NAME = "storeStoreInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StoreInfoService storeInfoService;

    public StoreInfoResource(StoreInfoService storeInfoService) {
        this.storeInfoService = storeInfoService;
    }

    /**
     * {@code POST  /store-infos} : Create a new storeInfo.
     *
     * @param storeInfo the storeInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new storeInfo, or with status {@code 400 (Bad Request)} if the storeInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/store-infos")
    public ResponseEntity<StoreInfo> createStoreInfo(@Valid @RequestBody StoreInfo storeInfo) throws URISyntaxException {
        log.debug("REST request to save StoreInfo : {}", storeInfo);
        if (storeInfo.getId() != null) {
            throw new BadRequestAlertException("A new storeInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StoreInfo result = storeInfoService.save(storeInfo);
        return ResponseEntity.created(new URI("/api/store-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /store-infos} : Updates an existing storeInfo.
     *
     * @param storeInfo the storeInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storeInfo,
     * or with status {@code 400 (Bad Request)} if the storeInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the storeInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/store-infos")
    public ResponseEntity<StoreInfo> updateStoreInfo(@Valid @RequestBody StoreInfo storeInfo) throws URISyntaxException {
        log.debug("REST request to update StoreInfo : {}", storeInfo);
        if (storeInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        StoreInfo result = storeInfoService.save(storeInfo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, storeInfo.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /store-infos} : get all the storeInfos.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of storeInfos in body.
     */
    @GetMapping("/store-infos")
    public ResponseEntity<List<StoreInfo>> getAllStoreInfos(Pageable pageable) {
        log.debug("REST request to get a page of StoreInfos");
        Page<StoreInfo> page = storeInfoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /store-infos/:id} : get the "id" storeInfo.
     *
     * @param id the id of the storeInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the storeInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/store-infos/{id}")
    public ResponseEntity<StoreInfo> getStoreInfo(@PathVariable UUID id) {
        log.debug("REST request to get StoreInfo : {}", id);
        Optional<StoreInfo> storeInfo = storeInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(storeInfo);
    }

    /**
     * {@code DELETE  /store-infos/:id} : delete the "id" storeInfo.
     *
     * @param id the id of the storeInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/store-infos/{id}")
    public ResponseEntity<Void> deleteStoreInfo(@PathVariable UUID id) {
        log.debug("REST request to delete StoreInfo : {}", id);
        storeInfoService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
