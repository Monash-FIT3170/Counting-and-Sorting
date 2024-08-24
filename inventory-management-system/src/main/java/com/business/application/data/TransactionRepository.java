package com.business.application.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    // Find all transactions for a specific store by storeId
    List<Transaction> findByStoreId(Integer storeId);

    // Find all transactions by type
    List<Transaction> findByType(TransactionType type);
}
