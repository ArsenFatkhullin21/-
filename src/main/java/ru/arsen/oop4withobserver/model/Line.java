package ru.arsen.oop4withobserver.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Line extends Shape {

    private double x1, y1, x2, y2;

    public Line(double x, double y, double width, double height, Color color, int strokeWeight) {
        super(x, y, width, height, color, strokeWeight);
        updatePoints();
    }

    private void updatePoints() {
        this.x1 = x;
        this.y1 = y;
        this.x2 = x + width;
        this.y2 = y + height;
        notifyObservers();
    }

    @Override
    public void setHeight(double height) {
        super.setHeight(height);
        updatePoints();
    }

    @Override
    public void setWidth(double width) {
        super.setWidth(width);
        updatePoints();
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        updatePoints();
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        updatePoints();
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(color);

        gc.setLineWidth(strokeWeight);
        gc.strokeLine(x1, y1, x2, y2);

        if (selected) {
            super.drawResizeHandles(gc);
        }
    }

    @Override
    public boolean contains(double px, double py) {
        // Проверим, находится ли точка на линии (в пределах ограничений от x до x2 и от y до y2)
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);
        double minY = Math.min(y1, y2);
        double maxY = Math.max(y1, y2);

        if (px < minX || px > maxX || py < minY || py > maxY) {
            return false;  // Точка вне границ линии
        }

        // Используем формулу для расстояния от точки до прямой
        double distance = Math.abs((y2 - y1) * px - (x2 - x1) * py + x2 * y1 - y2 * x1) /
                Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
        return distance < 5;  // Если расстояние от точки до линии меньше 2 пикселей, точка считается на линии
    }


}
