package ru.arsen.oop4withobserver.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.arsen.oop4withobserver.Paint;
import ru.arsen.oop4withobserver.myList.MyList;
import ru.arsen.oop4withobserver.observer.ShapeObserver;
import ru.arsen.oop4withobserver.sevenLab.ArrowObserver;

import java.util.HashMap;
import java.util.List;

// Стрелка, соединяющая два объекта (A и B)
public class ArrowShape extends Shape implements ShapeObserver {

    private Shape objectA;
    private Shape objectB;
    private double startX, startY, endX, endY;





    public Shape getObjectA() {
        return objectA;
    }

    public void setObjectA(Shape objectA) {
        this.objectA = objectA;
    }

    public Shape getObjectB() {
        return objectB;
    }

    public void setObjectB(Shape objectB) {
        this.objectB = objectB;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }



//
//    public ArrowShape(HashMap<Shape, List<Double>> map) {
//
//        Shape objA = (Shape) map.get(objectA);
//        Shape objB = (Shape) map.get(objectB);
//
//        this.objectA = objA;
//        this.objectB = objB;
//    }

    public ArrowShape(Shape objectA, Shape objectB) {
        this.objectA = objectA;
        this.objectB = objectB;

        // Начальная позиция стрелки
        this.startX = objectA.getX() + objectA.getWidth() / 2;
        this.startY = objectA.getY() + objectA.getHeight() / 2;
        this.endX = objectB.getX() + objectB.getWidth() / 2;
        this.endY = objectB.getY() + objectB.getHeight() / 2;

        // Подписываемся на изменения объектов A и B
        objectA.addObserver(this);
        objectB.addObserver(this);
    }

//    public ArrowShape(Paint paint, Shape objectA, Shape objectB) {
//        this.paint = paint;
//        this.objectA = objectA;
//        this.objectB = objectB;
//
//        this.startX = objectA.getX() + objectA.getWidth() / 2;
//        this.startY = objectA.getY() + objectA.getHeight() / 2;
//        this.endX = objectB.getX() + objectB.getWidth() / 2;
//        this.endY = objectB.getY() + objectB.getHeight() / 2;
//
//        // Подписываемся на изменения объектов A и B
//        objectA.addObserver(this);
//        objectB.addObserver(this);
//    }

    // Метод для рисования стрелки
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(startX, startY, endX, endY);

        // Рисуем стрелку на конце
        double arrowHeadSize = 10;
        double angle = Math.atan2(endY - startY, endX - startX);
        gc.strokeLine(endX, endY, endX - arrowHeadSize * Math.cos(angle - Math.PI / 6), endY - arrowHeadSize * Math.sin(angle - Math.PI / 6));
        gc.strokeLine(endX, endY, endX - arrowHeadSize * Math.cos(angle + Math.PI / 6), endY - arrowHeadSize * Math.sin(angle + Math.PI / 6));

        if (selected) {
            // Задаем рамку для выделения, как у линии
            this.x = Math.min(startX, endX);
            this.y = Math.min(startY, endY);
            this.width = Math.abs(endX - startX);
            this.height = Math.abs(endY - startY);

            super.drawResizeHandles(gc);
        }
    }

    @Override
    public boolean contains(double px, double py) {
        double dx = endX - startX;
        double dy = endY - startY;

        // Длина вектора
        double lengthSquared = dx * dx + dy * dy;

        if (lengthSquared == 0) {
            // Старт и конец совпадают (плохой кейс, но на всякий)
            return false;
        }

        // Проекция точки (px, py) на отрезок
        double t = ((px - startX) * dx + (py - startY) * dy) / lengthSquared;

        // Ограничим проекцию отрезком [0,1]
        t = Math.max(0, Math.min(1, t));

        // Точка на отрезке, ближайшая к (px, py)
        double projX = startX + t * dx;
        double projY = startY + t * dy;

        // Расстояние от этой точки до (px, py)
        double distance = Math.hypot(projX - px, projY - py);

        return distance <= 5; // допустимая погрешность в пикселях
    }

    // Метод обновления позиции стрелки при изменении позиции объектов A или B


    @Override
    public void onShapeChanged(Shape shape) {
        if (shape == objectA) {
            this.startX = objectA.getX() + objectA.getWidth() / 2;
            this.startY = objectA.getY() + objectA.getHeight() / 2;
        } else if (shape == objectB) {
            this.endX = objectB.getX() + objectB.getWidth() / 2;
            this.endY = objectB.getY() + objectB.getHeight() / 2;
        }
    }



    @Override
    public void move(double dx, double dy) {

    }

    @Override
    public void resizeFromHandle(int handleIndex, double dx, double dy) {

    }

    //     Удаление наблюдателя при удалении стрелки
    public void removeObservers() {
        objectA.removeObserver(this);
        objectB.removeObserver(this);
    }
}