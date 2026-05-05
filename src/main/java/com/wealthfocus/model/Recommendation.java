package com.wealthfocus.model;

import java.math.BigDecimal;

public class Recommendation {
    private String title;
    private String description;
    private String riskLevel;
    private BigDecimal suggestedAmount;
    private String reasoning;

    public Recommendation(String title, String description, String riskLevel,
                          BigDecimal suggestedAmount, String reasoning) {
        this.title = title;
        this.description = description;
        this.riskLevel = riskLevel;
        this.suggestedAmount = suggestedAmount;
        this.reasoning = reasoning;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getRiskLevel() { return riskLevel; }
    public BigDecimal getSuggestedAmount() { return suggestedAmount; }
    public String getReasoning() { return reasoning; }
}
