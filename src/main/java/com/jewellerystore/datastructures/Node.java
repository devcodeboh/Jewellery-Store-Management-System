package com.jewellerystore.datastructures;

import java.io.Serializable;

/**
 * Simple node used by the custom linked list.
 */
public class Node<T> implements Serializable {
    private T data;
    private Node<T> next;

    public Node(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }
}
