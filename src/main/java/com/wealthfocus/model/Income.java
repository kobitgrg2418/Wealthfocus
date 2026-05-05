package com.wealthfocus.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Income {
    private String id;
    private String userId;
    private BigDecimal amount;
    private String source;
    private LocalDate date;

    public Income() {}

    public Income(String id, String userId, BigDecimal amount, String source, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.source = source;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
