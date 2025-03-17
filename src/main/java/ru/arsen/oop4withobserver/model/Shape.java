package ru.arsen.oop4withobserver.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.arsen.oop4withobserver.observer.ShapeObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class Shape {

    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected Color color;
    protected int strokeWeight;

    private final List<ShapeObserver> observers = new ArrayList<>();

    protected boolean selected = false;
    protected static final double HANDLE_SIZE = 8;
    protected int resizingHandle = -1;

    public Shape(){
    }

    public Shape(double x, double y, double width, double height, Color color, int strokeWeight) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.strokeWeight = strokeWeight;
        this.width = width;
        this.height = height;
    }

    public void addObserver(ShapeObserver observer) {
        observers.add(observer);
    }

    protected void notifyObservers() {
        for (ShapeObserver observer : observers) {
            observer.onShapeChanged(this);
        }
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
        notifyObservers();
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
        notifyObservers();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        notifyObservers();
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        notifyObservers();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        notifyObservers();
    }

    public int getStrokeWeight() {
        return strokeWeight;
    }

    public void setStrokeWeight(int strokeWeight) {
        this.strokeWeight = strokeWeight;
        notifyObservers();
    }


    protected void drawResizeHandles(GraphicsContext gc) {
        gc.setFill(Color.BLUE);
        double[][] handles = getResizeHandles();
        for (double[] handle : handles) {
            gc.fillRect(handle[0] - HANDLE_SIZE / 2, handle[1] - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);

        }
        gc.setStroke(Color.BLACK);
        gc.strokeRect(handles[0][0],handles[0][1],width,height);
    }

    protected double[][] getResizeHandles() {
        return new double[][]{
                {x, y},                            // Top-left
                {x + width / 2, y},                // Top-center
                {x + width, y},                    // Top-right
                {x, y + height / 2},               // Middle-left
                {x + width, y + height / 2},       // Middle-right
                {x, y + height},                   // Bottom-left
                {x + width / 2, y + height},       // Bottom-center
                {x + width, y + height}            // Bottom-right
        };
    }

    public int getHandleAt(double mouseX, double mouseY) {
        double[][] handles = getResizeHandles();
        for (int i = 0; i < handles.length; i++) {
            double hx = handles[i][0], hy = handles[i][1];
            if (mouseX >= hx - HANDLE_SIZE / 2 && mouseX <= hx + HANDLE_SIZE / 2 &&
                    mouseY >= hy - HANDLE_SIZE / 2 && mouseY <= hy + HANDLE_SIZE / 2) {
                return i;
            }
        }
        return -1;
    }

    public void resizeFromHandle(int handleIndex, double dx, double dy) {
        double newX = x, newY = y;
        double newWidth = width, newHeight = height;

        switch (handleIndex) {
            case 0: newX += dx; newY += dy; newWidth -= dx; newHeight -= dy; break;
            case 1: newY += dy; newHeight -= dy; break;
            case 2: newY += dy; newWidth += dx; newHeight -= dy; break;
            case 3: newX += dx; newWidth -= dx; break;
            case 4: newWidth += dx; break;
            case 5: newX += dx; newWidth -= dx; newHeight += dy; break;
            case 6: newHeight += dy; break;
            case 7: newWidth += dx; newHeight += dy; break;
        }

        // Проверяем инверсию
        if (newWidth < 0) {
            newWidth = -newWidth;
            newX -= newWidth;  // Перемещаем x влево, чтобы сохранить позицию угла
        }
        if (newHeight < 0) {
            newHeight = -newHeight;
            newY -= newHeight;  // Перемещаем y вверх
        }

        // Устанавливаем новые размеры и позицию
        setX(newX);
        setY(newY);
        setWidth(Math.max(newWidth, HANDLE_SIZE * 2));
        setHeight(Math.max(newHeight, HANDLE_SIZE * 2));

        notifyObservers();
    }

    public void move(double dx, double dy) {

        setX( getX() + dx );
        setY( getY() + dy );
        notifyObservers();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        notifyObservers();
    }



    public boolean isSelected() {
        return selected;
    }

    public abstract void draw(GraphicsContext gc);
    public abstract boolean contains(double x, double y);

    public void chainColor(Color currentColor) {
        this.color = currentColor;
        notifyObservers();
    }

    public String save(){
        return x + " " + y + " " + width + " " + height + " " + color.toString() + " " + strokeWeight;

    }
}