package com.business.application;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.business.application.repository.StoreRepository;
import com.business.application.services.InventoryService;

@Component
public class InventoryInitialiser implements CommandLineRunner {

    private final StoreRepository storeRepository;
    private final InventoryService inventoryService;

    public InventoryInitialiser(StoreRepository storeRepository, InventoryService inventoryService) {
        this.storeRepository = storeRepository;
        this.inventoryService = inventoryService;
    }

    @Override
    public void run(String... args) {
        storeRepository.findAll().forEach(store -> {
            if (store.getInventory() == null) {
                inventoryService.getOrCreateInventory(store.getStoreId());
            }
        });
    }
}
