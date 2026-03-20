package com.jewellerystore.service;

import com.jewellerystore.model.JewelleryItem;

public class SearchResult {
    private final JewelleryItem item;
    private final String displayCaseIdentifier;
    private final String displayTrayIdentifier;

    public SearchResult(JewelleryItem item, String displayCaseIdentifier, String displayTrayIdentifier) {
        this.item = item;
        this.displayCaseIdentifier = displayCaseIdentifier;
        this.displayTrayIdentifier = displayTrayIdentifier;
    }

    public JewelleryItem getItem() {
        return item;
    }

    public String getDisplayCaseIdentifier() {
        return displayCaseIdentifier;
    }

    public String getDisplayTrayIdentifier() {
        return displayTrayIdentifier;
    }

    @Override
    public String toString() {
        return item.getDescription() + " -> Case " + displayCaseIdentifier + ", Tray " + displayTrayIdentifier;
    }
}
