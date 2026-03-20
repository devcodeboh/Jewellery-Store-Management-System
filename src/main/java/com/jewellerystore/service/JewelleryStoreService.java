package com.jewellerystore.service;

import com.jewellerystore.datastructures.CustomLinkedList;
import com.jewellerystore.enums.DisplayCaseType;
import com.jewellerystore.enums.LightingType;
import com.jewellerystore.model.DisplayCase;
import com.jewellerystore.model.DisplayTray;
import com.jewellerystore.model.JewelleryItem;
import com.jewellerystore.model.JewelleryStore;
import com.jewellerystore.model.MaterialComponent;

public class JewelleryStoreService {
    private JewelleryStore store;

    public JewelleryStoreService() {
        this.store = new JewelleryStore();
    }

    public JewelleryStoreService(JewelleryStore store) {
        this.store = store;
    }

    public JewelleryStore getStore() {
        return store;
    }

    public void setStore(JewelleryStore store) {
        this.store = store;
    }

    public DisplayCase addDisplayCase(String identifier, DisplayCaseType type, LightingType lighting) {
        validateText(identifier, "Display case identifier is required.");
        if (type == null) {
            throw new IllegalArgumentException("Display case type is required.");
        }
        if (lighting == null) {
            throw new IllegalArgumentException("Lighting type is required.");
        }
        if (findDisplayCase(identifier) != null) {
            throw new IllegalArgumentException("Display case identifier must be unique.");
        }
        DisplayCase displayCase = new DisplayCase(identifier.trim(), type, lighting);
        store.getDisplayCases().add(displayCase);
        return displayCase;
    }

    public DisplayTray addDisplayTray(String caseIdentifier, String trayIdentifier, String inlayColour,
                                      double widthCm, double depthCm) {
        validateText(caseIdentifier, "Display case identifier is required.");
        validateText(trayIdentifier, "Tray identifier is required.");
        checkTrayMeasurements(widthCm, depthCm);

        if (findTrayGlobally(trayIdentifier) != null) {
            throw new IllegalArgumentException("Tray identifier must be globally unique.");
        }

        DisplayCase displayCase = findDisplayCase(caseIdentifier);
        if (displayCase == null) {
            throw new IllegalArgumentException("Display case not found.");
        }

        DisplayTray tray = new DisplayTray(trayIdentifier.trim(), safeText(inlayColour), widthCm, depthCm);
        displayCase.addTray(tray);
        return tray;
    }

    public JewelleryItem addJewelleryItem(String caseIdentifier, String trayIdentifier, JewelleryItem item) {
        validateItem(item);
        DisplayTray tray = findTrayInCase(caseIdentifier, trayIdentifier);
        if (tray == null) {
            throw new IllegalArgumentException("Display tray not found.");
        }
        tray.addJewelleryItem(item);
        return item;
    }

    public MaterialComponent addMaterialComponent(JewelleryItem item, MaterialComponent materialComponent) {
        if (item == null) {
            throw new IllegalArgumentException("Jewellery item is required.");
        }
        if (materialComponent == null) {
            throw new IllegalArgumentException("Material component is required.");
        }
        item.addMaterial(materialComponent);
        return materialComponent;
    }

    public boolean removeJewelleryItem(String caseIdentifier, String trayIdentifier, JewelleryItem item) {
        DisplayTray tray = findTrayInCase(caseIdentifier, trayIdentifier);
        if (tray == null) {
            return false;
        }
        return tray.removeJewelleryItem(item);
    }

    public CustomLinkedList<SearchResult> searchItems(String query) {
        CustomLinkedList<SearchResult> foundItems = new CustomLinkedList<>();
        String text = normalize(query);
        if (text.isEmpty()) {
            return foundItems;
        }

        for (int caseIndex = 0; caseIndex < store.getDisplayCases().size(); caseIndex++) {
            DisplayCase displayCase = store.getDisplayCases().get(caseIndex);
            for (int trayIndex = 0; trayIndex < displayCase.getTrays().size(); trayIndex++) {
                DisplayTray tray = displayCase.getTrays().get(trayIndex);
                for (int itemIndex = 0; itemIndex < tray.getJewelleryItems().size(); itemIndex++) {
                    JewelleryItem item = tray.getJewelleryItems().get(itemIndex);
                    if (itemMatches(item, text)) {
                        foundItems.add(new SearchResult(item, displayCase.getIdentifier(), tray.getTrayIdentifier()));
                    }
                }
            }
        }
        return foundItems;
    }

    public SearchResult smartAddJewelleryItem(JewelleryItem item) {
        validateItem(item);

        SearchResult bestPlace = null;
        double smallestDifference = Double.MAX_VALUE;
        SearchResult firstPlace = null;

        for (int caseIndex = 0; caseIndex < store.getDisplayCases().size(); caseIndex++) {
            DisplayCase displayCase = store.getDisplayCases().get(caseIndex);
            for (int trayIndex = 0; trayIndex < displayCase.getTrays().size(); trayIndex++) {
                DisplayTray tray = displayCase.getTrays().get(trayIndex);

                if (firstPlace == null) {
                    firstPlace = new SearchResult(item, displayCase.getIdentifier(), tray.getTrayIdentifier());
                }

                if (trayContainsType(tray, item.getType())) {
                    double priceGap = calculateClosestPriceDifference(tray, item.getType(), item.getRetailPrice());
                    if (priceGap < smallestDifference) {
                        smallestDifference = priceGap;
                        bestPlace = new SearchResult(item, displayCase.getIdentifier(), tray.getTrayIdentifier());
                    }
                }
            }
        }

        if (bestPlace != null) {
            addJewelleryItem(bestPlace.getDisplayCaseIdentifier(), bestPlace.getDisplayTrayIdentifier(), item);
            return bestPlace;
        }

        if (firstPlace != null) {
            addJewelleryItem(firstPlace.getDisplayCaseIdentifier(), firstPlace.getDisplayTrayIdentifier(), item);
            return firstPlace;
        }

        throw new IllegalStateException("No display trays exist, so smart add cannot place the item.");
    }

