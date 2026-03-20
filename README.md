# Jewellery Store Management System

JavaFX project for a college assignment about managing jewellery stock in a store.

## Main Idea

The system stores data in this hierarchy:

- JewelleryStore
- DisplayCase
- DisplayTray
- JewelleryItem
- MaterialComponent

The main requirement of the assignment was to use my own data structures.  
Because of that, the project uses a custom linked list instead of Java collection classes for the main application data.

## Custom Data Structure

The following classes were created for storage:

- `Node<T>`
- `CustomLinkedList<T>`

This linked list is used inside the main model classes instead of `ArrayList` or other built-in collections.

## Main Classes

- `JewelleryStore`
- `DisplayCase`
- `DisplayTray`
- `JewelleryItem`
- `MaterialComponent`
- `JewelleryStoreService`
- `StorePersistenceManager`
- `MainApp`
- `MainController`

## Features Implemented

- Add new display case
- Add new tray to a case
- Add jewellery item to a tray
- Add material/component to an item
- View store stock
- Drill down through case, tray and item
- Search by jewellery fields and material fields
- Smart add for jewellery items
- Remove jewellery item
- Calculate tray value
- Calculate case value
- Calculate total store value
- Reset all data
- Save and load from file
- JUnit tests

## Smart Add Rule

The smart add feature works like this:

1. It first looks for a tray that already contains the same jewellery type.
2. If there are several matching trays, it chooses the one with the closest price.
3. If no matching tray exists, it uses the first tray found.
4. If there are no trays at all, it shows an error.

## Save and Load

Saving and loading is done using Java object serialization.  
This was chosen because it is simple and keeps the whole store structure together.

## Short Development Steps

1. Create Maven project
2. Add JavaFX and JUnit
3. Build custom linked list
4. Build model classes
5. Build service layer
6. Add search and smart add
7. Add valuation and reset
8. Add file save/load
9. Build JavaFX interface
10. Add tests

## Run

```bash
mvn test
mvn javafx:run
```

## Notes

- JavaFX is only used for the interface.
- The main store data stays inside custom ADTs.
- The code was kept simple so it can be explained easily in a project demo.
