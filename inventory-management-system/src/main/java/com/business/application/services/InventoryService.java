package com.business.application.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.business.application.domain.Inventory;
import com.business.application.domain.Store;
import com.business.application.domain.WebScrapedProduct;
import com.business.application.repository.InventoryRepository;
import com.business.application.repository.ProductRepository;
import com.business.application.repository.StoreRepository;
import com.business.application.repository.WebScrapedProductRepository;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private WebScrapedProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;
    
    public InventoryService(InventoryRepository inventoryRepository, WebScrapedProductRepository productRepository, StoreRepository storeRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional
    public Inventory createInventory(Store store) {
        Inventory inventory = new Inventory();
        inventory.setStore(store);
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory getOrCreateInventory(int storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new RuntimeException("Store not found"));
        
        if (store.getInventory() == null) {
            Inventory newInventory = new Inventory();
            newInventory.setStore(store);
            newInventory = inventoryRepository.save(newInventory);
            store.setInventory(newInventory);
            storeRepository.save(store);
            return newInventory;
        }
        
        return store.getInventory();
    }

    //Add method to get all products in inventory
    public List<WebScrapedProduct> getProductsInInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
            .orElseThrow(() -> new RuntimeException("Inventory not found"));
        return inventory.getWebScrapedProducts();
    }

    public void addProductToInventory(Long inventoryId, Long productId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
            .orElseThrow(() -> new RuntimeException("Inventory not found"));
        WebScrapedProduct product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        inventory.getWebScrapedProducts().add(product);
        inventoryRepository.save(inventory);
    }

    public void removeProductFromInventory(Long inventoryId, Long productId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
            .orElseThrow(() -> new RuntimeException("Inventory not found"));
        WebScrapedProduct product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        inventory.getWebScrapedProducts().remove(product);
        inventoryRepository.save(inventory);
    }
}
