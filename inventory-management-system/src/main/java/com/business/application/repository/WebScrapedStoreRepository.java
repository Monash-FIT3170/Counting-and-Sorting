package com.business.application.repository;

import com.business.application.domain.WebScrapedStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebScrapedStoreRepository extends JpaRepository<WebScrapedStore, Long> {

}