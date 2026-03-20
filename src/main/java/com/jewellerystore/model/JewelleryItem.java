package com.jewellerystore.model;

import com.jewellerystore.datastructures.CustomLinkedList;

import java.io.Serial;
import java.io.Serializable;

public class JewelleryItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String description;
    private String type;
    private String targetGender;
    private String imageUrl;
    private double retailPrice;
    private final CustomLinkedList<MaterialComponent> materials;

    public JewelleryItem(String description, String type, String targetGender, String imageUrl, double retailPrice) {
        this.description = description;
        this.type = type;
        this.targetGender = targetGender;
        this.imageUrl = imageUrl;
        this.retailPrice = retailPrice;
        this.materials = new CustomLinkedList<>();
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getTargetGender() {
        return targetGender;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public CustomLinkedList<MaterialComponent> getMaterials() {
        return materials;
    }

    public void addMaterial(MaterialComponent material) {
        materials.add(material);
    }

    public boolean hasMaterials() {
        return !materials.isEmpty();
    }

    public int getMaterialCount() {
        return materials.size();
    }

    @Override
    public String toString() {
        return description + " [" + type + "] - " + retailPrice;
    }
}
