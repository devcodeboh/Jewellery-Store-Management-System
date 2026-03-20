package com.jewellerystore;

import com.jewellerystore.datastructures.CustomLinkedList;
import com.jewellerystore.enums.DisplayCaseType;
import com.jewellerystore.enums.LightingType;
import com.jewellerystore.model.DisplayCase;
import com.jewellerystore.model.DisplayTray;
import com.jewellerystore.model.JewelleryItem;
import com.jewellerystore.model.MaterialComponent;
import com.jewellerystore.persistence.StorePersistenceManager;
import com.jewellerystore.service.JewelleryStoreService;
import com.jewellerystore.service.SearchResult;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JewelleryStoreServiceTest {

    @Test
    void customLinkedListSupportsAddGetAndSize() {
        CustomLinkedList<String> list = new CustomLinkedList<>();
        list.add("gold");
        list.add("silver");

        assertEquals(2, list.size());
        assertEquals("gold", list.get(0));
        assertEquals("silver", list.get(1));
    }

    @Test
    void customLinkedListSupportsRemoveAt() {
        CustomLinkedList<String> list = new CustomLinkedList<>();
        list.add("gold");
        list.add("silver");
        list.add("diamond");

        String removed = list.removeAt(1);

        assertEquals("silver", removed);
        assertEquals(2, list.size());
        assertEquals("diamond", list.get(1));
    }

    @Test
    void trayIdentifierMustBeGloballyUnique() {
        JewelleryStoreService service = createServiceWithOneCase();
        service.addDisplayTray("Case-1", "A12", "Black", 30, 20);
        service.addDisplayCase("Case-2", DisplayCaseType.FREESTANDING, LightingType.LIT);

        assertThrows(IllegalArgumentException.class,
                () -> service.addDisplayTray("Case-2", "A12", "White", 25, 18));
    }

    @Test
    void addJewelleryItemRequiresAtLeastOneMaterial() {
        JewelleryStoreService service = createServiceWithOneCaseAndTray();
        JewelleryItem item = new JewelleryItem("Plain Ring", "Ring", "Women", "http://image", 200);

        assertThrows(IllegalArgumentException.class,
                () -> service.addJewelleryItem("Case-1", "A12", item));
    }

    @Test
    void searchFindsMatchesInMaterialDescription() {
        JewelleryStoreService service = createServiceWithOneCaseAndTray();
        JewelleryItem item = createItem("Pearl Necklace", "Necklace", 400, "Pearl", "Freshwater pearl");
        service.addJewelleryItem("Case-1", "A12", item);

        CustomLinkedList<SearchResult> results = service.searchItems("freshwater");

        assertEquals(1, results.size());
        assertEquals("A12", results.get(0).getDisplayTrayIdentifier());
    }

    @Test
    void smartAddPrefersTrayWithSameTypeAndClosestPrice() {
        JewelleryStoreService service = new JewelleryStoreService();
        service.addDisplayCase("Case-1", DisplayCaseType.WALL_MOUNTED, LightingType.LIT);
        service.addDisplayCase("Case-2", DisplayCaseType.FREESTANDING, LightingType.UNLIT);
        service.addDisplayTray("Case-1", "A12", "Black", 30, 20);
        service.addDisplayTray("Case-2", "B21", "White", 30, 20);

        service.addJewelleryItem("Case-1", "A12", createItem("Budget Ring", "Ring", 100, "Gold", "Gold band"));
        service.addJewelleryItem("Case-2", "B21", createItem("Luxury Ring", "Ring", 1000, "Gold", "High carat gold"));

        SearchResult result = service.smartAddJewelleryItem(createItem("Mid Ring", "Ring", 120, "Gold", "Polished gold"));

        assertEquals("Case-1", result.getDisplayCaseIdentifier());
        assertEquals("A12", result.getDisplayTrayIdentifier());
    }

    @Test
    void removeJewelleryItemDeletesTheItemFromTray() {
        JewelleryStoreService service = createServiceWithOneCaseAndTray();
        JewelleryItem item = createItem("Silver Watch", "Watch", 550, "Silver", "Steel and silver");
        service.addJewelleryItem("Case-1", "A12", item);

        boolean removed = service.removeJewelleryItem("Case-1", "A12", item);

        assertTrue(removed);
        assertEquals(0, service.findTrayInCase("Case-1", "A12").getJewelleryItems().size());
    }

    @Test
    void valuationCalculatesTrayCaseAndStoreTotals() {
        JewelleryStoreService service = createServiceWithOneCaseAndTray();
        JewelleryItem itemOne = createItem("Gold Ring", "Ring", 200, "Gold", "18k gold");
        JewelleryItem itemTwo = createItem("Silver Necklace", "Necklace", 300, "Silver", "Sterling silver");
        service.addJewelleryItem("Case-1", "A12", itemOne);
        service.addJewelleryItem("Case-1", "A12", itemTwo);

        DisplayCase displayCase = service.findDisplayCase("Case-1");
        DisplayTray tray = service.findTrayInCase("Case-1", "A12");

        assertEquals(500.0, service.calculateTrayValue(tray));
        assertEquals(500.0, service.calculateCaseValue(displayCase));
        assertEquals(500.0, service.calculateStoreValue());
    }

    @Test
    void resetClearsAllStoreData() {
        JewelleryStoreService service = createServiceWithOneCaseAndTray();
        service.reset();

        assertEquals(0, service.getStore().getDisplayCases().size());
    }

    @Test
    void saveAndLoadPreservesStoreData() throws Exception {
        JewelleryStoreService service = createServiceWithOneCaseAndTray();
        service.addJewelleryItem("Case-1", "A12", createItem("Diamond Ring", "Ring", 1200, "Diamond", "Brilliant cut"));

        File file = File.createTempFile("jewellery-store", ".dat");
        file.deleteOnExit();

        StorePersistenceManager persistenceManager = new StorePersistenceManager();
        persistenceManager.saveStore(service.getStore(), file.getAbsolutePath());
        JewelleryStoreService loadedService = new JewelleryStoreService(persistenceManager.loadStore(file.getAbsolutePath()));

        assertNotNull(loadedService.findDisplayCase("Case-1"));
        assertEquals(1, loadedService.findTrayInCase("Case-1", "A12").getJewelleryItems().size());
    }

    @Test
    void searchReturnsEmptyWhenNoMatchExists() {
        JewelleryStoreService service = createServiceWithOneCaseAndTray();

        assertFalse(service.searchItems("emerald").size() > 0);
    }

    private JewelleryStoreService createServiceWithOneCase() {
        JewelleryStoreService service = new JewelleryStoreService();
        service.addDisplayCase("Case-1", DisplayCaseType.WALL_MOUNTED, LightingType.LIT);
        return service;
    }

    private JewelleryStoreService createServiceWithOneCaseAndTray() {
        JewelleryStoreService service = createServiceWithOneCase();
        service.addDisplayTray("Case-1", "A12", "Black", 30, 20);
        return service;
    }

    private JewelleryItem createItem(String description, String type, double price,
                                     String materialName, String materialDescription) {
        JewelleryItem item = new JewelleryItem(description, type, "Unisex", "http://image", price);
        item.addMaterial(new MaterialComponent(materialName, materialDescription, 1.0, 9.0));
        return item;
    }
}
