package ru.arsen.oop4withobserver.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ellipse extends Shape {
    private double radiusX, radiusY;

    public Ellipse(double x, double y, double width, double height, Color color, int strokeWeight) {
        super(x, y, width, height, color, strokeWeight);
        updateRadii();
    }

    private void updateRadii() {
        this.radiusX = width / 2;
        this.radiusY = height / 2;
        notifyObservers();
    }

    @Override
    public void setWidth(double width) {
        super.setWidth(width);
        updateRadii();
    }

    @Override
    public void setHeight(double height) {
        super.setHeight(height);
        updateRadii();
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillOval(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(strokeWeight);
        gc.strokeOval(x, y, width, height);

        if (selected) {
            super.drawResizeHandles(gc);
        }
    }

    @Override
    public boolean contains(double x, double y) {
        double centerX = this.x + width / 2;
        double centerY = this.y + height / 2;
        double dx = x - centerX;
        double dy = y - centerY;
        return (dx * dx) / (radiusX * radiusX) + (dy * dy) / (radiusY * radiusY) <= 1;
    }
}