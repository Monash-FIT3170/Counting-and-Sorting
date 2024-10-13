package com.business.application.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "store_transactions_2")
public class Transaction extends AbstractEntity {

    @Column(name = "tx_id", nullable = false, unique = true)
    private String TxId;

    @Column(name = "store_id", nullable = false)
    private Integer storeId;
    
    @Column(name = "item", nullable = false)
    private String item;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Column(name = "id", nullable = true)
    private Long id;

    @Column(name = "to_entity", nullable = false)
    private String toEntity;

    @Column(name = "date", nullable = false)
    private LocalDate date;



    // Getters and setters

    public String getTxId() {
        return TxId;
    }

    public void setTxId(String TxId) {
        this.TxId = TxId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getToEntity() {
        return toEntity;
    }

    public void setToEntity(String toEntity) {
        this.toEntity = toEntity;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}