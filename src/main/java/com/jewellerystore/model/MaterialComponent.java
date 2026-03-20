package com.jewellerystore.model;

import java.io.Serial;
import java.io.Serializable;

public class MaterialComponent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private double quantity;
    private double quality;

    public MaterialComponent(String name, String description, double quantity, double quality) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.quality = quality;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getQuality() {
        return quality;
    }

    @Override
    public String toString() {
        return name + " - quantity: " + quantity + ", quality: " + quality;
    }
}
