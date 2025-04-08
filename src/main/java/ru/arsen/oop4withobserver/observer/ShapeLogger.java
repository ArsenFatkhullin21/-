package ru.arsen.oop4withobserver.observer;

import ru.arsen.oop4withobserver.model.Shape;

public class ShapeLogger implements ShapeObserver {

    @Override
    public void onShapeChanged(Shape shape) {
        System.out.println("Фигура изменена: " + shape.getClass().getSimpleName() +
                " | Позиция: (" + shape.getX() + ", " + shape.getY() + ") " +
                "| Размеры: (" + shape.getWidth() + "x" + shape.getHeight() + ")");
    }


}