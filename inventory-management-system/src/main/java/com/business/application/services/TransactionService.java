package com.business.application.services;

import com.business.application.data.Transaction;
import com.business.application.data.TransactionRepository;
import com.business.application.data.TransactionType;
import com.business.application.data.StoreProfit; // Import the StoreProfit class
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Total expenses per store
    public Map<Integer, BigDecimal> getTotalExpensesPerStore() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.groupingBy(
                        Transaction::getStoreId,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    // Total sales per store
    public Map<Integer, BigDecimal> getTotalSalesPerStore() {
        return transactionRepository.findByType(TransactionType.SALES).stream()
                .collect(Collectors.groupingBy(
                        Transaction::getStoreId,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    // Breakdown of costs for a specific store
    public Map<String, BigDecimal> getCostBreakdownByStore(int storeId) {
        return transactionRepository.findByStoreId(storeId).stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.groupingBy(
                        Transaction::getItem,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    // Revenue breakdown for a specific store
    public Map<String, BigDecimal> getRevenueBreakdownByStore(int storeId) {
        return transactionRepository.findByStoreId(storeId).stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.groupingBy(
                        Transaction::getItem,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    // Account balances of all stores
    public Map<Integer, BigDecimal> getAccountBalances() {
        return transactionRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Transaction::getStoreId,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    // Profits and account balances of each store
    public List<StoreProfit> getProfitsAndBalances() {
        Map<Integer, BigDecimal> accountBalances = getAccountBalances();

        return accountBalances.entrySet().stream()
                .map(entry -> {
                    int storeId = entry.getKey();
                    BigDecimal accountBalance = entry.getValue();
                    BigDecimal initialFunds = transactionRepository.findByStoreId(storeId).stream()
                            .filter(t -> t.getType() == TransactionType.INITIAL_FUNDS)
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal profit = accountBalance.subtract(initialFunds);

                    return new StoreProfit(storeId, accountBalance, profit);
                })
                .collect(Collectors.toList());
    }

    // Total disbursements to head office
    public BigDecimal getTotalDisbursements() {
        return transactionRepository.findByType(TransactionType.DISBURSEMENT).stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .abs(); // Make sure the total is positive
    }

}
