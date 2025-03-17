package ru.arsen.oop4withobserver.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;



public class Rectangle extends Shape {




    public Rectangle(double x, double y, double width, double height, Color color, int strokeWeight) {
        super(x, y, width, height, color, strokeWeight);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(strokeWeight);
        gc.strokeRect(x, y, width, height);

        if (selected) {
            super.drawResizeHandles(gc);
        }
    }


    @Override
    public boolean contains(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean isOnResizeHandle(double mouseX, double mouseY) {
        double handleX = x + width - HANDLE_SIZE / 2;
        double handleY = y + height - HANDLE_SIZE / 2;
        return mouseX >= handleX && mouseX <= handleX + HANDLE_SIZE &&
                mouseY >= handleY && mouseY <= handleY + HANDLE_SIZE;
    }







}
