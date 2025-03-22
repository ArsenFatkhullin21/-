package ru.arsen.oop4withobserver.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.arsen.oop4withobserver.observer.ShapeObserver;

import java.util.ArrayList;
import java.util.List;

public class ShapeGroup extends Shape {
    private final List<Shape> shapes = new ArrayList<>();


    public  ShapeGroup(){
    }

    public ShapeGroup(double x, double y, double width, double height, Color color, int strokeWidht) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height =height;
    }


    public List<Shape> getShapes() {
        return shapes;
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
        shape.addObserver(s -> notifyObservers());
        recalculateBounds();
    }

    public void removeShape(Shape shape) {
        shapes.remove(shape);
        recalculateBounds();
    }

    private void recalculateBounds() {
        if (shapes.isEmpty()) return;

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (Shape shape : shapes) {
            minX = Math.min(minX, shape.getX());
            minY = Math.min(minY, shape.getY());
            maxX = Math.max(maxX, shape.getX() + shape.getWidth());
            maxY = Math.max(maxY, shape.getY() + shape.getHeight());
        }

        this.x = minX;
        this.y = minY;
        this.width = maxX - minX;
        this.height = maxY - minY;
    }

    @Override
    public void draw(GraphicsContext gc) {
        for (Shape shape : shapes) {
            shape.draw(gc);
        }
        if (selected) {
            drawResizeHandles(gc);
        }
    }

    @Override
    public boolean contains(double x, double y) {
        for (Shape shape : shapes) {
            if (shape.contains(x, y)) {
//                notifyObservers();
                return true;
            }
        }
        return false;
    }

    @Override
    public void move(double dx, double dy) {

        setX( getX() + dx );
        setY( getY() + dy );
        for (Shape shape : shapes) {
            shape.move(dx, dy);

        }
        notifyObservers();
//        recalculateBounds();

    }

    @Override
    public void resizeFromHandle(int handleIndex, double dx, double dy) {
        double prevX = this.x;
        double prevY = this.y;
        double prevWidth = this.width;
        double prevHeight = this.height;

        super.resizeFromHandle(handleIndex, dx, dy);

        double widthScale = prevWidth == 0 ? 1 : this.width / prevWidth;
        double heightScale = prevHeight == 0 ? 1 : this.height / prevHeight;

        for (Shape shape : shapes) {
            if (shape instanceof ShapeGroup) {
                shape.resizeFromHandle(handleIndex, dx, dy);
            } else {
                double newX = this.x + (shape.getX() - prevX) * widthScale;
                double newY = this.y + (shape.getY() - prevY) * heightScale;
                double newWidth = shape.getWidth() * widthScale;
                double newHeight = shape.getHeight() * heightScale;

                shape.setX(newX);
                shape.setY(newY);
                shape.setWidth(newWidth);
                shape.setHeight(newHeight);
            }
        }
        notifyObservers();
    }

    @Override
    public void chainColor(Color color) {
        for (Shape shape : shapes) {
            shape.setColor(color);
        }
        notifyObservers();
    }

    @Override
    public String save() {
        for (Shape shape : shapes) {
            return shape.save();
        }
        return null;
    }
}
