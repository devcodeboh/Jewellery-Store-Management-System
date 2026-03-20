package com.jewellerystore.ui;

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
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Optional;

public class MainController {
    private final JewelleryStoreService service;
    private final StorePersistenceManager persistenceManager;

    private final ListView<DisplayCase> caseListView;
    private final ListView<DisplayTray> trayListView;
    private final ListView<JewelleryItem> itemListView;
    private final ListView<SearchResult> searchResultsListView;
    private final TextArea detailsArea;
    private final TextField searchField;

    public MainController() {
        this.service = new JewelleryStoreService();
        this.persistenceManager = new StorePersistenceManager();
        this.caseListView = new ListView<>();
        this.trayListView = new ListView<>();
        this.itemListView = new ListView<>();
        this.searchResultsListView = new ListView<>();
        this.detailsArea = new TextArea();
        this.searchField = new TextField();
    }

    public Parent createRoot() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        VBox topBox = new VBox(10);
        topBox.getChildren().add(createToolbar());
        topBox.getChildren().add(createSearchBar());

        root.setTop(topBox);
        root.setCenter(createBrowserArea());
        root.setBottom(createDetailsPanel());

        detailsArea.setEditable(false);
        detailsArea.setPrefRowCount(9);
        refreshAllViews();
        wireSelectionListeners();
        return root;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(8);
        toolbar.setPadding(new Insets(0, 0, 5, 0));

        Button addCaseButton = new Button("Add Case");
        addCaseButton.setOnAction(event -> showAddCaseDialog());

        Button addTrayButton = new Button("Add Tray");
        addTrayButton.setOnAction(event -> showAddTrayDialog());

        Button addItemButton = new Button("Add Item");
        addItemButton.setOnAction(event -> showAddItemDialog(false));

        Button smartAddButton = new Button("Smart Add Item");
        smartAddButton.setOnAction(event -> showAddItemDialog(true));

        Button addMaterialButton = new Button("Add Material");
        addMaterialButton.setOnAction(event -> showAddMaterialDialog());

        Button removeItemButton = new Button("Remove Item");
        removeItemButton.setOnAction(event -> removeSelectedItem());

