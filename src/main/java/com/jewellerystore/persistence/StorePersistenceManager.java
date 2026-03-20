package com.jewellerystore.persistence;

import com.jewellerystore.model.JewelleryStore;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Binary persistence is simple to explain in a demo and keeps the whole custom object graph intact.
 */
public class StorePersistenceManager {

    public void saveStore(JewelleryStore store, String filePath) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            outputStream.writeObject(store);
        }
    }

    public JewelleryStore loadStore(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            return (JewelleryStore) inputStream.readObject();
        }
    }
}
