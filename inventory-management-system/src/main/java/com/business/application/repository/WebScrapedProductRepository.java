package com.business.application.repository;

import com.business.application.domain.WebScrapedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebScrapedProductRepository extends JpaRepository<WebScrapedProduct, Long> {
    
}
