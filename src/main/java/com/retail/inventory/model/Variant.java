package com.retail.inventory.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Variant model representing a specific SKU under a Design.
 * Identified by structured code: GENDER / SIZE / LENGTH / COLOUR
 * e.g., M/L/B/BK for black trunks for males in size large
 */
public class Variant implements Serializable {
    private static final long serialVersionUID = 1L;

    private String variantCode;
    private String designNo;
    private String gender;     // M = Gents, W = Ladies, B = Boys, G = Girls
    private String size;       // S, M, L, XL, XXL, FS, 6, 7, 8, etc., or NA
    private String length;     // T, SH, CP, FP, FS, HS, SL, or NA
    private String colourCode; // BK, BL, RD, etc.
    private Timestamp createdAt;

    // Optional joined fields for display convenience
    private Design design;
    private Colour colour;

    public Variant() {
    }

    public Variant(String variantCode, String designNo, String gender, String size, String length, String colourCode) {
        this.variantCode = variantCode;
        this.designNo = designNo;
        this.gender = gender;
        this.size = size;
        this.length = length;
        this.colourCode = colourCode;
    }

    public String getVariantCode() {
        return variantCode;
    }

    public void setVariantCode(String variantCode) {
        this.variantCode = variantCode;
    }

    public String getDesignNo() {
        return designNo;
    }

    public void setDesignNo(String designNo) {
        this.designNo = designNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getColourCode() {
        return colourCode;
    }

    public void setColourCode(String colourCode) {
        this.colourCode = colourCode;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Design getDesign() {
        return design;
    }

    public void setDesign(Design design) {
        this.design = design;
    }

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    /**
     * Readable gender label
     */
    public String getGenderLabel() {
        if ("M".equalsIgnoreCase(gender)) return "Gents";
        if ("W".equalsIgnoreCase(gender)) return "Ladies";
        if ("B".equalsIgnoreCase(gender)) return "Boys";
        if ("G".equalsIgnoreCase(gender)) return "Girls";
        return gender;
    }

    /**
     * Readable length label
     */
    public String getLengthLabel() {
        if (length == null || "NA".equalsIgnoreCase(length)) return "N/A";
        switch (length.toUpperCase()) {
            case "B":
            case "T": return "Trunk";
            case "SH": return "Shorts";
            case "CP": return "Capri";
            case "FP": return "Full pant";
            case "FS": return "Full sleeve";
            case "HS": return "Half sleeve";
            case "SL": return "Sleeveless";
            default: return length;
        }
    }

    @Override
    public String toString() {
        return variantCode;
    }
}