    public double calculateTrayValue(DisplayTray tray) {
        double total = 0;
        for (int itemIndex = 0; itemIndex < tray.getJewelleryItems().size(); itemIndex++) {
            total += tray.getJewelleryItems().get(itemIndex).getRetailPrice();
        }
        return total;
    }

    public double calculateCaseValue(DisplayCase displayCase) {
        double total = 0;
        for (int trayIndex = 0; trayIndex < displayCase.getTrays().size(); trayIndex++) {
            total += calculateTrayValue(displayCase.getTrays().get(trayIndex));
        }
        return total;
    }

    public double calculateStoreValue() {
        double total = 0;
        for (int caseIndex = 0; caseIndex < store.getDisplayCases().size(); caseIndex++) {
            total += calculateCaseValue(store.getDisplayCases().get(caseIndex));
        }
        return total;
    }

    public void reset() {
        store.clear();
    }

    public DisplayCase findDisplayCase(String identifier) {
        String normalizedIdentifier = normalize(identifier);
        for (int caseIndex = 0; caseIndex < store.getDisplayCases().size(); caseIndex++) {
            DisplayCase displayCase = store.getDisplayCases().get(caseIndex);
            if (normalize(displayCase.getIdentifier()).equals(normalizedIdentifier)) {
                return displayCase;
            }
        }
        return null;
    }

    public DisplayTray findTrayGlobally(String trayIdentifier) {
        String normalizedTrayIdentifier = normalize(trayIdentifier);
        for (int caseIndex = 0; caseIndex < store.getDisplayCases().size(); caseIndex++) {
            DisplayCase displayCase = store.getDisplayCases().get(caseIndex);
            for (int trayIndex = 0; trayIndex < displayCase.getTrays().size(); trayIndex++) {
                DisplayTray tray = displayCase.getTrays().get(trayIndex);
                if (normalize(tray.getTrayIdentifier()).equals(normalizedTrayIdentifier)) {
                    return tray;
                }
            }
        }
        return null;
    }

    public DisplayTray findTrayInCase(String caseIdentifier, String trayIdentifier) {
        DisplayCase displayCase = findDisplayCase(caseIdentifier);
        if (displayCase == null) {
            return null;
        }
        String normalizedTrayIdentifier = normalize(trayIdentifier);
        for (int trayIndex = 0; trayIndex < displayCase.getTrays().size(); trayIndex++) {
            DisplayTray tray = displayCase.getTrays().get(trayIndex);
            if (normalize(tray.getTrayIdentifier()).equals(normalizedTrayIdentifier)) {
                return tray;
            }
        }
        return null;
    }

    private void validateItem(JewelleryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Jewellery item is required.");
        }
        validateText(item.getDescription(), "Jewellery item description is required.");
        validateText(item.getType(), "Jewellery item type is required.");
        if (!item.hasMaterials()) {
            throw new IllegalArgumentException("Each jewellery item must contain at least one material/component.");
        }
    }

    private boolean itemMatches(JewelleryItem item, String query) {
        if (matchesText(item.getDescription(), query)
                || matchesText(item.getType(), query)
                || matchesText(item.getTargetGender(), query)
                || matchesText(item.getImageUrl(), query)) {
            return true;
        }

        for (int materialIndex = 0; materialIndex < item.getMaterials().size(); materialIndex++) {
            MaterialComponent material = item.getMaterials().get(materialIndex);
            if (matchesText(material.getName(), query) || matchesText(material.getDescription(), query)) {
                return true;
            }
        }
        return false;
    }

    private boolean trayContainsType(DisplayTray tray, String type) {
        String itemType = normalize(type);
        for (int itemIndex = 0; itemIndex < tray.getJewelleryItems().size(); itemIndex++) {
            JewelleryItem currentItem = tray.getJewelleryItems().get(itemIndex);
            if (normalize(currentItem.getType()).equals(itemType)) {
                return true;
            }
        }
        return false;
    }

    private double calculateClosestPriceDifference(DisplayTray tray, String type, double price) {
        String itemType = normalize(type);
        double smallestDifference = Double.MAX_VALUE;
        for (int itemIndex = 0; itemIndex < tray.getJewelleryItems().size(); itemIndex++) {
            JewelleryItem currentItem = tray.getJewelleryItems().get(itemIndex);
            if (normalize(currentItem.getType()).equals(itemType)) {
                double difference = Math.abs(currentItem.getRetailPrice() - price);
                if (difference < smallestDifference) {
                    smallestDifference = difference;
                }
            }
        }
        return smallestDifference;
    }

    private boolean matchesText(String source, String query) {
        return normalize(source).contains(query);
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase();
    }

    private void validateText(String value, String message) {
        if (normalize(value).isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void checkTrayMeasurements(double widthCm, double depthCm) {
        if (widthCm <= 0 || depthCm <= 0) {
            throw new IllegalArgumentException("Tray width and depth must be greater than zero.");
        }
    }

    private String safeText(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}
