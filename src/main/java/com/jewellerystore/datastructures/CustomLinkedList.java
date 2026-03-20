package com.jewellerystore.datastructures;

import java.io.Serializable;

/**
 * Student-friendly singly linked list used as the main storage structure.
 */
public class CustomLinkedList<T> implements Serializable {
    public interface Matcher<T> {
        boolean matches(T value);
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public void add(T value) {
        addLast(value);
    }

    public void addFirst(T value) {
        Node<T> newNode = new Node<>(value);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.setNext(head);
            head = newNode;
        }
        size++;
    }

    public void addLast(T value) {
        Node<T> newNode = new Node<>(value);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = newNode;
        }
        size++;
    }

    public T get(int index) {
        validateIndex(index);
        Node<T> current = head;
        int currentIndex = 0;
        while (currentIndex < index) {
            current = current.getNext();
            currentIndex++;
        }
        return current.getData();
    }

    public T getFirst() {
        if (isEmpty()) {
            return null;
        }
        return head.getData();
    }

    public T removeAt(int index) {
        validateIndex(index);
        if (index == 0) {
            T removed = head.getData();
            head = head.getNext();
            size--;
            if (size == 0) {
                tail = null;
            }
            return removed;
        }

        Node<T> previous = getNode(index - 1);
        Node<T> current = previous.getNext();
        previous.setNext(current.getNext());
        if (current == tail) {
            tail = previous;
        }
        size--;
        return current.getData();
    }

    public boolean remove(T value) {
        if (isEmpty()) {
            return false;
        }

        if (value == null) {
            if (head.getData() == null) {
                head = head.getNext();
                size--;
                if (size == 0) {
                    tail = null;
                }
                return true;
            }
        } else if (value.equals(head.getData())) {
            head = head.getNext();
            size--;
            if (size == 0) {
                tail = null;
            }
            return true;
        }

        Node<T> previous = head;
        Node<T> current = head.getNext();
        while (current != null) {
            if ((value == null && current.getData() == null)
                    || (value != null && value.equals(current.getData()))) {
                previous.setNext(current.getNext());
                if (current == tail) {
                    tail = previous;
                }
                size--;
                return true;
            }
            previous = current;
            current = current.getNext();
        }

        return false;
    }

    public boolean removeFirstMatch(Matcher<T> matcher) {
        if (isEmpty()) {
            return false;
        }

        if (matcher.matches(head.getData())) {
            head = head.getNext();
            size--;
            if (size == 0) {
                tail = null;
            }
            return true;
        }

        Node<T> previous = head;
        Node<T> current = head.getNext();
        while (current != null) {
            if (matcher.matches(current.getData())) {
                previous.setNext(current.getNext());
                if (current == tail) {
                    tail = previous;
                }
                size--;
                return true;
            }
            previous = current;
            current = current.getNext();
        }
        return false;
    }

    public T findFirst(Matcher<T> matcher) {
        Node<T> current = head;
        while (current != null) {
            if (matcher.matches(current.getData())) {
                return current.getData();
            }
            current = current.getNext();
        }
        return null;
    }

    public boolean contains(Matcher<T> matcher) {
        return findFirst(matcher) != null;
    }

    public int indexOf(T value) {
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            if (value == null && current.getData() == null) {
                return index;
            }
            if (value != null && value.equals(current.getData())) {
                return index;
            }
            current = current.getNext();
            index++;
        }
        return -1;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    private Node<T> getNode(int index) {
        validateIndex(index);
        Node<T> current = head;
        int currentIndex = 0;
        while (currentIndex < index) {
            current = current.getNext();
            currentIndex++;
        }
        return current;
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
    }
}
