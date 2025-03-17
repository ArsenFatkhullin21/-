package ru.arsen.oop4withobserver.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class HighlightRectangle extends Rectangle {

    public HighlightRectangle(double x, double y, double width, double height, Color color, int strokeWeight) {
        super(x, y, width, height, color, strokeWeight);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setLineDashes(10, 5);


        gc.setStroke(Color.LIGHTBLUE);
        gc.setLineWidth(2);


        gc.strokeRect(x, y, width, height);
        gc.setLineDashes(10,0);


    }
}
