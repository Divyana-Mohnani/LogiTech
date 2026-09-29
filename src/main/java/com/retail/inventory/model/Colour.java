package com.retail.inventory.model;

import java.io.Serializable;

/**
 * Colour lookup model (e.g. BK -> Black, BL -> Blue).
 */
public class Colour implements Serializable {
    private static final long serialVersionUID = 1L;

    private String colourCode;
    private String colourName;

    public Colour() {
    }

    public Colour(String colourCode, String colourName) {
        this.colourCode = colourCode;
        this.colourName = colourName;
    }

    public String getColourCode() {
        return colourCode;
    }

    public void setColourCode(String colourCode) {
        this.colourCode = colourCode;
    }

    public String getColourName() {
        return colourName;
    }

    public void setColourName(String colourName) {
        this.colourName = colourName;
    }

    @Override
    public String toString() {
        return colourName + " (" + colourCode + ")";
    }
}
