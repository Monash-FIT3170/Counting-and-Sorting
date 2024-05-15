package com.business.application.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.business.application.domain.Product;

public interface ProductRepository
        extends
            JpaRepository<Product, Long>,
            JpaSpecificationExecutor<Product> {

}
