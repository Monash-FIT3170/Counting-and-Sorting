package com.business.application.services;

import com.business.application.domain.WebScrapedProduct;
import com.business.application.repository.WebScrapedProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebScrapedProductService {

    @Autowired
    private WebScrapedProductRepository webscrapedProductRepository;

    public List<WebScrapedProduct> getAllWebscrapedProducts() {
        return webscrapedProductRepository.findAll();
    }

    public WebScrapedProduct getWebscrapedProductById(Long id) {
        return webscrapedProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Webscraped product not found"));
    }
}
