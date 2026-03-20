package com.jewellerystore.model;

import com.jewellerystore.datastructures.CustomLinkedList;
import com.jewellerystore.enums.DisplayCaseType;
import com.jewellerystore.enums.LightingType;

import java.io.Serial;
import java.io.Serializable;

public class DisplayCase implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String caseId;
    private DisplayCaseType type;
    private LightingType lighting;
    private final CustomLinkedList<DisplayTray> trays;

    public DisplayCase(String caseId, DisplayCaseType type, LightingType lighting) {
        this.caseId = caseId;
        this.type = type;
        this.lighting = lighting;
        this.trays = new CustomLinkedList<>();
    }

    public String getIdentifier() {
        return caseId;
    }

    public DisplayCaseType getType() {
        return type;
    }

    public LightingType getLighting() {
        return lighting;
    }

    public CustomLinkedList<DisplayTray> getTrays() {
        return trays;
    }

    public void addTray(DisplayTray tray) {
        trays.add(tray);
    }

    @Override
    public String toString() {
        return caseId + " - " + type + " - " + lighting;
    }
}
