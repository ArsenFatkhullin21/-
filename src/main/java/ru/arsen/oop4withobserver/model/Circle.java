package ru.arsen.oop4withobserver.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Circle extends Ellipse {
    public Circle(double x, double y,double size, Color color, int strokeWeight) {
        super(x, y, size, size, color, strokeWeight);
    }


}
