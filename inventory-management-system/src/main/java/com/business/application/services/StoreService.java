package com.business.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.business.application.repository.StoreRepository;
import com.business.application.domain.*;

import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

    
    private final StoreRepository storeRepository;

    @Autowired
    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public Store createStore(Store store) {
        store.setInventory(new Inventory());
        store.getInventory().setStore(store);
        return storeRepository.save(store);
    }

    public List<Store> findAllStores() {
        List<Store> stores = storeRepository.findAll();
        System.out.println("Number of stores found: " + stores.size());
        return storeRepository.findAll();
    }
    

    public Optional<Store> findStoreById(int storeId) {
        return storeRepository.findById(storeId);
    }

    public Store saveStore(Store store) {
        return storeRepository.save(store);
    }

    public void deleteStore(int storeId) {
        storeRepository.deleteById(storeId);
    }
    
    public Store getStoreByManagerId(Long userId) {
        return storeRepository.findByManagerId(userId);
        
    }

    public void ensureInventoryForAllStores() {
        List<Store> stores = storeRepository.findAll();
        for (Store store : stores) {
            if (store.getInventory() == null) {
                store.ensureInventory();
                storeRepository.save(store);
            }
        }
    }
    
}
