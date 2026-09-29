package com.retail.inventory.model;

import java.io.Serializable;

/**
 * Stock Condition model (Normal, Defective, Old, Dead stock).
 */
public class StockCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    private int conditionId;
    private String conditionName;

    public StockCondition() {
    }

    public StockCondition(int conditionId, String conditionName) {
        this.conditionId = conditionId;
        this.conditionName = conditionName;
    }

    public int getConditionId() {
        return conditionId;
    }

    public void setConditionId(int conditionId) {
        this.conditionId = conditionId;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    @Override
    public String toString() {
        return conditionName;
    }
}