        Button valueButton = new Button("Value Stock");
        valueButton.setOnAction(event -> showValuationSummary());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> saveStoreToFile());

        Button loadButton = new Button("Load");
        loadButton.setOnAction(event -> loadStoreFromFile());

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> resetStore());

        toolbar.getChildren().add(addCaseButton);
        toolbar.getChildren().add(addTrayButton);
        toolbar.getChildren().add(addItemButton);
        toolbar.getChildren().add(smartAddButton);
        toolbar.getChildren().add(addMaterialButton);
        toolbar.getChildren().add(removeItemButton);
        toolbar.getChildren().add(valueButton);
        toolbar.getChildren().add(saveButton);
        toolbar.getChildren().add(loadButton);
        toolbar.getChildren().add(resetButton);
        return toolbar;
    }

    private HBox createSearchBar() {
        HBox searchBox = new HBox(8);

        Label searchLabel = new Label("Search:");
        searchField.setPromptText("Enter text");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchButton = new Button("Find");
        searchButton.setOnAction(event -> runSearch());

        searchBox.getChildren().add(searchLabel);
        searchBox.getChildren().add(searchField);
        searchBox.getChildren().add(searchButton);
        return searchBox;
    }

    private HBox createBrowserArea() {
        HBox browserArea = new HBox(10);
        browserArea.setPadding(new Insets(10, 0, 10, 0));

        VBox casesBox = createListPanel("Display Cases", caseListView);
        VBox traysBox = createListPanel("Trays", trayListView);
        VBox itemsBox = createListPanel("Items", itemListView);
        VBox searchBox = createListPanel("Search Results", searchResultsListView);

        HBox.setHgrow(casesBox, Priority.ALWAYS);
        HBox.setHgrow(traysBox, Priority.ALWAYS);
        HBox.setHgrow(itemsBox, Priority.ALWAYS);
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        browserArea.getChildren().add(casesBox);
        browserArea.getChildren().add(traysBox);
        browserArea.getChildren().add(itemsBox);
        browserArea.getChildren().add(searchBox);
        return browserArea;
    }

    private VBox createListPanel(String title, ListView<?> listView) {
        VBox panel = new VBox(6);
        Label label = new Label(title);
        VBox.setVgrow(listView, Priority.ALWAYS);
        panel.getChildren().add(label);
        panel.getChildren().add(listView);
        panel.setPrefWidth(200);
        return panel;
    }

    private Region createDetailsPanel() {
        VBox panel = new VBox(6);
        panel.setPadding(new Insets(10, 0, 0, 0));

        Label title = new Label("Selected Item Details");
        VBox.setVgrow(detailsArea, Priority.ALWAYS);
        panel.getChildren().add(title);
        panel.getChildren().add(detailsArea);
        return panel;
    }

    private void wireSelectionListeners() {
        caseListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            refreshTrayList(newValue);
            itemListView.getItems().clear();
            detailsArea.clear();
        });

        trayListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            refreshItemList(newValue);
            detailsArea.clear();
        });

        itemListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                showItemDetails(newValue));

        searchResultsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                focusSearchResult(newValue);
            }
        });
    }

    private void refreshAllViews() {
        caseListView.getItems().clear();
        for (int i = 0; i < service.getStore().getDisplayCases().size(); i++) {
            caseListView.getItems().add(service.getStore().getDisplayCases().get(i));
        }
        trayListView.getItems().clear();
        itemListView.getItems().clear();
        searchResultsListView.getItems().clear();
        detailsArea.clear();
    }

    private void refreshTrayList(DisplayCase displayCase) {
        trayListView.getItems().clear();
        if (displayCase == null) {
            return;
        }
        for (int i = 0; i < displayCase.getTrays().size(); i++) {
            trayListView.getItems().add(displayCase.getTrays().get(i));
        }
    }

    private void refreshItemList(DisplayTray tray) {
        itemListView.getItems().clear();
        if (tray == null) {
            return;
        }
        for (int i = 0; i < tray.getJewelleryItems().size(); i++) {
            itemListView.getItems().add(tray.getJewelleryItems().get(i));
        }
    }

    private void showAddCaseDialog() {
        DialogForm form = new DialogForm("Add Display Case");
        TextField idField = form.addTextField("Identifier");
        ComboBox<DisplayCaseType> typeBox = form.addComboBox("Type");
        typeBox.getItems().add(DisplayCaseType.WALL_MOUNTED);
        typeBox.getItems().add(DisplayCaseType.FREESTANDING);
        typeBox.getSelectionModel().selectFirst();

        ComboBox<LightingType> lightingBox = form.addComboBox("Lighting");
        lightingBox.getItems().add(LightingType.LIT);
        lightingBox.getItems().add(LightingType.UNLIT);
        lightingBox.getSelectionModel().selectFirst();

        form.show(() -> {
            service.addDisplayCase(idField.getText(), typeBox.getValue(), lightingBox.getValue());
            refreshAllViews();
        });
    }

    private void showAddTrayDialog() {
        DisplayCase selectedCase = caseListView.getSelectionModel().getSelectedItem();
        if (selectedCase == null) {
            showError("Select a display case first.");
            return;
        }

        DialogForm form = new DialogForm("Add Display Tray");
        TextField trayIdField = form.addTextField("Tray ID");
        TextField colourField = form.addTextField("Inlay Colour");
        TextField widthField = form.addTextField("Width (cm)");
        TextField depthField = form.addTextField("Depth (cm)");

        form.show(() -> {
            service.addDisplayTray(
                    selectedCase.getIdentifier(),
                    trayIdField.getText(),
                    colourField.getText(),
                    parseDouble(widthField.getText(), "Width"),
                    parseDouble(depthField.getText(), "Depth")
            );
            refreshAllViews();
            caseListView.getSelectionModel().select(selectedCase);
            refreshTrayList(selectedCase);
        });
    }

    private void showAddItemDialog(boolean smartAdd) {
        if (!smartAdd && trayListView.getSelectionModel().getSelectedItem() == null) {
            showError("Select a display tray first.");
            return;
        }

        DialogForm form = new DialogForm(smartAdd ? "Smart Add Jewellery Item" : "Add Jewellery Item");
        TextField descriptionField = form.addTextField("Description");
        TextField typeField = form.addTextField("Type");
        TextField genderField = form.addTextField("Target Gender");
        TextField imageField = form.addTextField("Image URL");
        TextField priceField = form.addTextField("Retail Price");

        form.addSectionLabel("First Material Component");
        TextField materialNameField = form.addTextField("Material Name");
        TextField materialDescriptionField = form.addTextField("Material Description");
        TextField materialQuantityField = form.addTextField("Quantity");
        TextField materialQualityField = form.addTextField("Quality");

        form.show(() -> {
            JewelleryItem item = new JewelleryItem(
                    descriptionField.getText(),
                    typeField.getText(),
                    genderField.getText(),
                    imageField.getText(),
                    parseDouble(priceField.getText(), "Retail price")
            );

            MaterialComponent material = new MaterialComponent(
                    materialNameField.getText(),
                    materialDescriptionField.getText(),
                    parseDouble(materialQuantityField.getText(), "Quantity"),
                    parseDouble(materialQualityField.getText(), "Quality")
            );
            item.addMaterial(material);

            if (smartAdd) {
                SearchResult result = service.smartAddJewelleryItem(item);
                refreshAllViews();
                showInfo("Item placed in Case " + result.getDisplayCaseIdentifier() + ", Tray " + result.getDisplayTrayIdentifier());
            } else {
                DisplayCase displayCase = caseListView.getSelectionModel().getSelectedItem();
                DisplayTray tray = trayListView.getSelectionModel().getSelectedItem();
                service.addJewelleryItem(displayCase.getIdentifier(), tray.getTrayIdentifier(), item);
                refreshAllViews();
                caseListView.getSelectionModel().select(displayCase);
                refreshTrayList(displayCase);
                trayListView.getSelectionModel().select(tray);
                refreshItemList(tray);
            }
        });
    }

    private void showAddMaterialDialog() {
        JewelleryItem selectedItem = itemListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showError("Select a jewellery item first.");
            return;
        }

        DialogForm form = new DialogForm("Add Material Component");
        TextField nameField = form.addTextField("Material Name");
        TextField descriptionField = form.addTextField("Description");
        TextField quantityField = form.addTextField("Quantity");
        TextField qualityField = form.addTextField("Quality");

        form.show(() -> {
            MaterialComponent material = new MaterialComponent(
                    nameField.getText(),
                    descriptionField.getText(),
                    parseDouble(quantityField.getText(), "Quantity"),
                    parseDouble(qualityField.getText(), "Quality")
            );
            service.addMaterialComponent(selectedItem, material);
            showItemDetails(selectedItem);
        });
    }

    private void removeSelectedItem() {
        DisplayCase displayCase = caseListView.getSelectionModel().getSelectedItem();
        DisplayTray tray = trayListView.getSelectionModel().getSelectedItem();
        JewelleryItem item = itemListView.getSelectionModel().getSelectedItem();
        if (displayCase == null || tray == null || item == null) {
            showError("Select an item to remove.");
            return;
        }

        boolean removed = service.removeJewelleryItem(displayCase.getIdentifier(), tray.getTrayIdentifier(), item);
        if (removed) {
            refreshItemList(tray);
            detailsArea.clear();
        }
    }

    private void runSearch() {
        searchResultsListView.getItems().clear();
        CustomLinkedList<SearchResult> results = service.searchItems(searchField.getText());
        for (int i = 0; i < results.size(); i++) {
            searchResultsListView.getItems().add(results.get(i));
        }
    }

    private void focusSearchResult(SearchResult result) {
        DisplayCase displayCase = service.findDisplayCase(result.getDisplayCaseIdentifier());
        if (displayCase == null) {
            return;
        }
        caseListView.getSelectionModel().select(displayCase);
        refreshTrayList(displayCase);

        DisplayTray tray = service.findTrayInCase(result.getDisplayCaseIdentifier(), result.getDisplayTrayIdentifier());
        if (tray == null) {
            return;
        }
        trayListView.getSelectionModel().select(tray);
        refreshItemList(tray);
        itemListView.getSelectionModel().select(result.getItem());
        showItemDetails(result.getItem());
    }

    private void showItemDetails(JewelleryItem item) {
        if (item == null) {
            detailsArea.clear();
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Description: ").append(item.getDescription()).append("\n");
        builder.append("Type: ").append(item.getType()).append("\n");
        builder.append("Target Gender: ").append(item.getTargetGender()).append("\n");
        builder.append("Image URL: ").append(item.getImageUrl()).append("\n");
        builder.append("Retail Price: ").append(formatMoney(item.getRetailPrice())).append("\n");
        builder.append("\nMaterials / Components:\n");

        for (int i = 0; i < item.getMaterials().size(); i++) {
            MaterialComponent material = item.getMaterials().get(i);
            builder.append("- ").append(material.getName()).append(": ")
                    .append(material.getDescription())
                    .append(", quantity=").append(material.getQuantity())
                    .append(", quality=").append(material.getQuality())
                    .append("\n");
        }
        detailsArea.setText(builder.toString());
    }

    private void showValuationSummary() {
        StringBuilder builder = new StringBuilder();
        builder.append("Store total value: ").append(formatMoney(service.calculateStoreValue())).append("\n\n");

        for (int caseIndex = 0; caseIndex < service.getStore().getDisplayCases().size(); caseIndex++) {
            DisplayCase displayCase = service.getStore().getDisplayCases().get(caseIndex);
            builder.append("Case ").append(displayCase.getIdentifier())
                    .append(": ").append(formatMoney(service.calculateCaseValue(displayCase))).append("\n");

            for (int trayIndex = 0; trayIndex < displayCase.getTrays().size(); trayIndex++) {
                DisplayTray tray = displayCase.getTrays().get(trayIndex);
                builder.append("  Tray ").append(tray.getTrayIdentifier())
                        .append(": ").append(formatMoney(service.calculateTrayValue(tray))).append("\n");
            }
            builder.append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Stock Valuation");
        alert.setHeaderText("Current values");
        alert.setContentText(builder.toString());
        alert.showAndWait();
    }

    private void saveStoreToFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Store");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Binary Store File", "*.dat"));
        File file = chooser.showSaveDialog(detailsArea.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            persistenceManager.saveStore(service.getStore(), file.getAbsolutePath());
            showInfo("Store saved successfully.");
        } catch (IOException exception) {
            showError("Could not save store: " + exception.getMessage());
        }
    }

    private void loadStoreFromFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load Store");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Binary Store File", "*.dat"));
        File file = chooser.showOpenDialog(detailsArea.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            service.setStore(persistenceManager.loadStore(file.getAbsolutePath()));
            refreshAllViews();
            showInfo("Store loaded successfully.");
        } catch (IOException | ClassNotFoundException exception) {
            showError("Could not load store: " + exception.getMessage());
        }
    }

    private void resetStore() {
        service.reset();
        refreshAllViews();
    }

    private double parseDouble(String text, String fieldName) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
    }

    private String formatMoney(double value) {
        return new DecimalFormat("0.00").format(value);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Action could not be completed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Action completed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private final class DialogForm {
        private final Alert alert;
        private final GridPane gridPane;
        private int rowIndex;

        private DialogForm(String title) {
            alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle(title);
            alert.setHeaderText(title);
            alert.getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            alert.getButtonTypes().add(javafx.scene.control.ButtonType.CANCEL);

            gridPane = new GridPane();
            gridPane.setHgap(8);
            gridPane.setVgap(8);
            gridPane.setPadding(new Insets(10));
            alert.getDialogPane().setContent(gridPane);
        }

        private TextField addTextField(String labelText) {
            Label label = new Label(labelText + ":");
            TextField field = new TextField();
            gridPane.add(label, 0, rowIndex);
            gridPane.add(field, 1, rowIndex);
            rowIndex++;
            return field;
        }

        private <T> ComboBox<T> addComboBox(String labelText) {
            Label label = new Label(labelText + ":");
            ComboBox<T> comboBox = new ComboBox<>();
            comboBox.setPrefWidth(220);
            gridPane.add(label, 0, rowIndex);
            gridPane.add(comboBox, 1, rowIndex);
            rowIndex++;
            return comboBox;
        }

        private void addSectionLabel(String text) {
            Label label = new Label(text);
            gridPane.add(label, 0, rowIndex, 2, 1);
            rowIndex++;
        }

        private void show(Runnable onConfirm) {
            try {
                Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                    onConfirm.run();
                }
            } catch (IllegalArgumentException exception) {
                showError(exception.getMessage());
            }
        }
    }
}
