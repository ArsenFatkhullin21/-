package ru.arsen.oop4withobserver.observer;

import ru.arsen.oop4withobserver.model.Shape;

public interface ShapeObserver {
    void onShapeChanged(Shape shape);
}