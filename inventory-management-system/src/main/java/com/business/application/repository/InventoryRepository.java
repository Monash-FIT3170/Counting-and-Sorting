package com.business.application.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.business.application.domain.Store;

@Repository
public interface InventoryRepository extends JpaRepository<com.business.application.domain.Inventory, Long> {
    // Basic CRUD operations are automatically provided by JpaRepository
    
    
    // You can add custom query methods here if needed, for example:
    List<Inventory> findByStore(Store store);
        
    

    
}
