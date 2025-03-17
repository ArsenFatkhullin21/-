package ru.arsen.oop4withobserver.myList;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class MyList<T> implements Iterable<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size;

    // Конструктор по умолчанию
    public MyList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // Добавление элемента в конец списка
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
    }

    // Получение элемента по индексу
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

    // Удаление элемента по индексу
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        if (index == 0) { // Удаление первого элемента
            head = head.getNext();
            if (head == null) { // Если список стал пустым
                tail = null;
            }
        } else {
            Node<T> temp = head;
            for (int i = 0; i < index - 1; i++) {
                temp = temp.getNext();
            }
            temp.setNext(temp.getNext().getNext());

            if (temp.getNext() == null) { // Если удаляем последний элемент
                tail = temp;
            }
        }

        size--;
    }

    // Удаление элемента по значению
    public void remove(T value) {
        Node<T> current = head;
        Node<T> previous = null;

        while (current != null) {
            if (current.getData().equals(value)) {
                if (previous == null) {
                    head = current.getNext();  // Если удаляем первый элемент
                } else {
                    previous.setNext(current.getNext());  // Удаляем из середины или конца
                }
                if (current.getNext() == null) { // Если удаляем последний элемент
                    tail = previous;
                }
                size--;
                return;
            }
            previous = current;
            current = current.getNext();
        }
    }

    // Удаление всех элементов из списка
    public void removeAll(Iterable<T> otherList) {
        for (T value : otherList) {
            remove(value);  // Для каждого элемента в otherList удаляем его из текущего списка
        }
    }

    // Возвращает размер списка
    public int size() {
        return size;
    }

    // Проверка на пустоту списка
    public boolean isEmpty() {
        return size == 0;
    }

    // Проверка, содержится ли значение в списке
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

    // Удаление всех элементов из списка
    public void removeAll() {
        head = null;
        tail = null;
        size = 0;
    }

    // Печать списка
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

    // Итератор для обхода списка
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;  // Начинаем с головы списка

            @Override
            public boolean hasNext() {
                return current != null;  // Есть следующий элемент?
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();  // Если нет следующего элемента, выбрасываем исключение
                }
                T data = current.getData();  // Берем данные текущего элемента
                current = current.getNext();  // Переходим к следующему элементу
                return data;
            }
        };
    }

    // Метод forEach, вызываемый для каждого элемента
    @Override
    public void forEach(Consumer<? super T> action) {
        Iterable.super.forEach(action);
    }

    // Метод spliterator
    @Override
    public Spliterator<T> spliterator() {
        return Iterable.super.spliterator();
    }
}