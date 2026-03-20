package com.jewellerystore.model;

import com.jewellerystore.datastructures.CustomLinkedList;

import java.io.Serial;
import java.io.Serializable;

public class DisplayTray implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String trayId;
    private String colour;
    private double widthCm;
    private double depthCm;
    private final CustomLinkedList<JewelleryItem> jewelleryItems;

    public DisplayTray(String trayId, String colour, double widthCm, double depthCm) {
        this.trayId = trayId;
        this.colour = colour;
        this.widthCm = widthCm;
        this.depthCm = depthCm;
        this.jewelleryItems = new CustomLinkedList<>();
    }

    public String getTrayIdentifier() {
        return trayId;
    }

    public String getInlayMaterialColour() {
        return colour;
    }

    public double getWidthCm() {
        return widthCm;
    }

    public double getDepthCm() {
        return depthCm;
    }

    public CustomLinkedList<JewelleryItem> getJewelleryItems() {
        return jewelleryItems;
    }

    public void addJewelleryItem(JewelleryItem item) {
        jewelleryItems.add(item);
    }

    public boolean removeJewelleryItem(JewelleryItem item) {
        return jewelleryItems.remove(item);
    }

    @Override
    public String toString() {
        return trayId + " (" + colour + ")";
    }
}
