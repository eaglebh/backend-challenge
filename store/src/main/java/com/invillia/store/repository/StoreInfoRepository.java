package com.invillia.store.repository;
import com.invillia.store.domain.StoreInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the StoreInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StoreInfoRepository extends JpaRepository<StoreInfo, Long> {

}
