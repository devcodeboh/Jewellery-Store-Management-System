package com.jewellerystore.model;

import com.jewellerystore.datastructures.CustomLinkedList;

import java.io.Serial;
import java.io.Serializable;

public class JewelleryStore implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final CustomLinkedList<DisplayCase> cases;

    public JewelleryStore() {
        this.cases = new CustomLinkedList<>();
    }

    public CustomLinkedList<DisplayCase> getDisplayCases() {
        return cases;
    }

    public void clear() {
        cases.clear();
    }
}
