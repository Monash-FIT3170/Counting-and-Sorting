package com.business.application.repository;

import com.business.application.domain.Transaction;
import com.business.application.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    // Find all transactions for a specific store by storeId
    List<Transaction> findByStoreId(Integer storeId);

    // Find all transactions by type
    List<Transaction> findByType(TransactionType type);

    // Find all transactions by date
    List<Transaction> findByDate(LocalDate date);

    // Find all transactions for a specific store and type
    List<Transaction> findByStoreIdAndType(Integer storeId, TransactionType type);

    // Find all transactions for a specific store and date
    List<Transaction> findByStoreIdAndDate(Integer storeId, LocalDate date);

    // Find all transactions by storeId, type, and date
    List<Transaction> findByStoreIdAndTypeAndDate(Integer storeId, TransactionType type, LocalDate date);

    // Optional: Find transactions by a range of dates
    List<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT * FROM transactions WHERE storeid = :storeId", nativeQuery = true)
    List<Transaction> findAllTransactionsByStoreId(@Param("storeId") Integer storeId);
}

