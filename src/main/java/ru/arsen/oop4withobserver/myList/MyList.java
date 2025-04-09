package ru.arsen.oop4withobserver.myList;

import ru.arsen.oop4withobserver.sevenLab.ListObserver;

import java.util.*;
import java.util.function.Consumer;


public class MyList<T> implements Iterable<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size;

    private final List<ListObserver> observers = new ArrayList<>();


    public void addObserver(ListObserver observer) {
        observers.add(observer);
    }


    private void notifyObservers() {
        for (ListObserver observer : observers) {
            observer.update(this);
        }
    }



    public MyList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void add(T value) {
        Node<T> newNode = new Node<>(value);
        if (head == null) {
            head = newNode;
            tail = head;
        } else {
            tail.setNext(newNode);
            tail = newNode;
        }
        size++;
        notifyObservers();
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        Node<T> temp = head;
        for (int i = 0; i < index; i++) {
            temp = temp.getNext();
        }
        return temp.getData();
    }

    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        if (index == 0) {
            head = head.getNext();
            if (head == null) {
                tail = null;
            }
        } else {
            Node<T> temp = head;
            for (int i = 0; i < index - 1; i++) {
                temp = temp.getNext();
            }
            temp.setNext(temp.getNext().getNext());

            if (temp.getNext() == null) {
                tail = temp;
            }
        }
        size--;
        notifyObservers(); // 🔔 уведомление
    }


    public int indexOf(T value) {
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            if (current.getData().equals(value)) {
                return index;
            }
            current = current.getNext();
            index++;
        }
        return -1;
    }

    public void remove(T value) {
        Node<T> current = head;
        Node<T> previous = null;

        while (current != null) {
            if (current.getData().equals(value)) {
                if (previous == null) {
                    head = current.getNext();
                } else {
                    previous.setNext(current.getNext());
                }
                if (current.getNext() == null) {
                    tail = previous;
                }
                size--;
                notifyObservers();
                return;
            }
            previous = current;
            current = current.getNext();
        }
    }

    public void removeAll(Iterable<T> otherList) {
        for (T value : otherList) {
            remove(value);
        }
        notifyObservers();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(T value) {
        Node<T> temp = head;
        while (temp != null) {
            if (temp.getData().equals(value)) {
                return true;
            }
            temp = temp.getNext();
        }
        return false;
    }

    public void removeAll() {
        head = null;
        tail = null;
        size = 0;
        notifyObservers();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> temp = head;
        while (temp != null) {
            sb.append(temp.getData());
            if (temp.getNext() != null) {
                sb.append(", ");
            }
            temp = temp.getNext();
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                T data = current.getData();
                current = current.getNext();
                return data;
            }
        };
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return Iterable.super.spliterator();
    }
}