package com.business.application.services;

import com.business.application.data.Product;
import com.business.application.data.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Page<Product> list(PageRequest pageRequest) {
        return productRepository.findAll(pageRequest);
    }
}
