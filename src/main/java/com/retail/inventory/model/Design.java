package com.retail.inventory.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Design model representing the parent product design (e.g. D1024).
 * Holds design number, design name, category (Accessories, Swimwear, Footwear), and description.
 */
public class Design implements Serializable {
    private static final long serialVersionUID = 1L;

    private String designNo;
    private String designName;
    private String category;
    private String description;
    private Timestamp createdAt;

    public Design() {
    }

    public Design(String designNo, String designName, String category, String description) {
        this.designNo = designNo;
        this.designName = designName;
        this.category = category;
        this.description = description;
    }

    public String getDesignNo() {
        return designNo;
    }

    public void setDesignNo(String designNo) {
        this.designNo = designNo;
    }

    public String getDesignName() {
        return designName;
    }

    public void setDesignName(String designName) {
        this.designName = designName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Design{" +
                "designNo='" + designNo + '\'' +
                ", designName='" + designName + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
