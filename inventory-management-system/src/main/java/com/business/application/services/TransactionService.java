package com.business.application.services;

import com.business.application.domain.Transaction;
import com.business.application.repository.TransactionRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import com.business.application.domain.TransactionType;

import java.time.LocalDate;


import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Methods for individual stores

    public BigDecimal getTotalExpensesForStore(int storeId) {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getStoreId() == storeId && t.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalSalesForStore(int storeId) {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getStoreId() == storeId && t.getType().equals(TransactionType.SALES))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, BigDecimal> getCostBreakdownForStore(int storeId) {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getStoreId() == storeId && t.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.groupingBy(Transaction::getItem,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
    }

    public Map<String, BigDecimal> getRevenueBreakdownForStore(int storeId) {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getStoreId() == storeId && t.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.groupingBy(Transaction::getItem,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
    }

    public BigDecimal getAccountBalanceForStore(int storeId) {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getStoreId() == storeId)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getProfitForStore(int storeId) {
        BigDecimal totalAmount = transactionRepository.findAll().stream()
                .filter(t -> t.getStoreId() == storeId)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal initialFunds = transactionRepository.findAll().stream()
                .filter(t -> t.getStoreId() == storeId && t.getType().equals(TransactionType.INITIAL_FUNDS))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalAmount.subtract(initialFunds);
    }

    // Methods for all stores (head office)

    public Map<Integer, BigDecimal> getTotalExpensesForAllStores() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.groupingBy(Transaction::getStoreId,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
    }

    public Map<Integer, BigDecimal> getTotalSalesForAllStores() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getType().equals(TransactionType.SALES))
                .collect(Collectors.groupingBy(Transaction::getStoreId,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
    }

    public Map<Integer, BigDecimal> getAccountBalancesForAllStores() {
        return transactionRepository.findAll().stream()
                .collect(Collectors.groupingBy(Transaction::getStoreId,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
    }

    public Map<Integer, BigDecimal> getProfitsForAllStores() {
        Map<Integer, BigDecimal> totalAmounts = transactionRepository.findAll().stream()
                .collect(Collectors.groupingBy(Transaction::getStoreId,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

        Map<Integer, BigDecimal> initialFunds = transactionRepository.findAll().stream()
                .filter(t -> t.getType().equals(TransactionType.INITIAL_FUNDS))
                .collect(Collectors.groupingBy(Transaction::getStoreId,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

        return totalAmounts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().subtract(initialFunds.getOrDefault(entry.getKey(), BigDecimal.ZERO))
                ));
    }



    public void clearPersistenceContext() {
        entityManager.clear(); // Clears the persistence context, causing all managed entities to become detached
    }


    public BigDecimal getTotalDisbursements() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getType().equals(TransactionType.DISBURSEMENT))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .abs();
    }

    // Temporary method to manually fetch and log transactions
    public void logTransactionsUsingNativeQuery(int storeId) {
        clearPersistenceContext();  // Ensure we're starting with a fresh context

        List<Transaction> transactions = transactionRepository.findAllTransactionsByStoreId(storeId);
        
        System.out.println("Total Transactions Retrieved: " + transactions.size());
        
        transactions.forEach(tx -> {
            System.out.println("Transaction ID: " + tx.getTxId() + " | Hash: " + System.identityHashCode(tx));
            System.out.println("Store ID: " + tx.getStoreId());
            System.out.println("Item: " + tx.getItem());
            System.out.println("Amount: " + tx.getAmount());
            System.out.println("Type: " + tx.getType());
            System.out.println("Date: " + tx.getDate());
            System.out.println("To Entity: " + tx.getToEntity());
            System.out.println("=====================================");
        });
    }

    // New method to get all transactions for a specific store
    public List<Transaction> getTransactionsForStore(Integer storeId) {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getStoreId().equals(storeId))
                .collect(Collectors.toList());
    }


}

