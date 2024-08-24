package com.business.application.data;

import java.math.BigDecimal;

public class StoreProfit {
    private final int storeId;
    private final BigDecimal accountBalance;
    private final BigDecimal profit;

    public StoreProfit(int storeId, BigDecimal accountBalance, BigDecimal profit) {
        this.storeId = storeId;
        this.accountBalance = accountBalance;
        this.profit = profit;
    }

    public int getStoreId() {
        return storeId;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public BigDecimal getProfit() {
        return profit;
    }
}
