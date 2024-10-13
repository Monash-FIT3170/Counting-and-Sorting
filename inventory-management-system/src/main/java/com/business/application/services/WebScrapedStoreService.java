package com.business.application.services;

import com.business.application.domain.WebScrapedStore;
import com.business.application.repository.WebScrapedStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebScrapedStoreService {

    @Autowired
    private WebScrapedStoreRepository webscrapedStoreRepository;

    public List<WebScrapedStore> getAllWebscrapedStores() {
        return webscrapedStoreRepository.findAll();
    }
}