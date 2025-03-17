package ru.arsen.oop4withobserver.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Triangle extends Shape {
    private double x1, y1;
    private double x2, y2;
    private double x3, y3;

    public Triangle(double x, double y, double width, double height, Color color, int strokeWeight) {
        super(x, y, width, height, color, strokeWeight);
        updatePoints();
    }

    private void updatePoints() {
        this.x1 = x;
        this.y1 = y + getHeight();
        this.x2 = x + getWidth() / 2;
        this.y2 = y;
        this.x3 = x + getWidth();
        this.y3 = y + getHeight();
        notifyObservers();
    }

    @Override
    public void setWidth(double width) {
        super.setWidth(width);
        updatePoints();
    }

    @Override
    public void setHeight(double height) {
        super.setHeight(height);
        updatePoints();
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        updatePoints();
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        updatePoints();
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(strokeWeight);

        double[] xPoints = {x1, x2, x3};
        double[] yPoints = {y1, y2, y3};

        gc.fillPolygon(xPoints, yPoints, 3);
        gc.strokePolygon(xPoints, yPoints, 3);

        if (selected) {
            super.drawResizeHandles(gc);
        }
    }

    private double area(double x1, double y1, double x2, double y2, double x3, double y3) {
        return Math.abs((x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0);
    }

    @Override
    public boolean contains(double x, double y) {
        double epsilon = 0.01; // Допуск на погрешность вычислений
        double area1 = area(x, y, x2, y2, x3, y3);
        double area2 = area(x1, y1, x, y, x3, y3);
        double area3 = area(x1, y1, x2, y2, x, y);
        double totalArea = area(x1, y1, x2, y2, x3, y3);
        return Math.abs(area1 + area2 + area3 - totalArea) < epsilon;
    }
}